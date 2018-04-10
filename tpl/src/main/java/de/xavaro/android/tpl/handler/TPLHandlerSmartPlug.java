package de.xavaro.android.tpl.handler;

import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import de.xavaro.android.tpl.comm.TPLDatagrammService;
import de.xavaro.android.tpl.simple.Log;

public class TPLHandlerSmartPlug
{
    private static final String LOGTAG = TPLHandlerSmartPlug.class.getSimpleName();

    private static final int TPLINK_PORT = 9999;

    public static boolean sendPlugOnOff(String ipaddr, boolean on)
    {
        String messOn = "{\"system\":{\"set_relay_state\":{\"state\":1}}}";
        String messOff = "{\"system\":{\"set_relay_state\":{\"state\":0}}}";

        return sendToSocket(ipaddr, on ? messOn : messOff);
    }

    public static boolean sendLEDOnOff(String ipaddr, boolean on)
    {
        String messOn = "{\"system\":{\"set_led_off\":{\"off\": 0}}}";
        String messOff = "{\"system\":{\"set_led_off\":{\"off\": 1}}}";

        return sendToSocket(ipaddr, on ? messOn : messOff);
    }

    private static boolean sendToSocket(String ipaddr, String message)
    {
        boolean success = false;

        DatagramSocket socket = null;

        try
        {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);

            byte[] txbuff = TPLDatagrammService.encryptMessage(message);
            DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

            txpack.setAddress(InetAddress.getByName(ipaddr));
            txpack.setPort(TPLINK_PORT);

            socket.send(txpack);

            byte[] rxbuff = new byte[1024];
            DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);

            socket.receive(rxpack);

            success = true;
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

        return success;
    }
}
