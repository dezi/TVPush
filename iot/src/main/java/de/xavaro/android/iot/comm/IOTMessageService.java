package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class IOTMessageService extends Thread
{
    private static final String LOGTAG = IOTMessageService.class.getSimpleName();

    private static IOTMessageService receiver;

    private static final ArrayList<IOTMessageReceiver> subscribers = new ArrayList<>();

    public static void sendMessage(JSONObject json)
    {
        IOTTCPSender.sendMessage(json);
    }

    public static void doSubscribe(IOTMessageReceiver subscriber)
    {
        synchronized (subscribers)
        {
            if (! subscribers.contains(subscriber))
            {
                subscribers.add(subscriber);
            }
        }
    }

    public static void unSubscribe(IOTMessageReceiver subscriber)
    {
        synchronized (subscribers)
        {
            if (subscribers.contains(subscriber))
            {
                subscribers.remove(subscriber);
            }
        }
    }

    public static void startService()
    {
        if (receiver == null)
        {
            Log.d(LOGTAG, "startService: starting.");

            receiver = new IOTMessageService();
            receiver.start();
        }
        else
        {
            Log.d(LOGTAG, "startService: already started.");
        }

        IOTTCP.startService();
    }

    public static void stopService()
    {
        IOTTCP.stopService();

        if (receiver != null)
        {
            Log.d(LOGTAG, "stopService: stopping");

            receiver.stopRunning();
            receiver.interrupt();
            receiver = null;
        }
        else
        {
            Log.d(LOGTAG, "stopService: already stopped");
        }
   }

    private boolean running;

    public void stopRunning()
    {
        running = false;
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "run: started...");

        running = true;

        while (running)
        {
            try
            {
                JSONObject message = null;

                message = IOTTCPReceiver.receiveMessage();

                if (message == null)
                {
                    Thread.sleep(5);

                    continue;
                }

                synchronized (subscribers)
                {
                    for (IOTMessageReceiver subscriber : subscribers)
                    {
                        subscriber.receiveMessage(message);
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        Log.d(LOGTAG, "run: finished...");
    }
}
