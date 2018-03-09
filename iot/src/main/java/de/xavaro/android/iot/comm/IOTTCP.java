package de.xavaro.android.iot.comm;

import android.util.Log;

import java.net.InetAddress;
import java.net.MulticastSocket;

public class IOTTCP
{
    private static final String LOGTAG = IOTTCP.class.getSimpleName();

    private static final int BCAST_PORT = 42742;
    private static final String BCAST_ADDR = "239.255.255.250";

    public static MulticastSocket socket;
    public static InetAddress bcastip;
    public static int bcastport;

    public static IOTTCPReceiver receiver;
    public static IOTTCPSender sender;

    public static void startService()
    {
        if (socket == null)
        {
            Log.d(LOGTAG, "startService: socket: starting.");

            try
            {
                bcastip = InetAddress.getByName(BCAST_ADDR);
                bcastport = BCAST_PORT;

                socket = new MulticastSocket(bcastport);
                socket.setReuseAddress(true);
                socket.setSoTimeout(15000);
                socket.joinGroup(bcastip);

                Log.d(LOGTAG, "startService: socket: ip=" + BCAST_ADDR + " port=" + BCAST_PORT);
            }
            catch (Exception ex)
            {
                Log.d(LOGTAG, "startService: socket: failed.");

                socket = null;
            }
        }
        else
        {
            Log.d(LOGTAG, "startService: socket: already started.");
        }

        if (sender == null)
        {
            Log.d(LOGTAG, "startService: sender: starting.");

            sender = new IOTTCPSender();
            sender.start();
        }
        else
        {
            Log.d(LOGTAG, "startService: sender: already started.");
        }

        if (receiver == null)
        {
            Log.d(LOGTAG, "startService: receiver: starting.");

            receiver = new IOTTCPReceiver();
            receiver.start();
        }
        else
        {
            Log.d(LOGTAG, "startService: receiver: already started.");
        }
    }

    public static void stopService()
    {
        if (socket != null)
        {
            Log.d(LOGTAG, "stopService: socket: stopping.");

            socket.close();
            socket = null;
        }
        else
        {
            Log.d(LOGTAG, "stopService: socket: already stopped.");
        }

        if (sender != null)
        {
            Log.d(LOGTAG, "stopService: sender: stopping.");

            sender.stopRunning();
            sender.interrupt();
            sender = null;
        }
        else
        {
            Log.d(LOGTAG, "stopService: sender: already stopped.");
        }
        
        if (receiver != null)
        {
            Log.d(LOGTAG, "stopService: receiver: stopping.");

            receiver.stopRunning();
            receiver.interrupt();
            receiver = null;
        }
        else
        {
            Log.d(LOGTAG, "stopService: receiver: already stopped.");
        }
    }
}
