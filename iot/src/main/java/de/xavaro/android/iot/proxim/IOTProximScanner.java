package de.xavaro.android.iot.proxim;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.simple.Simple;

public class IOTProximScanner
{
    private static final String LOGTAG = IOTProximScanner.class.getName();

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
            if (result.getDevice().getName() != null)
            {
                //
                // Foreign device with name.
                //

                Log.d(LOGTAG, "evalScan: ALT"
                        + " rssi=" + result.getRssi()
                        + " name=" + result.getDevice().getName()
                );

                return;
            }

            if (result.getScanRecord() == null) return;

            SparseArray<byte[]> bytbyt = result.getScanRecord().getManufacturerSpecificData();

            for (int inx = 0; inx < bytbyt.size(); inx++)
            {
                int vendor = bytbyt.keyAt(inx);
                byte[] bytes = bytbyt.get(vendor);

                if (vendor != IOTProxim.MANUFACTURER_ID)
                {
                    Log.d(LOGTAG, "evalScan: ALT"
                            + " rssi=" + result.getRssi()
                            + " vend=" + vendor
                            + " name=" + IOTProxim.getAdvertiseVendor(vendor)
                    );
                }
                else
                {
                    byte type = bytes[0];
                    byte plev = bytes[1];

                    Log.d(LOGTAG, "evalScan: IOT"
                            + " rssi=" + result.getRssi()
                            + " plev=" + plev
                            + " type=" + IOTProxim.getAdvertiseType(type)
                    );
                }
            }
        }
    }
}
