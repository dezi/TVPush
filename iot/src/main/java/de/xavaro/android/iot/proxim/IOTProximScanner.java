package de.xavaro.android.iot.proxim;

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
import android.os.ParcelUuid;
import android.os.Build;
import android.util.SparseArray;
import android.util.Log;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

@SuppressWarnings({"WeakerAccess", "unused"})
public class IOTProximScanner
{
    private static final String LOGTAG = IOTProximScanner.class.getSimpleName();

    //
    // adb shell setprop log.tag.ScanRecord WARN
    // adb shell setprop log.tag.ScanRecord VERBOSE
    //

    public static void startService(Context appcontext)
    {
        if ((IOT.instance != null) && (IOT.instance.proximScanner == null))
        {
            IOT.instance.proximScanner = new IOTProximScanner(appcontext);

            IOT.instance.proximScanner.startLEScanner();

            //IOT.instance.proximScanner.startReceiver();
            //IOT.instance.proximScanner.startDiscovery();
        }
    }

    public static void stopService()
    {
        if ((IOT.instance != null) && (IOT.instance.proximScanner != null))
        {
            IOT.instance.proximScanner.stopLEScanner();

            //IOT.instance.proximScanner.stopDiscovery();
            //IOT.instance.proximScanner.stopReceiver();

            IOT.instance.proximScanner = null;
        }
    }

    private Context context;
    private String ownDeviceMac;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;

    private final Map<String, Long> lastUpdates = new HashMap<>();

    public IOTProximScanner(Context context)
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
        if ((adapter != null) &&  adapter.isDiscovering())
        {
            adapter.cancelDiscovery();

            Log.d(LOGTAG, "startDiscovery: discovery started.");
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

                Log.d(LOGTAG, "stopScan: scanner stopped.");
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

            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(LOGTAG, "onReceive: found"
                        + " mac=" + device.getAddress()
                        + " name=" + device.getName()
                );
            }
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
            buildEddyDev(result, eddystone);

