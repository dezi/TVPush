package zz.top.tpl.comm;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;
import zz.top.utl.Log;

public class TPLMessageService extends Thread
{
    private static final String LOGTAG = TPLMessageService.class.getSimpleName();

    public static void sendMessage(JSONObject json)
    {
        TPLUDPSender.sendMessage(json);
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
                JSONObject message = TPLUDPReceiver.receiveMessage();

                if (message == null)
                {
                    Thread.sleep(5);

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
