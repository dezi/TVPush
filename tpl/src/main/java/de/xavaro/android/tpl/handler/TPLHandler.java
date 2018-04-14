package de.xavaro.android.tpl.handler;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import de.xavaro.android.tpl.comm.TPLDiscover;
import de.xavaro.android.tpl.simple.Log;

public class TPLHandler
{
    private static final String LOGTAG = TPLHandler.class.getSimpleName();

    @Nullable
    public static String sendToSocket(String ipaddr, JSONObject message)
    {
        return sendToSocket(ipaddr, message.toString());
    }

    @Nullable
    public static String sendToSocket(String ipaddr, String message)
    {
        DatagramSocket socket = null;

        try
        {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);

            byte[] txbuff = encryptMessage(message);
            DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

            txpack.setAddress(InetAddress.getByName(ipaddr));
            txpack.setPort(TPLDiscover.TPLINK_PORT);

            socket.send(txpack);

            byte[] rxbuff = new byte[1024];
            DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);

            socket.receive(rxpack);

            return decryptMessage(rxpack.getData(), 0, rxpack.getLength());
        }
        catch (SocketTimeoutException ignore)
        {
            Log.e(LOGTAG, "sendToSocket: socket: receive timeout ipaddr=" + ipaddr);
        }
        catch (Exception ex)
        {
            Log.e(LOGTAG, "sendToSocket: socket: failed ipaddr=" + ipaddr);
        }
        finally
        {
            if (socket != null) socket.close();
        }

        return null;
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

    @Nullable
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
}
