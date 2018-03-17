package de.xavaro.android.iot.proxim;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.util.UUID;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.simple.Simple;

public class IOTProximScanner
{
    private static final String LOGTAG = IOTProximScanner.class.getName();

    //
    // adb shell setprop log.tag.ScanRecord WARN
    // adb shell setprop log.tag.ScanRecord VERBOSE
    //

    public static void startService()
    {
        if (IOT.instance == null) return;

        if (IOT.instance.proximScanner == null)
        {
            IOT.instance.proximScanner = new IOTProximScanner();
            IOT.instance.proximScanner.startScan();
        }
    }

    public BluetoothLeScanner scanner;
    public ScanCallback scanCallback;

    public void startScan()
    {
        if (scanCallback == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                BluetoothAdapter adapter = Simple.getBTAdapter();

                if (adapter != null)
                {
                    scanCallback = new ScanCallback()
                    {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result)
                        {
                            evalScan(callbackType, result);
                        }
                    };

                    scanner = adapter.getBluetoothLeScanner();
                    scanner.startScan(scanCallback);

                    Log.d(LOGTAG, "startScan: scanner started.");
                }
            }
        }
    }

    public void stopScan()
    {
        if (scanCallback != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                scanner.stopScan(scanCallback);
                scanCallback = null;
                scanner = null;

                Log.d(LOGTAG, "stopScan: scanner stopped.");
            }
        }
    }

    private void evalScan(int callbackType, ScanResult result)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (result.getScanRecord() == null)
            {
                Log.d(LOGTAG, "Nix sacnnnnn....");

                return;
            }

            byte[] eddystone = result.getScanRecord().getServiceData(ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB"));

            if ((eddystone != null) && (eddystone[0] == 0x10))
            {
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

                Log.d(LOGTAG, "evalScan: EDY"
                        + " rssi=" + result.getRssi()
                        + " addr=" + result.getDevice().getAddress()
                        + " name=" + result.getDevice().getName()
                        + " url=" + url
                );

                return;
            }

            if (result.getDevice().getName() != null)
            {
                //
                // Foreign device with name.
                //

                Log.d(LOGTAG, "evalScan: ALT"
                        + " rssi=" + result.getRssi()
                        + " addr=" + result.getDevice().getAddress()
                        + " name=" + result.getDevice().getName()
                );

                return;
            }

            byte[] bytes = result.getScanRecord().getManufacturerSpecificData(IOTProxim.IOT_MANUFACTURER_ID);

            if (bytes != null)
            {
                ByteBuffer bb = ByteBuffer.wrap(bytes);

                byte type = bb.get();
                byte plev = bb.get();

                String display = null;

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

                    display = (new UUID(msb, lsb)).toString();
                }

                Log.d(LOGTAG, "evalScan: IOT"
                        + " rssi=" + result.getRssi()
                        + " addr=" + result.getDevice().getAddress()
                        + " plev=" + plev
                        + " type=" + IOTProxim.getAdvertiseType(type)
                        + " disp=" + display
                );

                return;
            }

            SparseArray<byte[]> bytbyt = result.getScanRecord().getManufacturerSpecificData();

            for (int inx = 0; inx < bytbyt.size(); inx++)
            {
                int vendor = bytbyt.keyAt(inx);

                if (vendor != IOTProxim.IOT_MANUFACTURER_ID)
                {
                    Log.d(LOGTAG, "evalScan: ALT"
                            + " rssi=" + result.getRssi()
                            + " addr=" + result.getDevice().getAddress()
                            + " vend=" + vendor
                            + " name=" + IOTProxim.getAdvertiseVendor(vendor)
                    );
                }
            }
        }
    }
}
