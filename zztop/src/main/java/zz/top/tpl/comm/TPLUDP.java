package zz.top.tpl.comm;

import org.json.JSONObject;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;

import zz.top.utl.Json;
import zz.top.utl.Log;

public class TPLUDP
{
    private static final String LOGTAG = TPLUDP.class.getSimpleName();

    private static final int BCAST_PORT = 9999;
    private static final String BCAST_ADDR = "255.255.255.255";

    public static DatagramSocket socket;
    public static InetAddress bcastip;
    public static int bcastport;

    public static TPLUDPReceiver receiver;
    public static TPLUDPSender sender;

    public static void startService()
    {
        if (socket == null)
        {
            Log.d(LOGTAG, "startService: socket: starting.");

            try
            {
                bcastip = InetAddress.getByName(BCAST_ADDR);
                bcastport = BCAST_PORT;

                socket = new DatagramSocket(bcastport);
                socket.setReuseAddress(true);
                socket.setSoTimeout(15000);

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

            sender = new TPLUDPSender();
            sender.start();
        }
        else
        {
            Log.d(LOGTAG, "startService: sender: already started.");
        }

        if (receiver == null)
        {
            Log.d(LOGTAG, "startService: receiver: starting.");

            receiver = new TPLUDPReceiver();
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

    private static final byte TPLKEY = (byte) 0xAB;

    public static byte[] encryptMessage(String message)
    {
        byte[] data = message.getBytes();

        byte key = TPLKEY;

        for (int inx = 0; inx < data.length; inx ++)
        {
            data[inx] = (byte) (data[inx] ^ key);
            key = data[inx];
        }

        return data;
    }

    public static String decryptMessage(byte[] data, int offset, int len)
    {
        if (data == null) return null;

        byte key = TPLKEY;

        byte nextKey;

        for (int inx = 0; inx < len; inx++)
        {
            nextKey = data[inx + offset];
            data[inx + offset] = (byte) (data[inx + offset] ^ key);
            key = nextKey;
        }

        return new String(data, offset, len);
    }

    public static String getMessageType(JSONObject message)
    {
        String type = "unknown";

        JSONObject system = Json.getObject(message, "system");

        if (system != null)
        {
            Iterator<String> keys = system.keys();

            if  (keys.hasNext())
            {
                type = keys.next();
            }
        }

        JSONObject emeter = Json.getObject(message, "emeter");

        if (emeter != null)
        {
            Iterator<String> keys = emeter.keys();

            if  (keys.hasNext())
            {
                type = keys.next();
            }
        }

        JSONObject light = Json.getObject(message, "smartlife.iot.smartbulb.lightingservice");

        if (light != null)
        {
            Iterator<String> keys = light.keys();

            if  (keys.hasNext())
            {
                type = keys.next();
            }
        }

        return type;
    }
}
