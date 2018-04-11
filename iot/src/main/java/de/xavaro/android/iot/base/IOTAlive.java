package de.xavaro.android.iot.base;

import android.support.annotation.Nullable;

import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.simple.Json;

public class IOTAlive
{
    private final static String LOGTAG = IOTAlive.class.getSimpleName();

    public static void startService()
    {
        if ((IOT.instance != null) && (IOT.instance.alive == null))
        {
            IOT.instance.alive = new IOTAlive();
            IOT.instance.alive.startThread();
        }
    }

    public static void stopService()
    {
        if ((IOT.instance != null) && (IOT.instance.alive != null))
        {
            IOT.instance.alive.stopThread();
            IOT.instance.alive = null;
        }
    }

    private final Map<String, Long> alivesStatus = new HashMap<>();
    private final Map<String, Long> alivesNetwork = new HashMap<>();
    private final Map<String, Long> alivesRequest = new HashMap<>();

    private Thread aliveThread;

    private void startThread()
    {
        if (aliveThread == null)
        {
            aliveThread = new Thread(aliveRunnable);
            aliveThread.start();
        }
    }

    private void stopThread()
    {
        if (aliveThread != null)
        {
            aliveThread.interrupt();
            aliveThread = null;
        }
    }

    private final Runnable aliveRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "aliveRunnable: start.");

            int index = 0;
            JSONArray list = null;

            while (aliveThread != null)
            {
                Simple.sleep(40);

                if ((list == null) || (index >= list.length()))
                {
                    index = 0;
                    list = IOTDevice.list.getUUIDList();
                }

                String uuid = Json.getString(list, index++);
                if (uuid == null) continue;

                IOTStatus status = IOTStatus.list.getEntry(uuid);
                if (status == null) continue;

                if (status.ipaddr != null) performPing(uuid, status.ipaddr);

                performStatus(uuid);
            }

            Log.d(LOGTAG, "aliveRunnable: finished.");
        }
    };

    private void performPing(String uuid, String ipaddr)
    {
        Long lastRequest = getRequested(ipaddr);

        int secs = new Random().nextInt(6) + 6;

        if ((lastRequest != null) && ((System.currentTimeMillis() - lastRequest) < (secs * 1000)))
        {
            return;
        }

        setRequested(ipaddr);

        try
        {
            Process ping = Runtime.getRuntime().exec("ping -c 1 -W 2 " + ipaddr);
            if ((ping.waitFor() == 0)) setAliveNetwork(uuid);
        }
        catch (Exception ignore)
        {
        }
    }

    private void performStatus(String uuid)
    {
        Long lastRequest = getRequested(uuid);

        int secs = new Random().nextInt(6) + 6;

        if ((lastRequest != null) && ((System.currentTimeMillis() - lastRequest) < (secs * 1000)))
        {
            return;
        }

        setRequested(uuid);

        IOTDevice device = IOTDevice.list.getEntry(uuid);
        if (device == null) return;

        IOT.instance.onDeviceStatusRequest(device.toJson());
    }

    @Nullable
    private Long getRequested(String tag)
    {
        synchronized (alivesRequest)
        {
            return Simple.getMapLong(alivesRequest, tag);
        }
    }

    private void setRequested(String tag)
    {
        synchronized (alivesRequest)
        {
            alivesRequest.put(tag, System.currentTimeMillis());
        }
    }

    public void setAliveStatus(String uuid)
    {
        synchronized (alivesStatus)
        {
            alivesStatus.put(uuid, System.currentTimeMillis());
        }
    }

    @Nullable
    public Long getAliveStatus(String uuid)
    {
        synchronized (alivesStatus)
        {
            return Simple.getMapLong(alivesStatus, uuid);
        }
    }

    public void setAliveNetwork(String uuid)
    {
        synchronized (alivesNetwork)
        {
            alivesNetwork.put(uuid, System.currentTimeMillis());
        }
    }

    @Nullable
    public Long getAliveNetwork(String uuid)
    {
        synchronized (alivesNetwork)
        {
            return Simple.getMapLong(alivesNetwork, uuid);
        }
    }
}