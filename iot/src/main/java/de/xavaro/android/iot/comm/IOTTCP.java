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

    static
    {
        try
        {
            bcastip = InetAddress.getByName(BCAST_ADDR);
            bcastport = BCAST_PORT;

            socket = new MulticastSocket(bcastport);
            socket.setReuseAddress(true);
            socket.setSoTimeout(15000);
            socket.joinGroup(bcastip);

            Log.d(LOGTAG, "static: MulticastSocket ip=" + BCAST_ADDR + " port=" + BCAST_PORT);

            startService();
        }
        catch (Exception ex)
        {
            socket = null;
        }
    }

    public static void startService()
    {
        Log.d(LOGTAG, "startService.");

        if (receiver == null)
        {
            receiver = new IOTTCPReceiver();
            receiver.start();
        }

        if (sender == null)
        {
            sender = new IOTTCPSender();
            sender.start();
        }
    }

    public static void stopService()
    {
        Log.d(LOGTAG, "stopService.");

        if (receiver != null)
        {
            receiver.stopRunning();
            receiver.interrupt();
            receiver = null;
        }

        if (sender != null)
        {
            sender.stopRunning();
            sender.interrupt();
            sender = null;
        }
    }
}
