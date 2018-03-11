package zz.top.tpl.comm;

import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;

public class TPLMessageService extends Thread
{
    private static final String LOGTAG = TPLMessageService.class.getSimpleName();

    private static TPLMessageService receiver;

    public static void sendMessage(JSONObject json)
    {
        TPLUDPSender.sendMessage(json);
    }

    public static void startService()
    {
        if (receiver == null)
        {
            Log.d(LOGTAG, "startService: starting.");

            receiver = new TPLMessageService();
            receiver.start();
        }
        else
        {
            Log.d(LOGTAG, "startService: already started.");
        }

        TPLUDP.startService();
    }

    public static void stopService()
    {
        TPLUDP.stopService();

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

                message = TPLUDPReceiver.receiveMessage();

                if (message == null)
                {
                    Thread.sleep(5);

                    continue;
                }

                TPL.instance.message.receiveMessage(message);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        Log.d(LOGTAG, "run: finished...");
    }
}
