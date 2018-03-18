package de.xavaro.android.iot.proxim;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONObject;

import java.nio.ByteBuffer;
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
    private static final String LOGTAG = IOTProximScanner.class.getName();

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

            if (Simple.isTV())
            {
                IOT.instance.proximScanner.startReceiver();
                IOT.instance.proximScanner.startDiscovery();
            }
        }
    }

    public static void stopService()
    {
        if ((IOT.instance != null) && (IOT.instance.proximScanner != null))
        {
            IOT.instance.proximScanner.stopLEScanner();

            if (Simple.isTV())
            {
                IOT.instance.proximScanner.stopDiscovery();
                IOT.instance.proximScanner.stopReceiver();
            }

            IOT.instance.proximScanner = null;
        }
    }

    private Context context;
    private String ownDeviceMac;
    private BluetoothLeScanner scanner;

    public IOTProximScanner(Context contenxt)
    {
        this.context = contenxt;
    }

    public void startReceiver()
    {
        IntentFilter filterStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(receiver, filterStarted);

        IntentFilter filterFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filterFinished);

        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filterFound);
    }

    public void stopReceiver()
    {
        context.unregisterReceiver(receiver);
    }

    public void startDiscovery()
    {
        BluetoothAdapter adapter = Simple.getBTAdapter();

        if (adapter != null)
        {
            adapter.startDiscovery();

            Log.d(LOGTAG, "startDiscovery: discovery started.");
        }
    }

    public void stopDiscovery()
    {
        BluetoothAdapter adapter = Simple.getBTAdapter();

        if ((adapter != null) &&  adapter.isDiscovering())
        {
            adapter.cancelDiscovery();

            Log.d(LOGTAG, "startDiscovery: discovery started.");
        }
    }

    public void startLEScanner()
    {
        if (scanner == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                BluetoothAdapter adapter = Simple.getBTAdapter();

                if (adapter != null)
                {
                    scanner = adapter.getBluetoothLeScanner();
                    scanner.startScan(scanCallback);

                    Log.d(LOGTAG, "startLEScanner: scanner started.");
                }
            }
        }
    }

    public void stopLEScanner()
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

    private final ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            evaluateScan(callbackType, result);
        }
    };

    private void evaluateScan(int callbackType, ScanResult result)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (result.getScanRecord() == null) return;

            byte[] eddystone = result.getScanRecord().getServiceData(ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB"));

            if (eddystone != null)
            {
                buildEddyDev(result, eddystone);

                return;
            }

            byte[] bytes = result.getScanRecord().getManufacturerSpecificData(IOTProxim.IOT_MANUFACTURER_ID);

            if (bytes != null)
            {
                evaluateIOTAdvertisement(result, bytes);

                return;
            }

            SparseArray<byte[]> bytbyt = result.getScanRecord().getManufacturerSpecificData();

            if (bytbyt.size() > 0)
            {
                for (int inx = 0; inx < bytbyt.size(); inx++)
                {
                    int vendor = bytbyt.keyAt(inx);

                    Log.d(LOGTAG, "evaluateScan: ALT"
                            + " rssi=" + result.getRssi()
                            + " addr=" + result.getDevice().getAddress()
                            + " vend=" + Simple.padZero(vendor, 3)
                            + " name=" + result.getDevice().getName()
                            + " vend=" + IOTProxim.getAdvertiseVendor(vendor)
                    );

                    if (vendor == 301)
                    {
                        buildSonyDev(result, vendor, bytbyt.get(vendor));
                    }
                }
            }
            else
            {
                Log.d(LOGTAG, "evaluateScan: ALT"
                        + " rssi=" + result.getRssi()
                        + " addr=" + result.getDevice().getAddress()
                        + " vend=" + "???"
                        + " name=" + result.getDevice().getName()
                );
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void evaluateIOTAdvertisement(ScanResult result, byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        byte type = bb.get();
        byte plev = bb.get();

        boolean ignore = true;
        String display;

        if ((type == IOTProxim.ADVERTISE_GPS_FINE) || (type == IOTProxim.ADVERTISE_GPS_COARSE))
        {
            double lat = bb.getDouble();
            double lon = bb.getDouble();

            display = lat + " - " + lon;
        }
        else
        {
            long msb = bb.getLong();
            long lsb = bb.getLong();

            String uuid = (new UUID(msb, lsb)).toString();

            display = uuid;

            if (ownDeviceMac == null)
            {
                if ((IOT.device != null) && (IOT.device.uuid.equals(uuid)))
                {
                    ownDeviceMac = result.getDevice().getAddress();
                }
            }
            else
            {
                ignore = ! ownDeviceMac.equals(result.getDevice().getAddress());
            }
        }

        if (ignore) return;

        Log.d(LOGTAG, "evaluateScan: IOT"
                + " rssi=" + result.getRssi()
                + " addr=" + result.getDevice().getAddress()
                + " plev=" + plev
                + " type=" + IOTProxim.getAdvertiseType(type)
                + " disp=" + display
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

        String name = result.getDevice().getName();
        String macAddr = result.getDevice().getAddress();
        String uuid = IOTSimple.hmacSha1UUID(name, macAddr);

        String caps = "beacon|eddystone|fixed|stupid";

        if (name.equalsIgnoreCase("iBKS Plus"))
        {
            //
            // Super long range Bodomann beacon. Not usefull
            // for fine location. Usefull for premises
            // area location.
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
                + " vend=" + "???"
                + " name=" + name
                + " url=" + url
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
    private void buildSonyDev(ScanResult result, int vendor, byte[] bytes)
    {
        String name = result.getDevice().getName();
        String macAddr = result.getDevice().getAddress();
        String uuid = IOTSimple.hmacSha1UUID(name, macAddr);

        String caps = "beacon|sonytv|fixed|stupid|gpsfine";

        Log.d(LOGTAG, "buildSonyDev: ALT"
                + " rssi=" + result.getRssi()
                + " addr=" + macAddr
                + " vend=" + Simple.padZero(vendor, 3)
                + " name=" + name
                + " uuid=" + uuid
        );

        JSONObject beacondev = new JSONObject();

        Json.put(beacondev, "uuid", uuid);
        Json.put(beacondev, "type", IOTDevice.TYPE_BEACON);
        Json.put(beacondev, "name", name);
        Json.put(beacondev, "nick", name);
        Json.put(beacondev, "macaddr", macAddr);
        Json.put(beacondev, "driver", "iot");
        Json.put(beacondev, "location", Simple.getConnectedWifiName());

        Json.put(beacondev, "capabilities", Json.jsonArrayFromSeparatedString(caps, "\\|"));

        IOTDevices.addEntry(new IOTDevice(beacondev), false);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);

        IOTStatusses.addEntry(new IOTStatus(status), false);
    }
}
