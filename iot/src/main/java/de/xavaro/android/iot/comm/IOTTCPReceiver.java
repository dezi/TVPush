package de.xavaro.android.iot.comm;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.xavaro.android.simple.Json;

public class IOTTCPReceiver extends Thread
{
    private static final String LOGTAG = IOTTCPReceiver.class.getSimpleName();

    private static boolean running;

    private static final ArrayList<String> messageQueue = new ArrayList<>();

    @Nullable
    public static String receiveMessage()
    {
        synchronized (messageQueue)
        {
            if (messageQueue.size() > 0)
            {
                return messageQueue.remove(0);
            }
        }

        return null;
    }

    public static void stopService()
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
                byte[] rxbuff = new byte[8 * 1024];
                DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);
                IOTTCP.socket.receive(rxpack);

                String message = new String(rxpack.getData(), 0, rxpack.getLength());

                if (message.length() == 4)
                {
                    Log.d(LOGTAG, "run: simple "
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " simple=" + message);

                    continue;
                }

                JSONObject jsonmess = Json.fromStringObject(message);

                if (jsonmess == null)
                {
                    Log.d(LOGTAG, "run: junk"
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " message=" + message);

                    continue;
                }

                synchronized (messageQueue)
                {
                    messageQueue.add(message);
                }
            }
            catch (SocketTimeoutException ignore)
            {
                //
                // Do nothing.
                //
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
