package de.xavaro.android.edx.comm;

import android.annotation.SuppressLint;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import de.xavaro.android.edx.base.EDX;
import de.xavaro.android.edx.simple.Json;
import de.xavaro.android.edx.simple.Simple;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class EDXDiscover
{
    private static final String LOGTAG = EDXDiscover.class.getSimpleName();

    private static final int DISCOVERY_PACKET_LENGTH = 22;
    private static final int DISCOVERY_AGENT_PORT = 20560;

    private static final int PACKET_STATUS_REQUEST = 0;
    private static final int PACKET_STATUS_RESPONSE = 1;
    private static final int PACKET_STATUS_FAULT = 2;

    private static final int RECEIVER_SIZE = 186;

    private static final int PACKET_OFFSET_MAC_ADDR = 0;

    private static final int PACKET_OFFSET_STATUS = 18;

    private static final int PACKET_OFFSET_UNKNOWN_STATUS = 19;

    private static final int PACKET_OFFSET_MODEL_NAME = 22;
    private static final int PACKET_LENGTH_MODEL_NAME = 14;

    private static final int PACKET_OFFSET_FIRMWARE_VERSION = 36;
    private static final int PACKET_LENGTH_FIRMWARE_VERSION = 8;

    private static final int PACKET_OFFSET_NAME = 44;
    private static final int PACKET_LENGTH_NAME = 128;

    private static final int PACKET_OFFSET_WEB_PORT = 172;

    private static final int PACKET_OFFSET_IP_ADDR = 174;
    private static final int PACKET_OFFSET_SUBNET = 178;
    private static final int PACKET_OFFSET_GATEWAY = 182;

    private static final String IPV4_ADDR_FORMAT_STR = "%d.%d.%d.%d";
    private static final String MAC_ADDR_FORMAT_STR = "%02x:%02x:%02x:%02x:%02x:%02x";

    public static void startService()
    {
        if ((EDX.instance != null) && (EDX.instance.discover == null))
        {
            EDX.instance.discover = new EDXDiscover();
        }
    }

    public static void stopService()
    {
        if ((EDX.instance != null) && (EDX.instance.discover != null))
        {
            EDXDiscover discover = EDX.instance.discover;

            synchronized (discover.mutex)
            {
                if (discover.discoverThread != null)
                {
                    discover.discoverThread.interrupt();
                    discover.discoverThread = null;
                }
            }

            EDX.instance.discover = null;
        }
    }

    private Thread discoverThread;
    private DatagramSocket socket;
    private final Object mutex = new Object();

    private EDXDiscover()
    {
        try
        {
            socket = new DatagramSocket();
            socket.setSoTimeout(2000);
            socket.setBroadcast(true);

            discoverThread = new Thread(discoverRunnable);
            discoverThread.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private final Runnable discoverRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "discoverRunnable: start.");

            //
            // Discover devices and credentials from Edimax cloud.
            //

            EDXCloud.discoverDevices();

            //
            // Discover local LAN devices.
            //

            try
            {
                byte[] helloPacket = getDiscoveryHeader();
                InetAddress ipbroadcast = InetAddress.getByName("255.255.255.255");
                DatagramPacket hello = new DatagramPacket(helloPacket, helloPacket.length, ipbroadcast, DISCOVERY_AGENT_PORT);

                socket.send(hello);
            }
            catch (Exception ignore)
            {
            }

            ArrayList<String> dupstuff = new ArrayList<>();
            long exittime = System.currentTimeMillis() + 10 * 1000;

            while ((discoverThread != null) && (System.currentTimeMillis() < exittime))
            {
                try
                {
                    byte[] rxbuf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);
                    socket.receive(packet);

                    String dupkey = packet.getAddress() + ":" + packet.getPort();
                    if (dupstuff.contains(dupkey)) continue;
                    dupstuff.add(dupkey);

                    buildDeviceDescription(packet);
                }
                catch (Exception ignore)
                {
                }
            }

            socket.close();
            socket = null;

            synchronized (mutex)
            {
                discoverThread = null;
            }

            Log.d(LOGTAG, "discoverRunnable: done.");
        }
    };

    private static byte[] getDiscoveryHeader()
    {
        byte[] data = new byte[DISCOVERY_PACKET_LENGTH];

        data[0] = (byte) 0xff;
        data[1] = (byte) 0xff;
        data[2] = (byte) 0xff;
        data[3] = (byte) 0xff;
        data[4] = (byte) 0xff;
        data[5] = (byte) 0xff;

        data[6] = (byte) 'E';
        data[7] = (byte) 'D';
        data[8] = (byte) 'I';
        data[9] = (byte) 'M';
        data[10] = (byte) 'A';
        data[11] = (byte) 'X';

        data[12] = 0;
        data[13] = 0;
        data[14] = 0;
        data[15] = 0;
        data[16] = 0;
        data[17] = 0;

        data[18] = (byte) 0;
        data[19] = (byte) -95;
        data[20] = (byte) -1;
        data[21] = (byte) 94;

        return data;
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("PointlessArithmeticExpression")
    private static void buildDeviceDescription(DatagramPacket packet)
    {
        byte[] data = packet.getData();
        int dlen = packet.getLength();

        if ((RECEIVER_SIZE > dlen)
            || (data[PACKET_OFFSET_STATUS] == PACKET_STATUS_FAULT)
            || (data[PACKET_OFFSET_STATUS] != PACKET_STATUS_RESPONSE)
            || (data[PACKET_OFFSET_UNKNOWN_STATUS] != -95))
        {
            Log.e(LOGTAG, "buildDeviceDescription: packet invalid!");

            return;
        }

        String name = new String(data, PACKET_OFFSET_NAME, PACKET_LENGTH_NAME).trim();
        String model = new String(data, PACKET_OFFSET_MODEL_NAME, PACKET_LENGTH_MODEL_NAME).trim();
        String version = new String(data, PACKET_OFFSET_FIRMWARE_VERSION, PACKET_LENGTH_FIRMWARE_VERSION).trim();

        int ipport = ((data[PACKET_OFFSET_WEB_PORT + 1] & 0xff) << 8) + (data[PACKET_OFFSET_WEB_PORT] & 0xff);

        String ipaddr = String.format(IPV4_ADDR_FORMAT_STR,
                data[PACKET_OFFSET_IP_ADDR + 0] & 0xff,
                data[PACKET_OFFSET_IP_ADDR + 1] & 0xff,
                data[PACKET_OFFSET_IP_ADDR + 2] & 0xff,
                data[PACKET_OFFSET_IP_ADDR + 3] & 0xff);

        String macaddr = String.format(MAC_ADDR_FORMAT_STR,
                data[PACKET_OFFSET_MAC_ADDR + 0],
                data[PACKET_OFFSET_MAC_ADDR + 1],
                data[PACKET_OFFSET_MAC_ADDR + 2],
                data[PACKET_OFFSET_MAC_ADDR + 3],
                data[PACKET_OFFSET_MAC_ADDR + 4],
                data[PACKET_OFFSET_MAC_ADDR + 5]);

        String uuid = Simple.hmacSha1UUID(model, macaddr);
        String ssid = Simple.getConnectedWifiName();
        String caps = EDXUtil.getCapabilities(model);

        JSONObject edimax = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(edimax, "device", device);

        JSONObject network = new JSONObject();
        Json.put(edimax, "network", network);

        Json.put(device, "uuid", uuid);
        Json.put(device, "name", name);
        Json.put(device, "nick", name);
        Json.put(device, "model", model);
        Json.put(device, "type", "smartplug");
        Json.put(device, "driver", "edx");
        Json.put(device, "brand", "edimax");
        Json.put(device, "macaddr", macaddr);
        Json.put(device, "version", version);

        Json.put(device, "capabilities", caps);
        Json.put(device, "location", ssid);
        Json.put(device, "fixedwifi", ssid);

        Json.put(network, "ipaddr", ipaddr);
        Json.put(network, "ssid", ssid);

        EDX.instance.onDeviceFound(edimax);

        //
        // Device status.
        //

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);
        Json.put(status, "wifi", ssid);
        Json.put(status, "ipaddr", ipaddr);
        Json.put(status, "ipport", ipport);

        Log.d(LOGTAG, "buildDeviceDescription: device=" + Json.toPretty(edimax));

        //
        // Check for credentials and aquire status if all set.
        //

        JSONObject credential = EDX.instance.onGetCredentialRequest(uuid);
        JSONObject credentials = Json.getObject(credential, "credentials");
        String user = Json.getString(credentials, "localUser");
        String pass = Json.getString(credentials, "localPass");

        if ((user != null) && (pass != null))
        {
            boolean on = EDXCommand.getPowerStatus(ipaddr, ipport, user, pass);
            Json.put(status, "plugstate", on ? 1 : 0);
        }

        EDX.instance.onDeviceStatus(status);

        Log.d(LOGTAG, "buildDeviceDescription: status=" + Json.toPretty(status));
    }
}