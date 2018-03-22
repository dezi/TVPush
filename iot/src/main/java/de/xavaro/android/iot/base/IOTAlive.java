package de.xavaro.android.iot.base;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class IOTAlive
{
    private final static String LOGTAG = IOTAlive.class.getSimpleName();

    private final static Map<String, Long> alivesStatus = new HashMap<>();
    private final static Map<String, Long> alivesRequest = new HashMap<>();

    private static Thread worker;

    public static void startService()
    {
        if (worker == null)
        {
            worker = new Thread(runner);
            worker.start();
        }
    }

    public static void stopService()
    {
        if (worker != null)
        {
            worker.interrupt();
            worker = null;
        }
    }

    public static void setAlive(String tag)
    {
        synchronized (alivesStatus)
        {
            alivesStatus.put(tag, System.currentTimeMillis());
        }
    }

    @Nullable
    public static Long getAlive(String tag)
    {
        synchronized (alivesStatus)
        {
            return Simple.getMapLong(alivesStatus, tag);
        }
    }

    public static void setRequested(String tag)
    {
        synchronized (alivesRequest)
        {
            alivesRequest.put(tag, System.currentTimeMillis());
        }
    }

    @Nullable
    public static Long getRequested(String tag)
    {
        synchronized (alivesRequest)
        {
            return Simple.getMapLong(alivesRequest, tag);
        }
    }

    private final static Runnable runner = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "runner: start.");

            int index = 0;
            JSONArray list = null;

            while (worker != null)
            {
                Simple.sleep(40);

                if ((list == null) || (index >= list.length()))
                {
                    index = 0;
                    list = IOTDevices.instance.getListUUIDs();
                }

                String uuid = Json.getString(list, index++);
                if (uuid == null) continue;

                IOTStatus status = IOTStatusses.getEntry(uuid);
                if (status == null) continue;

                if (status.ipaddr != null) performPing(status.ipaddr);

                performStatus(uuid);
            }

            Log.d(LOGTAG, "runner: finished.");
        }
    };

    private static void performPing(String ipaddr)
    {
        Long lastRequest = getRequested(ipaddr);

        if ((lastRequest != null) && ((System.currentTimeMillis() - lastRequest) < (10 * 1000)))
        {
            return;
        }

        setRequested(ipaddr);

        try
        {
            Process ping = Runtime.getRuntime().exec("ping -c 1 -W 2 " + ipaddr);
            if ((ping.waitFor() == 0)) setAlive(ipaddr);
        }
        catch (Exception ignore)
        {
        }
    }

    private static void performStatus(String uuid)
    {
        Long lastRequest = getRequested(uuid);

        if ((lastRequest != null) && ((System.currentTimeMillis() - lastRequest) < (10 * 1000)))
        {
            return;
        }

        setRequested(uuid);

        IOTDevice device = IOTDevices.getEntry(uuid);
        if (device == null) return;

        if (! device.driver.equals("tpl")) return;

        IOT.instance.onDeviceStatusRequest(device.toJson());
    }
}