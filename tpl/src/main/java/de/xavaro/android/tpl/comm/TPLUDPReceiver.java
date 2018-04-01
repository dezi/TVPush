package de.xavaro.android.tpl.comm;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.simple.Log;

public class TPLUDPReceiver extends Thread
{
    private static final String LOGTAG = TPLUDPReceiver.class.getSimpleName();

    private static final ArrayList<JSONObject> messageQueue = new ArrayList<>();

    @Nullable
    public static JSONObject receiveMessage()
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
                byte[] rxbuff = new byte[8 * 1024];
                DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);
                TPLUDP.socket.receive(rxpack);

                String strmess = TPLUDP.decryptMessage(rxpack.getData(), rxpack.getOffset(), rxpack.getLength());

                JSONObject message = Json.fromStringObject(strmess);

                if (message == null)
                {
                    Log.d(LOGTAG, "run: junk"
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " message=" + message);

                    continue;
                }

                String type  = TPLUDP.getMessageType(message);
                String ipaddr = rxpack.getAddress().toString().substring(1);
                int ipport = rxpack.getPort();

                Log.d(LOGTAG, "run:"
                        + " recv=" + ipaddr + ":" + ipport
                        + " type=" + type);

                JSONObject origin = new JSONObject();
                Json.put(message, "origin", origin);

                Json.put(origin, "ipaddr", ipaddr);
                Json.put(origin, "ipport", ipport);

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
            catch (SocketException ex)
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
    }
}
