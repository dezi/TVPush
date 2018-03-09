package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class IOTMessaging extends Thread
{
    private static final String LOGTAG = IOTMessaging.class.getSimpleName();

    private static IOTMessaging receiver;

    private static final ArrayList<IOTMessageReceiver> subscribers = new ArrayList<>();

    static
    {
        startService();
    }

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
        Log.d(LOGTAG, "startService.");

        if (receiver == null)
        {
            receiver = new IOTMessaging();
            receiver.start();
        }
    }

    public static void stopService()
    {
        Log.d(LOGTAG, "stopService.");

        if (receiver != null)
        {
            receiver.stopRunning();
            receiver.interrupt();
            receiver = null;
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
