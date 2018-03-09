package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import de.xavaro.android.simple.Json;

public class IOTTCPSender extends Thread
{
    private static final String LOGTAG = IOTTCPReceiver.class.getSimpleName();

    private static boolean running;

    private static final ArrayList<String> messageQueue = new ArrayList<>();

    public static void sendMessage(String message)
    {
        synchronized (messageQueue)
        {
            messageQueue.add(message);
        }
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
                String message = null;

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

                JSONObject json = Json.fromStringObject(message);
                String type = Json.getString(json, "type");

                if ((json == null) || (type == null))
                {
                    Log.d(LOGTAG, "run: junk" + " message=" + message);

                    continue;
                }

                byte[] txbuff = message.getBytes();
                DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

                JSONObject dest = Json.getObject(json, "destination");

                if (dest == null)
                {
                    //
                    // Broadcast.
                    //

                    txpack.setAddress(IOTTCP.bcastip);
                    txpack.setPort(IOTTCP.bcastport);
                }
                else
                {
                    //
                    // Dedicated.
                    //

                    Json.remove(json, "destination");

                    String ipaddr = Json.getString(dest, "ipaddr");
                    int ipport = Json.getInt(dest, "ipport");

                    txpack.setAddress(InetAddress.getByName(ipaddr));
                    txpack.setPort(ipport);
                }

                Log.d(LOGTAG, "run:"
                                + " send=" + txpack.getAddress().toString() + ":" + txpack.getPort()
                                + " type=" + type);

                IOTTCP.socket.send(txpack);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        Log.d(LOGTAG, "run: finished...");
    }
}