            return;
        }

        byte[] bytes = result.getScanRecord().getManufacturerSpecificData(IOTProxim.IOT_MANUFACTURER_ID);

        if (bytes != null)
        {
            evalIOTAdver(result, bytes);

            return;
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

                if ((vendor == 117)
                        && (result.getDevice() != null)
                        && (result.getDevice().getName() != null)
                        && (result.getDevice().getName().startsWith("[TV]")))
                {
                    buildBeacDev(result, vendor, manData);

                    return;
                }

                if (vendor == 301)
                {
                    buildBeacDev(result, vendor, manData);

                    return;
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

        Log.d(LOGTAG, "evaluateScan: ALT"
                + " rssi=" + result.getRssi()
                + " addr=" + result.getDevice().getAddress()
                + " vend=" + Simple.padZero(vendor, 4)
                + " name=" + result.getDevice().getName()
                + " uuid=" + serviceUuid
                + " data=" + serviceDataUuid
                + " scan=" + result.getScanRecord()
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void evalIOTAdver(ScanResult result, byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        byte type = bb.get();
        byte plev = bb.get();

        String display = null;

        if ((type == IOTProxim.ADVERTISE_GPS_FINE)
                || (type == IOTProxim.ADVERTISE_GPS_COARSE))
        {
            double lat = bb.getDouble();
            double lon = bb.getDouble();

            display = lat + " - " + lon;
        }

        if ((type == IOTProxim.ADVERTISE_IOT_HUMAN)
                || (type == IOTProxim.ADVERTISE_IOT_DOMAIN)
                || (type == IOTProxim.ADVERTISE_IOT_DEVICE)
                || (type == IOTProxim.ADVERTISE_IOT_LOCATION))
        {
            long msb = bb.getLong();
            long lsb = bb.getLong();

            String uuid = (new UUID(msb, lsb)).toString();

            display = uuid;
        }

        Log.d(LOGTAG, "evalIOTAdver: IOT"
                + " rssi=" + result.getRssi()
                + " addr=" + result.getDevice().getAddress()
                + " vend=" + Simple.padZero(IOTProxim.IOT_MANUFACTURER_ID, 4)
                + " type=" + IOTProxim.getAdvertiseType(type)
                + " disp=" + display
                + " self=" + IOT.device.uuid
        );
    }

    @SuppressWarnings("UnusedAssignment")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildEddyDev(ScanResult result, byte[] eddystone)
    {
        if (eddystone[0] != 0x10) return;

        //noinspection unused
        byte txp = eddystone[1];
        byte urs = eddystone[2];

        String urlstart = "";

        if (urs == 0x00) urlstart = "http://www.";
        if (urs == 0x01) urlstart = "https://www.";
        if (urs == 0x02) urlstart = "http://";
        if (urs == 0x03) urlstart = "https://";

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

        String name = result.getDevice().getName() + "@" + url;
        String macAddr = result.getDevice().getAddress();
        String uuid = IOTSimple.hmacSha1UUID(name, macAddr);

        if (! shouldUpdate(uuid)) return;

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
                + " rssi=" + result.getRssi()
                + " addr=" + macAddr
                + " vend=" + "0000"
                + " type=" + IOTDevice.TYPE_BEACON
                + " uuid=" + uuid
                + " name=" + name
        );

        JSONObject beacondev = new JSONObject();

        Json.put(beacondev, "uuid", uuid);
        Json.put(beacondev, "type", IOTDevice.TYPE_BEACON);
        Json.put(beacondev, "did", url);
        Json.put(beacondev, "name", url);
        Json.put(beacondev, "nick", url);
        Json.put(beacondev, "model", name);
        Json.put(beacondev, "macaddr", macAddr);
        Json.put(beacondev, "driver", "iot");
        Json.put(beacondev, "location", Simple.getConnectedWifiName());

        Json.put(beacondev, "capabilities", Json.jsonArrayFromSeparatedString(caps, "\\|"));

        IOTDevices.addEntry(new IOTDevice(beacondev), false);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);

        IOTStatusses.addEntry(new IOTStatus(status), false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildBeacDev(ScanResult result, int vendor, byte[] bytes)
    {
        String name = result.getDevice().getName();
        String macAddr = result.getDevice().getAddress();
        String uuid = IOTSimple.hmacSha1UUID(name, macAddr);

        if (! shouldUpdate(uuid)) return;

        String caps = "beacon|sonytv|fixed|stupid|gpsfine";

        Log.d(LOGTAG, "buildBeacDev: ALT"
                + " rssi=" + result.getRssi()
                + " addr=" + macAddr
                + " vend=" + Simple.padZero(vendor, 4)
                + " type=" + IOTDevice.TYPE_BEACON
                + " uuid=" + uuid
                + " name=" + name
        );

        JSONObject beacondev = new JSONObject();

        Json.put(beacondev, "uuid", uuid);
        Json.put(beacondev, "type", IOTDevice.TYPE_BEACON);
        Json.put(beacondev, "name", name);
        Json.put(beacondev, "nick", name);
        Json.put(beacondev, "vendor", IOTProxim.getAdvertiseVendor(vendor));
        Json.put(beacondev, "macaddr", macAddr);
        Json.put(beacondev, "driver", "iot");
        Json.put(beacondev, "location", Simple.getConnectedWifiName());

        Json.put(beacondev, "capabilities", Json.jsonArrayFromSeparatedString(caps, "\\|"));

        IOTDevices.addEntry(new IOTDevice(beacondev), false);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);

        IOTStatusses.addEntry(new IOTStatus(status), false);
    }

    private boolean shouldUpdate(String uuid)
    {
        Long lastUpdate = Simple.getMapLong(lastUpdates, uuid);

        if ((lastUpdate == null) || ((System.currentTimeMillis() - lastUpdate) >= (30 * 1000)))
        {
            lastUpdates.put(uuid, System.currentTimeMillis());

            return true;
        }

        return false;
    }
}
