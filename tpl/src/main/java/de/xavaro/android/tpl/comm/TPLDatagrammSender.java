package de.xavaro.android.tpl.comm;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.simple.Log;

public class TPLDatagrammSender extends Thread
{
    private static final String LOGTAG = TPLDatagrammSender.class.getSimpleName();

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

                String type = TPLDatagrammService.getMessageType(message);

                JSONObject dest = Json.getObject(message, "destination");
                Json.remove(message, "destination");

                byte[] txbuff = TPLDatagrammService.encryptMessage(message.toString());
                DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

                if (dest == null)
                {
                    //
                    // Broadcast.
                    //

                    txpack.setAddress(TPLDatagrammService.bcastip);
                    txpack.setPort(TPLDatagrammService.bcastport);
                }
                else
                {
                    //
                    // Dedicated.
                    //

                    String ipaddr = Json.getString(dest, "ipaddr");
                    int ipport = Json.getInt(dest, "ipport");

                    txpack.setAddress(InetAddress.getByName(ipaddr));
                    txpack.setPort((ipport != 0) ? ipport : TPLDatagrammService.bcastport);
                }

                String ipaddr = txpack.getAddress().toString().substring(1);
                int ipport = txpack.getPort();

                Log.d(LOGTAG, "run:"
                        + " send=" + ipaddr + ":" + ipport
                        + " type=" + type);

                TPLDatagrammService.socket.send(txpack);
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