package de.xavaro.android.brl.comm;

import android.support.annotation.Nullable;
import android.os.Build;
import android.util.Log;

import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class BRLCommand
{
    private static final String LOGTAG = BRLCommand.class.getSimpleName();

    private static final int DEFAULT_BYTES_SIZE = 56;

    private final static byte authCommand = 0x65;
    private final static byte powerStateCommand = 0x6a;

    private final static Map<String, BRLCrypt> deviceCrypt = new HashMap<>();

    @Nullable
    private static BRLCrypt getAuth(String ipaddr, String macaddr)
    {
        BRLCrypt crypt = getMapCrypt(deviceCrypt, macaddr);
        if (crypt != null) return crypt;

        //
        // Authorize new crypt.
        //

        crypt = new BRLCrypt();

        byte[] message = cmdPacket(macaddr, authCommand, getAuthPayload(), crypt);

        byte[] response = sendToSocket(ipaddr, message);

        if (response == null)
        {
            Log.e(LOGTAG, "getAuth: no response!");

            return null;
        }

        int err = (response[0x22] & 0xff) + ((response[0x23] & 0xff) << 8);

        if (err == 0)
        {
            Log.d(LOGTAG, "getAuth: received ok ipaddr=" + ipaddr);

            byte[] result = decryptFromDeviceMessage(response, crypt);

            if (result == null)
            {
                Log.e(LOGTAG, "getAuth: decrypt failed!");

                return null;
            }

            byte[] id = new byte[4];
            System.arraycopy(result, 0, id, 0, id.length);

            byte[] key = new byte[16];
            System.arraycopy(result, 4, key, 0, key.length);

            Log.d(LOGTAG, "getAuth:"
                    + " id=" + BRLUtil.getHexBytesToString(id)
                    + " key=" + BRLUtil.getHexBytesToString(key));

            crypt = new BRLCrypt(key, id);

            synchronized (deviceCrypt)
            {
                deviceCrypt.put(macaddr, crypt);
            }

            return crypt;
        }
        else
        {
            Log.e(LOGTAG, "getAuth: received error=" + Integer.toHexString(err));
        }

        return null;
    }

    public static int getPowerStatus(String ipaddr, String macaddr)
    {
        BRLCrypt crypt = getAuth(ipaddr, macaddr);

        if (crypt == null)
        {
            Log.e(LOGTAG, "getPowerStatus: no crypt!");
            return -1;
        }

        byte[] message = cmdPacket(macaddr, powerStateCommand, getPowerStatusPayload(), crypt);

        byte[] response = sendToSocket(ipaddr, message);

        if (response == null)
        {
            Log.e(LOGTAG, "getPowerStatus: no response!");

            return -1;
        }

        int err = (response[0x22] & 0xff) + ((response[0x23] & 0xff) << 8);

        if (err == 0)
        {
            byte[] result = decryptFromDeviceMessage(response, crypt);

            if (result == null)
            {
                Log.d(LOGTAG, "getPowerStatus: cannot decrypt!");

                return -1;
            }

            int onoff = (result[0x4] == 1) ? 1 : 0;

            Log.d(LOGTAG, "getPowerStatus: received ok ipaddr=" + ipaddr + " onoff=" + onoff);

            return onoff;
        }
        else
        {
            Log.e(LOGTAG, "getPowerStatus: received error=" + Integer.toHexString(err));
        }

        return -1;
    }

    public static int setPowerStatus(String ipaddr, String macaddr, int onoff)
    {
        BRLCrypt crypt = getAuth(ipaddr, macaddr);

        if (crypt == null)
        {
            Log.e(LOGTAG, "setPowerStatus: no crypt!");
            return -1;
        }

        byte[] message = cmdPacket(macaddr, powerStateCommand, setPowerStatusPayload(onoff), crypt);

        byte[] response = sendToSocket(ipaddr, message);

        if (response == null)
        {
            Log.e(LOGTAG, "setPowerStatus: no response!");

            return -1;
        }

        int err = (response[0x22] & 0xff) + ((response[0x23] & 0xff) << 8);

        if (err == 0)
        {
            byte[] result = decryptFromDeviceMessage(response, crypt);

            if (result == null)
            {
                Log.d(LOGTAG, "setPowerStatus: cannot decrypt!");

                return -1;
            }

            Log.d(LOGTAG, "setPowerStatus: received ok ipaddr=" + ipaddr + " onoff=" + onoff);

            return onoff;
        }
        else
        {
            Log.e(LOGTAG, "setPowerStatus: received error=" + Integer.toHexString(err));
        }

        return -1;
    }

    @Nullable
    private static byte[] sendToSocket(String ipaddr, byte[] message)
    {
        DatagramSocket socket = null;

        try
        {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            byte[] txbuff = encryptMessage(message);
            DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);

            txpack.setAddress(InetAddress.getByName(ipaddr));
            txpack.setPort(BRLDiscover.BRL_COMM_PORT);

            socket.send(txpack);

            byte[] rxbuff = new byte[1024];
            DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);

            socket.receive(rxpack);

            return decryptMessage(rxpack.getData(), rxpack.getLength());
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

    private static byte[] encryptMessage(byte[] message)
    {
        return message;
    }

    @Nullable
    private static byte[] decryptMessage(byte[] data, int len)
    {
        if (data == null) return null;

        byte[] result = new byte[len];

        System.arraycopy(data, 0, result, 0, len);

        return result;
    }

    private static byte[] getRawPayloadBytesPadded(byte[] data)
    {
        byte[] payload = new byte[data.length - DEFAULT_BYTES_SIZE];
        System.arraycopy(data, DEFAULT_BYTES_SIZE, payload, 0, payload.length);

        int numpad = 16 - (payload.length % 16);

        byte[] payloadPadded = new byte[payload.length + numpad];
        System.arraycopy(payload, 0, payloadPadded, 0, payload.length);

        return payloadPadded;
    }

    @Nullable
    private static byte[] decryptFromDeviceMessage(byte[] encData, BRLCrypt crypt)
    {
        byte[] encPL = getRawPayloadBytesPadded(encData);

        return crypt.decrypt(encPL);
    }

    private static byte[] setPowerStatusPayload(int onoff)
    {
        byte[] data = new byte[16];

        data[0] = 2;
        data[4] = (byte) onoff;

        return data;
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
    private static byte[] cmdPacket(String macaddr, byte cmd, byte[] payload, BRLCrypt crypt)
    {
        int count = 1;

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

        byte[] id = crypt.getId();

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

        for (byte bite : payloadPad)
        {
            checksumpayload = checksumpayload + (bite & 0xff);
            checksumpayload = checksumpayload & 0xffff;
        }

        headerdata[0x34] = (byte) (checksumpayload & 0xff);
        headerdata[0x35] = (byte) (checksumpayload >> 8);

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

        for (byte bite : data)
        {
            checksumpkt = checksumpkt + (bite & 0xff);
            checksumpkt = checksumpkt & 0xffff;
        }

        data[0x20] = (byte) (checksumpkt & 0xff);
        data[0x21] = (byte) (checksumpkt >> 8);

        return data;
    }

    @Nullable
    private static BRLCrypt getMapCrypt(Map<String, BRLCrypt> map, String key)
    {
        synchronized (deviceCrypt)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                return map.getOrDefault(key, null);
            }
            else
            {
                try
                {
                    return map.get(key);
                }
                catch (Exception ignore)
                {
                    return null;
                }
            }
        }
    }

}