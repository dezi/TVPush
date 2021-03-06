package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;

public class IOTMessageService extends Thread
{
    private static final String LOGTAG = IOTMessageService.class.getSimpleName();

    private static IOTMessageService receiver;

    public static void sendMessage(JSONObject json)
    {
        IOTUDPSender.sendMessage(json);
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

        IOTUDP.startService();
    }

    public static void stopService()
    {
        IOTUDP.stopService();

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

                message = IOTUDPReceiver.receiveMessage();

                if (message == null)
                {
                    Thread.sleep(5);

                    continue;
                }

                IOT.message.receiveMessage(message);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        Log.d(LOGTAG, "run: finished...");
    }
}
