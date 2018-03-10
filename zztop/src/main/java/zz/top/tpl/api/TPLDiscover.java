package zz.top.tpl.api;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import zz.top.tpl.comm.TPLUDPSender;
import zz.top.utl.Json;

public class TPLDiscover
{
    private static final String LOGTAG = TPLDiscover.class.getSimpleName();

    private static final int BCAST_PORT = 9999;
    private static final String BCAST_ADDR = "255.255.255.255";

    public static DatagramSocket socket;
    public static InetAddress bcastip;
    public static int bcastport;
    public static String mess = "{\"system\":{\"get_sysinfo\":{}}}";

    public static void discover()
    {
        TPLUDPSender.sendMessage(Json.fromStringObject(mess));
    }

    public static void discoverxxx()
    {

        try
        {
            bcastip = InetAddress.getByName(BCAST_ADDR);
            bcastport = BCAST_PORT;

            socket = new DatagramSocket(bcastport);
            //socket.setReuseAddress(true);
            socket.setSoTimeout(2000);
            socket.setBroadcast(true);

            Log.d(LOGTAG, "startService: socket: ip=" + BCAST_ADDR + " port=" + BCAST_PORT);

            Thread worker = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    byte[] txbuff = encrypt2(mess);
                    final DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);
                    txpack.setAddress(bcastip);
                    txpack.setPort(bcastport);

                    try
                    {
                        socket.send(txpack);

                        Log.d(LOGTAG, "send=" + mess);

                    }
                        catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    while (true)
                    {
                        try
                        {
                            byte[] rxbuff = new byte[8 * 1024];
                            DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);
                            socket.receive(rxpack);

                            String message = decrypt2(rxbuff, rxpack.getLength());

                            Log.d(LOGTAG, " len=" + rxpack.getLength() + " read=" + message);
                        }
                        catch (SocketTimeoutException ignore)
                        {
                            //
                            // Do nothing.
                            //
                            try
                            {
                                socket.send(txpack);
                                Log.d(LOGTAG, "send=" + mess);
                            }
                            catch (Exception ex)
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
            });

            worker.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.d(LOGTAG, "startService: socket: failed.");

            socket = null;
        }
    }

    private static final byte KEY = (byte) 0xAB;

    private static byte[] encrypt2(String message)
    {
        byte[] data = message.getBytes();
        byte[] enc = new byte[data.length];
        System.arraycopy(data, 0, enc, 0, data.length);
        byte key = KEY;
        for (int i = 0; i < enc.length; i ++)
        {
            enc[i] = (byte) (enc[i] ^ key);
            key = enc[i];
        }
        return enc;
    }

    private static byte[] encrypt(String message)
    {
        byte[] data = message.getBytes();
        byte[] enc = new byte[data.length + 4];
        ByteBuffer.wrap(enc).putInt(data.length);
        System.arraycopy(data, 0, enc, 4, data.length);
        byte key = KEY;
        for (int i = 4; i < enc.length; i ++)
        {
            enc[i] = (byte) (enc[i] ^ key);
            key = enc[i];
        }
        return enc;
    }

    private static String decrypt2(byte[] data, int len)
    {
        if (data == null) return null;

        byte key = KEY;

        byte nextKey;
        for (int i = 0; i < len; i++)
        {
            nextKey = data[i];
            data[i] = (byte) (data[i] ^ key);
            key = nextKey;
        }

        return new String(data, 0, len);
    }

    private static String decrypt(byte[] data)
    {
        if (data == null) return null;

        int len = (data[ 0 ] << 24) + (data[ 1 ] << 16) + (data[ 2 ] << 8) + (data[ 3 ] << 0);

        byte key = KEY;
        byte nextKey = 0;
        for (int i = 4; i < len + 4; i++)
        {
            nextKey = data[i];
            data[i] = (byte) (data[i] ^ key);
            key = nextKey;
        }
        return new String(data, 4, len);
    }

}
