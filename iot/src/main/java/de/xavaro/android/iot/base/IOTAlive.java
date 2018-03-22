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
    private final static Map<String, Long> alivesNetwork = new HashMap<>();

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

    public static void setAliveStatus(String uuid)
    {
        Log.d(LOGTAG, "setAliveStatus: uuid=" + uuid);

        synchronized (alivesStatus)
        {
            alivesStatus.put(uuid, System.currentTimeMillis());
        }
    }

    @Nullable
    public static Long getLastStatus(String uuid)
    {
        synchronized (alivesStatus)
        {
            return Simple.getMapLong(alivesStatus, uuid);
        }
    }

    public static void setAliveNetwork(String addr)
    {
        Log.d(LOGTAG, "setAliveNetwork: addr=" + addr);

        synchronized (alivesNetwork)
        {
            alivesNetwork.put(addr, System.currentTimeMillis());
        }
    }

    @Nullable
    public static Long getLastPing(String addr)
    {
        synchronized (alivesNetwork)
        {
            return Simple.getMapLong(alivesNetwork, addr);
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

                if (status.ipaddr != null)
                {
                    performPing(uuid, status);
                }

                performStatus(uuid);
            }

            Log.d(LOGTAG, "runner: finished.");
        }
    };

    private static void performPing(String uuid, IOTStatus status)
    {
        String ipaddr = status.ipaddr;

        Long lastPing = null;

        synchronized (alivesNetwork)
        {
            lastPing = Simple.getMapLong(alivesNetwork, ipaddr);
        }

        if ((lastPing != null) && ((System.currentTimeMillis() - lastPing) < (10 * 1000)))
        {
            return;
        }

        try
        {
            Process ping = Runtime.getRuntime().exec("ping -c 1 -W 2 " + ipaddr);
            if ((ping.waitFor() == 0)) setAliveNetwork(ipaddr);
        }
        catch (Exception ignore)
        {
        }
    }

    private static void performStatus(String uuid)
    {
        Long lastStat = null;

        synchronized (alivesStatus)
        {
            lastStat = Simple.getMapLong(alivesStatus, uuid);
        }

        if ((lastStat != null) && ((System.currentTimeMillis() - lastStat) < (10 * 1000)))
        {
            return;
        }

        IOTDevice device = IOTDevices.getEntry(uuid);
        if (device == null) return;

        if (! device.driver.equals("tpl")) return;

        IOT.instance.onDeviceStatusRequest(device.toJson());

    }
}