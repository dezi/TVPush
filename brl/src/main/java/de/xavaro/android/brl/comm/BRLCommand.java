package de.xavaro.android.brl.comm;

import android.support.annotation.Nullable;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.brl.simple.Json;

public class BRLCommand
{
    private static final String LOGTAG = BRLCommand.class.getSimpleName();

    private static final int DEFAULT_BYTES_SIZE = 56;

    private final static byte authCommand = 0x65;
    private final static byte doitCommand = 0x6a;

    private final static Map<String, BRLCrypt> deviceCrypt = new HashMap<>();

    @Nullable
    public static Integer enterLearning(String ipaddr, String macaddr)
    {
        byte[] result = sendCommand(ipaddr, macaddr, doitCommand, enterLearningPayload());
        if (result == null) return null;

        Log.d(LOGTAG, "enterLearning: received ok ipaddr=" + ipaddr);

        return 1;
    }

    @Nullable
    public static String getLearnedData(String ipaddr, String macaddr)
    {
        byte[] result = sendCommand(ipaddr, macaddr, doitCommand, getLearnedDataPayload());
        if (result == null) return null;

        String hexstr = toHexString(result, 4, result.length - 4);

        Log.d(LOGTAG, "getLearnedData: received ok ipaddr=" + ipaddr + " hex=" + hexstr);

        return hexstr;
    }

    private static String toHexString(byte[] buffer, int offset, int length)
    {
        StringBuilder hexstr = new StringBuilder();

        for (int inx = 0; inx < length; inx++)
        {
            String hex = Integer.toHexString(buffer[ inx + offset ] & 0xff);
            hexstr.append(hex.length() < 2 ? "0" + hex : hex);
        }

        return hexstr.toString();
    }

    @Nullable
    public static Integer getPowerStatus(String ipaddr, String macaddr)
    {
        byte[] result = sendCommand(ipaddr, macaddr, doitCommand, getPowerStatusPayload());
        if (result == null) return null;

        Integer onoff = ((result[0x4] & 0xff) == 1) ? 1 : 0;

        Log.d(LOGTAG, "getPowerStatus: received ok ipaddr=" + ipaddr + " onoff=" + onoff);

        return onoff;
    }

    @Nullable
    public static Integer setPowerStatus(String ipaddr, String macaddr, int onoff)
    {
        byte[] result = sendCommand(ipaddr, macaddr, doitCommand, setPowerStatusPayload(onoff));
        if (result == null) return null;

        Log.d(LOGTAG, "setPowerStatus: received ok ipaddr=" + ipaddr + " onoff=" + onoff);

        return onoff;
    }

    @Nullable
    public static Double getTemperature(String ipaddr, String macaddr)
    {
        byte[] result = sendCommand(ipaddr, macaddr, doitCommand, getTempStatusPayload());
        if (result == null) return null;

        Double temp = ((result[4] & 0xff) * 10 + (result[5] & 0xff)) / 10.0;

        Log.d(LOGTAG, "getTemperature: received ok ipaddr=" + ipaddr + " temp=" + temp);

        return temp;
    }

    @Nullable
    public static JSONObject getSensorData(String ipaddr, String macaddr)
    {
        byte[] result = sendCommand(ipaddr, macaddr, doitCommand, getSensorStatusPayload());
        if (result == null) return null;

        double temp = (float) (((result[0x4] & 0xff) * 10 + (result[0x5] & 0xff)) / 10.0);
        double humi = (float) (((result[0x6] & 0xff) * 10 + (result[0x7] & 0xff)) / 10.0);
        int light = result[0x8] & 0xff;
        int airquality = result[0x0a] & 0xff;
        int noise = result[0xc] & 0xff;

        Log.d(LOGTAG, "getSensorData: received ok ipaddr=" + ipaddr
                + " temp=" + temp
                + " humi=" + humi
                + " light=" + light
                + " airquality=" + airquality
                + " noise=" + noise
        );

        JSONObject res = new JSONObject();

        Json.put(res, "temperature", temp);
        Json.put(res, "humidity", humi);
        Json.put(res, "lightlevel", light);
        Json.put(res, "noiselevel", noise);
        Json.put(res, "airquality", airquality);

        return res;
    }

    @Nullable
    private static BRLCrypt getAuth(String ipaddr, String macaddr)
    {
        BRLCrypt crypt;

        synchronized (deviceCrypt)
        {
            crypt = deviceCrypt.get(macaddr);
        }

        if (crypt != null) return crypt;

        byte[] result = sendCommand(ipaddr, macaddr, authCommand, getAuthPayload());
        if (result == null) return null;

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

    @Nullable
    private static byte[] sendCommand(String ipaddr, String macaddr, byte command, byte[] payload)
    {
        BRLCrypt crypt;

        if (command == authCommand)
        {
            crypt = new BRLCrypt();
        }
        else
        {
            crypt = getAuth(ipaddr, macaddr);

            if (crypt == null)
            {
                Log.e(LOGTAG, "sendCommand: no crypt!");

                return null;
            }
        }

        byte[] message = cmdPacket(macaddr, command, payload, crypt);

        byte[] response = sendToSocket(ipaddr, message);

        if (response == null)
        {
            Log.e(LOGTAG, "sendCommand: no response!");

            return null;
        }

        int err = (response[0x22] & 0xff) + ((response[0x23] & 0xff) << 8);

        if (err != 0)
        {
            Log.e(LOGTAG, "sendCommand: received error=" + Integer.toHexString(err));

            return null;
        }

        byte[] result = decryptFromDeviceMessage(response, crypt);

        if (result == null)
        {
            Log.e(LOGTAG, "sendCommand: decrypt failed!");
        }

        return result;
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

    private static byte[] enterLearningPayload()
    {
        byte[] data = new byte[16];

        data[0] = 3;

        return data;
    }

    private static byte[] getLearnedDataPayload()
    {
        byte[] data = new byte[16];

        data[0] = 4;

        return data;
    }

    private static byte[] getPowerStatusPayload()
    {
        byte[] data = new byte[16];

        data[0] = 1;

        return data;
    }

    private static byte[] getTempStatusPayload()
    {
        byte[] data = new byte[16];

        data[0] = 1;

        return data;
    }

    private static byte[] getSensorStatusPayload()
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
