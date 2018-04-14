package de.xavaro.android.brl.comm;

import android.support.annotation.Nullable;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class BRLCommand
{
    private static final String LOGTAG = BRLCommand.class.getSimpleName();

    public static final int DEFAULT_BYTES_SIZE = 56;

    private final static byte authCommand = 0x65;
    private final static byte powerStateCommand = 0x6a;

    public static int getAuth(String ipaddr, int ipport, String macaddr)
    {
        byte[] message = cmdPacket(macaddr, authCommand, getAuthPayload());

        byte[] response = sendToSocket(ipaddr, message);

        if (response == null)
        {
            Log.e(LOGTAG, "getAuth: no response!");

            return -1;
        }

        int err = (response[0x22] & 0xff) + ((response[0x23] & 0xff) << 8);

        if (err == 0)
        {
            Log.d(LOGTAG, "getAuth: received ok");

            Log.d(LOGTAG, "getAuth: hex=" + BRLUtil.getHexBytesToString(response));

            byte[] result = decryptFromDeviceMessage(response);

            Log.d(LOGTAG, "getAuth: dec=" + BRLUtil.getHexBytesToString(result));
        }
        else
        {
            Log.e(LOGTAG, "getAuth: received error=" + Integer.toHexString(err));
        }

        return -1;
    }

    public static int getPowerStatus(String ipaddr, int ipport, String macaddr)
    {
        byte[] message = cmdPacket(macaddr, powerStateCommand, getPowerStatusPayload());

        byte[] response = sendToSocket(ipaddr, message);

        if (response == null)
        {
            Log.e(LOGTAG, "getPowerStatus: no response!");

            return -1;
        }

        int err = (response[0x22] & 0xff) + ((response[0x23] & 0xff) << 8);

        if (err == 0)
        {
            Log.e(LOGTAG, "getPowerStatus: received ok");
        }
        else
        {
            Log.e(LOGTAG, "getPowerStatus: received error=" + Integer.toHexString(err));
        }

        return -1;
    }

    @Nullable
    public static byte[] sendToSocket(String ipaddr, byte[] message)
    {
        DatagramSocket socket = null;

        try
        {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);

            byte[] txbuff = encryptMessage(message);
            DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

            txpack.setAddress(InetAddress.getByName(ipaddr));
            txpack.setPort(BRLDiscover.BRL_COMM_PORT);

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

    public static byte[] encryptMessage(byte[] message)
    {
        return message;
    }

    @Nullable
    public static byte[] decryptMessage(byte[] data, int offset, int len)
    {
        if (data == null) return null;

        byte[] result = new byte[len];

        System.arraycopy(data, offset, result, 0, len);

        Log.d(LOGTAG, "decryptMessage: result.len=" + result.length);

        return result;
    }

    public static byte[] getRawPayloadBytesPadded(byte[] data)
    {
        byte[] payload = new byte[data.length - DEFAULT_BYTES_SIZE];
        System.arraycopy(data, DEFAULT_BYTES_SIZE, payload, 0, payload.length);

        int numpad = 16 - (payload.length % 16);

        byte[] payloadPadded = new byte[payload.length + numpad];
        System.arraycopy(payload, 0, payloadPadded, 0, payload.length);

        return payloadPadded;
    }

    @Nullable
    protected static byte[] decryptFromDeviceMessage(byte[] encData)
    {
        byte[] encPL = getRawPayloadBytesPadded(encData);

        Log.d(LOGTAG, "decryptFromDeviceMessage: enc=" + BRLUtil.getHexBytesToString(encPL));

        BRLCrypt crypt = new BRLCrypt();
        byte[] decPL = crypt.decrypt(encPL);

        Log.d(LOGTAG, "decryptFromDeviceMessage: dec=" + BRLUtil.getHexBytesToString(decPL));

        return decPL;
    }

    private static byte[] getPowerStatusPayload()
    {
        byte[] data = new byte[16];

        data[0] = 1;

        return data;
    }

    private static byte[] getAuthPayload()
    {
        byte[] data = new byte[0x50];

        data[0x04] = 0x31;
        data[0x05] = 0x31;
        data[0x06] = 0x31;
        data[0x07] = 0x31;
        data[0x08] = 0x31;
        data[0x09] = 0x31;
        data[0x0a] = 0x31;
        data[0x0b] = 0x31;
        data[0x0c] = 0x31;
        data[0x0d] = 0x31;
        data[0x0e] = 0x31;
        data[0x0f] = 0x31;
        data[0x10] = 0x31;
        data[0x11] = 0x31;
        data[0x12] = 0x31;
        data[0x1e] = 0x01;
        data[0x2d] = 0x01;
        data[0x30] = (byte) 'T';
        data[0x31] = (byte) 'e';
        data[0x32] = (byte) 's';
        data[0x33] = (byte) 't';
        data[0x34] = (byte) ' ';
        data[0x35] = (byte) ' ';
        data[0x36] = (byte) '1';

        return data;
    }

    @Nullable
    private static byte[] macToBytes(String macaddr)
    {
        byte[] macbytes = null;

        if (macaddr != null)
        {
            String[] parts = macaddr.split(":");

            if (parts.length == 6)
            {
                macbytes = new byte[ parts.length ];

                for (int inx = 0; inx < parts.length; inx++)
                {
                    macbytes[ inx ] = (byte) (Integer.parseInt(parts[ inx ], 16) & 0xff);
                }
            }
        }

        return macbytes;
    }

    @Nullable
    private static byte[] cmdPacket(String macaddr, byte cmd, byte[] payload)
    {
        int count = 1;

        byte[] id = new byte[4];

        byte[] headerdata = new byte[DEFAULT_BYTES_SIZE];

        headerdata[0x00] = (byte) 0x5a;
        headerdata[0x01] = (byte) 0xa5;
        headerdata[0x02] = (byte) 0xaa;
        headerdata[0x03] = (byte) 0x55;
        headerdata[0x04] = (byte) 0x5a;
        headerdata[0x05] = (byte) 0xa5;
        headerdata[0x06] = (byte) 0xaa;
        headerdata[0x07] = (byte) 0x55;

        headerdata[0x24] = (byte) 0x2a;
        headerdata[0x25] = (byte) 0x27;
        headerdata[0x26] = cmd;

        headerdata[0x28] = (byte) (count & 0xff);
        headerdata[0x29] = (byte) (count >> 8);

        byte[] mac = macToBytes(macaddr);

        if (mac == null)
        {
            Log.e(LOGTAG, "cmdPacket: mac address failed!");
            return null;
        }

        headerdata[0x2a] = mac[0];
        headerdata[0x2b] = mac[1];
        headerdata[0x2c] = mac[2];
        headerdata[0x2d] = mac[3];
        headerdata[0x2e] = mac[4];
        headerdata[0x2f] = mac[5];

        headerdata[0x30] = id[0];
        headerdata[0x31] = id[1];
        headerdata[0x32] = id[2];
        headerdata[0x33] = id[3];

        //
        // Pad the payload for AES encryption,
        //

        int numpad = 16 - (payload.length % 16);
        byte[] payloadPad = new byte[payload.length + numpad];
        System.arraycopy(payload, 0, payloadPad, 0, payload.length);

        int checksumpayload = 0xbeaf;

        for (int inx = 0; inx < payloadPad.length; inx++)
        {
            checksumpayload = checksumpayload + (payloadPad[inx] & 0xff);
            checksumpayload = checksumpayload & 0xffff;
        }

        headerdata[0x34] = (byte) (checksumpayload & 0xff);
        headerdata[0x35] = (byte) (checksumpayload >> 8);

        Log.d(LOGTAG, "cmdPacket: Un-encrypted payload checksum=" + Integer.toHexString(checksumpayload));

        BRLCrypt crypt = new BRLCrypt();
        payload = crypt.encrypt(payloadPad);

        if (payload == null)
        {
            Log.e(LOGTAG, "cmdPacket: encryption failed!");
            return null;
        }

        byte[] data = new byte[headerdata.length + payload.length];

        System.arraycopy(headerdata, 0, data, 0, headerdata.length);
        System.arraycopy(payload, 0, data, headerdata.length, payload.length);

        int checksumpkt = 0xbeaf;

        for (int inx = 0; inx < data.length; inx++)
        {
            checksumpkt = checksumpkt + (data[inx] & 0xff);
            checksumpkt = checksumpkt & 0xffff;
        }

        Log.d(LOGTAG, "cmdPacket: Whole packet checksum=" + Integer.toHexString(checksumpkt));

        data[0x20] = (byte) (checksumpkt & 0xff);
        data[0x21] = (byte) (checksumpkt >> 8);

        return data;
    }
}
