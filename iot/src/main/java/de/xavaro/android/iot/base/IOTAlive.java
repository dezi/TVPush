package de.xavaro.android.iot.base;

import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevices;

public class IOTAlive
{
    private final static String LOGTAG = IOTAlive.class.getSimpleName();

    private final static Map<String, Long> alives = new HashMap<>();

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

    public static void setAlive(String addr)
    {
        Log.d(LOGTAG, "setAlive: addr=" + addr);

        synchronized (alives)
        {
            alives.put(addr, System.currentTimeMillis());
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
                if ((status == null) || (status.ipaddr == null)) continue;
                String ipaddr = status.ipaddr;

                Long lastPing = null;

                synchronized (alives)
                {
                    lastPing = Simple.getMapLong(alives, ipaddr);
                }

                if ((lastPing != null) && ((System.currentTimeMillis() - lastPing) < (10 * 1000)))
                {
                    continue;
                }

                try
                {
                    Process ping = Runtime.getRuntime().exec("ping -c 1 -W 2 " + ipaddr);
                    if ((ping.waitFor() == 0)) setAlive(ipaddr);
                }
                catch (Exception ignore)
                {
                }
            }

            Log.d(LOGTAG, "runner: finished.");
        }
    };
}
