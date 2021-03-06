package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import de.xavaro.android.iot.simple.Json;

public class IOTUDPSender extends Thread
{
    private static final String LOGTAG = IOTUDPSender.class.getSimpleName();

    private static final ArrayList<JSONObject> messageQueue = new ArrayList<>();

    public static void sendMessage(JSONObject message)
    {
        synchronized (messageQueue)
        {
            messageQueue.add(message);
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

                synchronized (messageQueue)
                {
                    if (messageQueue.size() > 0)
                    {
                        message = messageQueue.remove(0);
                    }
                }

                if (message == null)
                {
                    Thread.sleep(20);

                    continue;
                }

                String type = Json.getString(message, "type");

                if (type == null)
                {
                    Log.d(LOGTAG, "run: junk" + " message=" + message);

                    continue;
                }

                JSONObject dest = Json.getObject(message, "destination");
                Json.remove(message, "destination");

                byte[] txbuff = message.toString().getBytes();
                DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

                if (dest == null)
                {
                    //
                    // Broadcast.
                    //

                    txpack.setAddress(IOTUDP.bcastip);
                    txpack.setPort(IOTUDP.bcastport);
                }
                else
                {
                    //
                    // Dedicated.
                    //

                    String ipaddr = Json.getString(dest, "ipaddr");
                    int ipport = Json.getInt(dest, "ipport");

                    txpack.setAddress(InetAddress.getByName(ipaddr));
                    txpack.setPort(ipport);
                }

                String ipaddr = txpack.getAddress().toString().substring(1);
                int ipport = txpack.getPort();

                Log.d(LOGTAG, "run:"
                                + " send=" + ipaddr + ":" + ipport
                                + " type=" + type);

                IOTUDP.socket.send(txpack);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        Log.d(LOGTAG, "run: finished...");
    }
}