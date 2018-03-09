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
                IOTTCP.socket.receive(rxpack);

                String message = new String(rxpack.getData(), 0, rxpack.getLength());

                JSONObject jsonmess = Json.fromStringObject(message);

                if (jsonmess == null)
                {
                    Log.d(LOGTAG, "run: junk"
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " message=" + message);

                    continue;
                }

                String type  = Json.getString(jsonmess, "type");
                String ipaddr = rxpack.getAddress().toString().substring(1);
                int ipport = rxpack.getPort();

                Log.d(LOGTAG, "run:"
                        + " recv=" + ipaddr + ":" + ipport
                        + " type=" + type);

                JSONObject origin = new JSONObject();
                Json.put(jsonmess, "origin", origin);

                Json.put(origin, "ipaddr", ipaddr);
                Json.put(origin, "ipport", ipport);

                synchronized (messageQueue)
                {
                    messageQueue.add(jsonmess);
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
