package de.xavaro.android.bcn.beacon;

import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.os.ParcelUuid;
import android.os.Build;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xavaro.android.bcn.base.BCN;

import de.xavaro.android.bcn.simple.Simple;
import de.xavaro.android.bcn.simple.Json;
import de.xavaro.android.bcn.simple.Log;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BCNScanner
{
    private static final String LOGTAG = BCNScanner.class.getSimpleName();

    //
    // adb shell setprop log.tag.TextView WARN
    // adb shell setprop log.tag.ScanRecord WARN
    // adb shell setprop log.tag.ScanRecord VERBOSE
    //

    public static void startService(Context appcontext)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if ((BCN.instance != null) && (BCN.instance.scanner == null))
            {
                BCN.instance.scanner = new BCNScanner(appcontext);

                BCN.instance.scanner.startLEScanner();

                /*
                if (Simple.isSony())
                {
                    //
                    // Fucked up BLE daemons by Sony.
                    // They tend to work better, if a
                    // legacy scan is also requested.
                    //

                    IOT.instance.proximScanner.startReceiver();
                    IOT.instance.proximScanner.startDiscovery();
                }
                */
            }
        }
    }

    public static void stopService()
    {
        if ((BCN.instance != null) && (BCN.instance.scanner != null))
        {
            BCN.instance.scanner.stopLEScanner();

            BCN.instance.scanner.stopDiscovery();
            BCN.instance.scanner.stopReceiver();

            BCN.instance.scanner = null;
        }
    }

    private Context context;
    private String ownDeviceMac;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;

    private final Map<String, Long> lastUpdates = new HashMap<>();

    public BCNScanner(Context context)
    {
        this.context = context;
        this.adapter = Simple.getBTAdapter();
    }

    private void startReceiver()
    {
        IntentFilter filterStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(receiver, filterStarted);

        IntentFilter filterFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filterFinished);

        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filterFound);
    }

    private void stopReceiver()
    {
        context.unregisterReceiver(receiver);
    }

    private void startDiscovery()
    {
        if (adapter != null)
        {
            adapter.startDiscovery();

            Log.d(LOGTAG, "startDiscovery: discovery started.");
        }
    }

    private void stopDiscovery()
    {
        if ((adapter != null) && adapter.isDiscovering())
        {
            adapter.cancelDiscovery();

            Log.d(LOGTAG, "startDiscovery: discovery stopped.");
        }
    }

    private void startLEScanner()
    {
        if ((scanner == null) && (adapter != null))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                scanner = adapter.getBluetoothLeScanner();
                scanner.startScan(scanCallback);

                Log.d(LOGTAG, "startLEScanner: scanner started.");
            }
        }
    }

    private void stopLEScanner()
    {
        if (scanner != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                scanner.stopScan(scanCallback);
                scanner = null;

                Log.d(LOGTAG, "stopLEScanner: scanner stopped.");
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Log.d(LOGTAG, "onReceive: started.");
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Log.d(LOGTAG, "onReceive: finished.");

                startDiscovery();
            }

            /*
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(LOGTAG, "onReceive: found"
                        + " mac=" + device.getAddress()
                        + " name=" + device.getName()
                        + " intent=" + intent.getExtras()
                );
            }
            */
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private final ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            evaluateScan(callbackType, result);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void evaluateScan(int callbackType, ScanResult result)
    {
        if (result.getScanRecord() == null) return;

        //region Well known devices.

        byte[] eddystone = result.getScanRecord().getServiceData(ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB"));

        if (eddystone != null)
        {
            if (buildEddyDev(result, eddystone)) return;
        }

        byte[] bytes = result.getScanRecord().getManufacturerSpecificData(BCNDefs.MANUFACTURER_IOT);

        if (bytes != null)
        {
            if (evalIOTAdver(result, bytes)) return;
        }

        //endregion Well known devices.

        int vendor = 0;

        SparseArray<byte[]> manufacturerData = result.getScanRecord().getManufacturerSpecificData();

        if (manufacturerData.size() > 0)
        {
            for (int inx = 0; inx < manufacturerData.size(); inx++)
            {
                //
                // Abuse certain devices as beacons.
                //

                vendor = manufacturerData.keyAt(inx);

                byte[] manData = manufacturerData.get(vendor);

                if (vendor == BCNDefs.MANUFACTURER_APPLE)
                {
                    if (buildApplDev(result, vendor, manData)) return;
                }

                if ((vendor == BCNDefs.MANUFACTURER_SAMSUNG)
                        && (result.getDevice() != null)
                        && (result.getDevice().getName() != null)
                        && (result.getDevice().getName().startsWith("[TV]")))
                {
                    if (buildBeacDev(result, vendor, manData)) return;
                }

                if (vendor == BCNDefs.MANUFACTURER_GOOGLE)
                {
                    if (buildGoogDev(result, vendor, manData)) return;
                }

                if (vendor == BCNDefs.MANUFACTURER_SONY)
                {
                    if (buildBeacDev(result, vendor, manData)) return;
                }
            }
        }

        ParcelUuid serviceUuid = null;
        List<ParcelUuid> serviceUuids = result.getScanRecord().getServiceUuids();

        if ((serviceUuids != null) && (serviceUuids.size() > 0))
        {
            for (ParcelUuid uuid : serviceUuids)
            {
                serviceUuid = uuid;
            }
        }

        ParcelUuid serviceDataUuid = null;
        Map<ParcelUuid, byte[]> serviceDatas = result.getScanRecord().getServiceData();

        if ((serviceDatas != null) && (serviceDatas.size() > 0))
        {
            for (Map.Entry<ParcelUuid, byte[]> entry : serviceDatas.entrySet())
            {
                serviceDataUuid = entry.getKey();
            }
        }

        int rssi = result.getRssi();
        int txpo = -21;

        if ((result.getScanRecord() != null)
                && (result.getScanRecord().getTxPowerLevel() < 0)
                && (result.getScanRecord().getTxPowerLevel() > -100))
        {
            txpo = result.getScanRecord().getTxPowerLevel();
        }

        Log.d(LOGTAG, "evaluateScan: ALT"
                + " rssi=" + rssi
                + " txpo=" + txpo
                + " addr=" + result.getDevice().getAddress()
                + " vend=" + Simple.padZero(vendor, 4)
                + " name=" + result.getDevice().getName()
                + " uuid=" + serviceUuid
                + " data=" + serviceDataUuid
                + " scan=" + result.getScanRecord()
        );
    }

    @SuppressWarnings("SameReturnValue")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean evalIOTAdver(ScanResult result, byte[] bytes)
    {
        String macAddr = result.getDevice().getAddress();
        int rssi = result.getRssi();

        ByteBuffer bb = ByteBuffer.wrap(bytes);

        int type = bb.get();
        int txpo = bb.get();

        String display = null;

        if ((type == BCNDefs.ADVERTISE_GPS_FINE)
                || (type == BCNDefs.ADVERTISE_GPS_COARSE))
        {
            Double lat = bb.getDouble();
            Double lon = bb.getDouble();
            Double alt = (double) bb.getFloat();

            display = lat + " - " + lon + " - " + alt;

            String mode = (type == BCNDefs.ADVERTISE_GPS_FINE) ? "fine" : "coarse";

            JSONObject locmeasurement = new JSONObject();
            Json.put(locmeasurement, "akey", macAddr);
            Json.put(locmeasurement, "prov", "iotproxim");
            Json.put(locmeasurement, "time", System.currentTimeMillis());
            Json.put(locmeasurement, "mode", mode);
            Json.put(locmeasurement, "txpo", txpo);
            Json.put(locmeasurement, "rssi", rssi);
            Json.put(locmeasurement, "lat", lat);
            Json.put(locmeasurement, "lon", lon);
            Json.put(locmeasurement, "alt", alt);

            JSONObject measurement = new JSONObject();
            Json.put(measurement, "LOCMeasurement", locmeasurement);

            BCN.instance.onLocationMeasurement(measurement);
        }

        if ((type == BCNDefs.ADVERTISE_IOT_HUMAN)
                || (type == BCNDefs.ADVERTISE_IOT_DOMAIN)
                || (type == BCNDefs.ADVERTISE_IOT_DEVICE)
                || (type == BCNDefs.ADVERTISE_IOT_LOCATION))
        {
            long msb = bb.getLong();
            long lsb = bb.getLong();

            @SuppressWarnings("UnnecessaryLocalVariable")
            String uuid = (new UUID(msb, lsb)).toString();

            display = uuid;
        }

        if (type == BCNDefs.ADVERTISE_IOT_DEVNAME)
        {
            int devsize = bytes.length - 2;
            byte[] devbytes = new byte[devsize];

            bb.get(devbytes, 0, devbytes.length);

            display = new String(devbytes);
        }

        Log.d(LOGTAG, "evalIOTAdver: IOT"
                + " rssi=" + rssi
                + " txpo=" + txpo
                + " addr=" + result.getDevice().getAddress()
                + " vend=" + Simple.padZero(BCNDefs.MANUFACTURER_IOT, 4)
                + " type=" + BCNDefs.getAdvertiseType(type)
                + " disp=" + display
        );

        /*
        if ((type == BCNDefs.ADVERTISE_GPS_FINE)
                || (type == BCNDefs.ADVERTISE_GPS_COARSE))
        {
            //
            // Bootstrap location if not yet set.
            //

            if ((IOT.device != null)
                    && (IOT.device.hasCapability("fixed")
                    && (IOT.device.fixedLatCoarse == null)
                    && (IOT.device.fixedLonCoarse == null)
                    && (IOT.device.fixedAltCoarse == null)))
            {
                Double googlealt = IOTProximLocation.getAltitude(lat, lon);

                Log.d(LOGTAG, "evalIOTAdver: Bootstrap altitude googlealt=" + googlealt);

                if (googlealt != null) alt = googlealt;

                IOTDevice newDevice = new IOTDevice(IOT.device.uuid);

                newDevice.fixedLatCoarse = lat;
                newDevice.fixedLonCoarse = lon;
                newDevice.fixedAltCoarse = alt;

                Log.d(LOGTAG, "evalIOTAdver: Bootstrap location"
                    + " lat=" + newDevice.fixedLatCoarse
                    + " lon=" + newDevice.fixedLonCoarse
                    + " alt=" + newDevice.fixedAltCoarse
                );

                IOTDevice.list.addEntry(newDevice, false, true);
            }
        }
        */

        return true;
    }

    @SuppressWarnings("UnusedAssignment")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean buildEddyDev(ScanResult result, byte[] eddystone)
    {
        if (eddystone[0] != 0x10) return false;

        //noinspection unused
        int txpo = eddystone[1];
        byte urls = eddystone[2];

        String urlstart = "";

        if (urls == 0x00) urlstart = "http://www.";
        if (urls == 0x01) urlstart = "https://www.";
        if (urls == 0x02) urlstart = "http://";
        if (urls == 0x03) urlstart = "https://";

        String urlrest = Simple.getString(eddystone, 3, eddystone.length - 3);

        String url = urlstart + urlrest;

        //
        // Fuck dat.
        //

        char x;

        url = url.replace(Character.valueOf(x = 0x00).toString(), ".com/");
        url = url.replace(Character.valueOf(x = 0x01).toString(), ".org/");
        url = url.replace(Character.valueOf(x = 0x02).toString(), ".edu/");
        url = url.replace(Character.valueOf(x = 0x03).toString(), ".net/");
        url = url.replace(Character.valueOf(x = 0x04).toString(), ".info/");
        url = url.replace(Character.valueOf(x = 0x05).toString(), ".biz/");
        url = url.replace(Character.valueOf(x = 0x06).toString(), ".gov/");
        url = url.replace(Character.valueOf(x = 0x07).toString(), ".com");
        url = url.replace(Character.valueOf(x = 0x08).toString(), ".org");
        url = url.replace(Character.valueOf(x = 0x09).toString(), ".edu");
        url = url.replace(Character.valueOf(x = 0x0a).toString(), ".net");
        url = url.replace(Character.valueOf(x = 0x0b).toString(), ".info");
        url = url.replace(Character.valueOf(x = 0x0c).toString(), ".biz");
        url = url.replace(Character.valueOf(x = 0x0d).toString(), ".gov");

        String name = result.getDevice().getName();
        String macAddr = result.getDevice().getAddress();
        String uuid = Simple.hmacSha1UUID(url, macAddr);

        int rssi = result.getRssi();

        if (shouldUpdate(uuid))
        {
            String caps = "beacon|eddystone|fixed|stupid";

            if (name.equalsIgnoreCase("iBKS Plus"))
            {
                //
                // Super long range Bodomann beacon. Not usefull for
                // fine location. Usefull for domain area location.
                //
                // Elvis has entered/left the building.
                //

                caps += "|gpscoarse";
            }
            else
            {
                caps += "|gpsfine";
            }

            Log.d(LOGTAG, "buildEddyDev: EDY"
                    + " rssi=" + rssi
                    + " txpo=" + txpo
                    + " addr=" + macAddr
                    + " vend=" + "0000"
                    + " type=" + "beacon"
                    + " uuid=" + uuid
                    + " name=" + name
                    + " url=" + url
            );

            JSONObject beacondev = new JSONObject();

            Json.put(beacondev, "uuid", uuid);
            Json.put(beacondev, "type", "beacon");
            Json.put(beacondev, "did", url);
            Json.put(beacondev, "name", url);
            Json.put(beacondev, "model", name);
            Json.put(beacondev, "macaddr", macAddr);
            Json.put(beacondev, "driver", "bcn");
            Json.put(beacondev, "location", Simple.getConnectedWifiName());
            Json.put(beacondev, "capabilities", caps);

            BCN.instance.onDeviceFound(beacondev);

            JSONObject status = new JSONObject();

            Json.put(status, "uuid", uuid);
            Json.put(status, "txpower", txpo);
            Json.put(status, "macaddr", macAddr);

            BCN.instance.onDeviceStatus(status);
        }

        addLocationMeasurement(uuid, rssi, txpo, macAddr);

        BCN.instance.onThingAlive(uuid);

        return true;
    }

    private void addLocationMeasurement(String uuid, int rssi, int txpo, String macAddr)
    {
        JSONObject device = BCN.instance.onGetDeviceRequest(uuid);

        if ((device != null)
                && Json.has(device, "fixedLatFine")
                && Json.has(device, "fixedLonFine")
                && Json.has(device, "fixedAltFine"))
        {
            JSONObject locmeasurement = new JSONObject();
            Json.put(locmeasurement, "akey", macAddr);
            Json.put(locmeasurement, "prov", Json.getString(device, "type"));
            Json.put(locmeasurement, "time", System.currentTimeMillis());
            Json.put(locmeasurement, "mode", "fine");
            Json.put(locmeasurement, "txpo", txpo);
            Json.put(locmeasurement, "rssi", rssi);
            Json.put(locmeasurement, "lat", Json.getDouble(device, "fixedLatFine"));
            Json.put(locmeasurement, "lon", Json.getDouble(device, "fixedLonFine"));
            Json.put(locmeasurement, "alt", Json.getDouble(device, "fixedAltFine"));

            JSONObject measurement = new JSONObject();
            Json.put(measurement, "LOCMeasurement", locmeasurement);

            BCN.instance.onLocationMeasurement(measurement);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean buildBeacDev(ScanResult result, int vendor, byte[] bytes)
    {
        String name = result.getDevice().getName();

        if (name == null)
        {
            //301 => D8:0F:99:31:37:5C
            //Log.d(LOGTAG, "#########" + result.getScanRecord());

            return false;
        }

        String macAddr = result.getDevice().getAddress();
        String uuid = Simple.hmacSha1UUID(name, macAddr);

        int rssi = result.getRssi();
        int txpo = -20;

        if (shouldUpdate(uuid))
        {
            if ((result.getScanRecord() != null)
                    && (result.getScanRecord().getTxPowerLevel() < 0)
                    && (result.getScanRecord().getTxPowerLevel() > -100))
            {
                txpo = result.getScanRecord().getTxPowerLevel();
            }

            Log.d(LOGTAG, "buildBeacDev: ALT"
                    + " rssi=" + rssi
                    + " txpo=" + txpo
                    + " addr=" + macAddr
                    + " vend=" + Simple.padZero(vendor, 4)
                    + " type=" + "beacon"
                    + " uuid=" + uuid
                    + " name=" + name
            );

            String caps = "beacon|uuidbeacon|fixed|stupid|gpsfine";

            JSONObject beacondev = new JSONObject();

            Json.put(beacondev, "uuid", uuid);
            Json.put(beacondev, "type", "beacon");
            Json.put(beacondev, "name", name);
            Json.put(beacondev, "vendor", BCNDefs.getAdvertiseVendor(vendor));
            Json.put(beacondev, "macaddr", macAddr);
            Json.put(beacondev, "driver", "bcn");
            Json.put(beacondev, "location", Simple.getConnectedWifiName());
            Json.put(beacondev, "capabilities", caps);

            BCN.instance.onDeviceFound(beacondev);

            JSONObject status = new JSONObject();

            Json.put(status, "uuid", uuid);
            Json.put(status, "txpower", txpo);
            Json.put(status, "macaddr", macAddr);

            BCN.instance.onDeviceStatus(status);
        }

        addLocationMeasurement(uuid, rssi, txpo, macAddr);

        BCN.instance.onThingAlive(uuid);

        return true;
    }

    private boolean shouldUpdate(String uuid)
    {
        Long lastUpdate = Simple.getMapLong(lastUpdates, uuid);

        if ((lastUpdate == null) || ((System.currentTimeMillis() - lastUpdate) >= (5 * 1000)))
        {
            lastUpdates.put(uuid, System.currentTimeMillis());

            return true;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean buildApplDev(ScanResult result, int vendor, byte[] bytes)
    {
        String macAddr = result.getDevice().getAddress();
        String uuid = null;
        String type = null;
        String caps = null;
        String name = null;
        String model = null;

        int rssi = result.getRssi();
        int txpo = -1;

        if (bytes.length == 23)
        {
            //
            // Could be an Apple beacon.
            //

            if ((bytes[ 0 ] == 0x02) && (bytes[ 1 ] == 0x15))
            {
                int major = bytes[18] & 0xff;
                int minor = bytes[19] & 0xff;
                txpo = bytes[22];

                ByteBuffer bb = ByteBuffer.wrap(bytes, 2, 16);

                long msb = bb.getLong();
                long lsb = bb.getLong();

                if ((msb != 0) && (lsb != 0))
                {
                    uuid = (new UUID(msb, lsb)).toString();
                    name = uuid + " (" + major + "/" + minor + ")";
                    caps = "beacon|ibeacon|fixed|stupid|gpsfine";
                    type = "beacon";
                    model = "iBeacon";
                }
            }
        }
        else
        {
            if ((bytes.length > 2) && (result.getDevice().getName() != null))
            {
                byte dataType = bytes[ 0 ];
                byte restSize = bytes[ 1 ];

                if (dataType == 16)
                {
                    name = result.getDevice().getName();
                    txpo = -22;

                    if (name != null)
                    {
                        uuid = Simple.hmacSha1UUID(macAddr, name);
                        caps = "beacon|macbeacon|fixed|stupid|gpsfine";
                        type = "beacon";
                        model = "iMac";
                    }
                }
            }
        }

        if (uuid == null) return false;

        if (shouldUpdate(uuid))
        {
            Log.d(LOGTAG, "buildApplDev: ALT"
                    + " rssi=" + rssi
                    + " txpo=" + txpo
                    + " addr=" + macAddr
                    + " vend=" + Simple.padZero(vendor, 4)
                    + " type=" + type
                    + " uuid=" + uuid
                    + " name=" + name
            );

            JSONObject beacondev = new JSONObject();

            Json.put(beacondev, "uuid", uuid);
            Json.put(beacondev, "type", "beacon");
            Json.put(beacondev, "name", name);
            Json.put(beacondev, "model", model);
            Json.put(beacondev, "vendor", BCNDefs.getAdvertiseVendor(vendor));
            Json.put(beacondev, "macaddr", macAddr);
            Json.put(beacondev, "driver", "bcn");
            Json.put(beacondev, "location", Simple.getConnectedWifiName());
            Json.put(beacondev, "capabilities", caps);

            BCN.instance.onDeviceFound(beacondev);

            JSONObject status = new JSONObject();

            Json.put(status, "uuid", uuid);
            Json.put(status, "txpower", txpo);
            Json.put(status, "macaddr", macAddr);

            BCN.instance.onDeviceStatus(status);
        }

        addLocationMeasurement(uuid, rssi, txpo, macAddr);

        BCN.instance.onThingAlive(uuid);

        return true;
    }

    @SuppressWarnings("SameReturnValue")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean buildGoogDev(ScanResult result, int vendor, byte[] bytes)
    {
        return false;
    }
}
