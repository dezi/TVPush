package de.xavaro.android.tpl.comm;

import org.json.JSONObject;

import de.xavaro.android.tpl.base.TPL;
import de.xavaro.android.tpl.simple.Log;

public class TPLMessageService extends Thread
{
    private static final String LOGTAG = TPLMessageService.class.getSimpleName();

    public static void sendMessage(JSONObject json)
    {
        TPLDatagrammSender.sendMessage(json);
    }

    public static void startService()
    {
        if (TPL.instance.receiver == null)
        {
            Log.d(LOGTAG, "startService: starting.");

            TPL.instance.receiver = new TPLMessageService();
            TPL.instance.receiver.start();
        }
        else
        {
            Log.d(LOGTAG, "startService: already started.");
        }
    }

    public static void stopService()
    {
        if (TPL.instance.receiver != null)
        {
            Log.d(LOGTAG, "stopService: stopping");

            TPL.instance.receiver.stopRunning();
            TPL.instance.receiver.interrupt();
            TPL.instance.receiver = null;
        }
        else
        {
            Log.d(LOGTAG, "stopService: already stopped");
        }
    }

    private boolean running;

    private void stopRunning()
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
                JSONObject message = TPLDatagrammReceiver.receiveMessage();

                if (message == null)
                {
                    Thread.sleep(40);

                    continue;
                }

                TPL.instance.message.receiveMessage(message);
            }
            catch (InterruptedException ex)
            {
                if (running)
                {
                    ex.printStackTrace();
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
