package zz.top.sny.base;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.InputStream;
import java.util.ArrayList;
import java.net.URL;

import zz.top.utl.Simple;
import zz.top.utl.Json;
import zz.top.utl.Log;

public class SNYDiscover
{
    private final static String LOGTAG = SNYDiscover.class.getSimpleName();

    private final static String BCAST_ADDR = "239.255.255.250";
    private final static int BCAST_PORT = 1900;

    private final static String DISCOVER_MESSAGE_ROOTDEVICE = ""
            + "M-SEARCH * HTTP/1.1\r\n"
            + "HOST: 239.255.255.250:1900\r\n"
            + "MAN: \"ssdp:discover\"\r\n"
            + "ST: ssdp:all\r\n"
            + "MX: 1\r\n"
            + "\r\n";

    private final static String ipAddrRegex = "<URLBase>http:\\/\\/([0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*)[^<]*<\\/URLBase>";
    private final static String friendlyNameRegex = "<friendlyName>([^<]*)<\\/friendlyName>";
    private final static String manufacturerRegex = "<manufacturer>([^<]*)<\\/manufacturer>";
    private final static String modelNameRegex = "<modelName>([^<]*)<\\/modelName>";
    private final static String UDNRegex = "<UDN>uuid:([^<]*)<\\/UDN>";

    public static void startService()
    {
        if ((SNY.instance != null) && (SNY.instance.discover == null))
        {
            SNY.instance.discover = new SNYDiscover();
        }
    }

    public static void stopService()
    {
        if ((SNY.instance != null) && (SNY.instance.discover != null))
        {
            SNYDiscover discover = SNY.instance.discover;

            synchronized (discover.mutex)
            {
                if (discover.searchThread != null)
                {
                    discover.searchThread.interrupt();
                    discover.searchThread = null;
                }
            }

            SNY.instance.discover = null;
        }
    }

    private final Object mutex = new Object();

    private Thread searchThread;
    private MulticastSocket socket;
    private long exittime;

    private SNYDiscover()
    {
        try
        {
            InetAddress bcastip = InetAddress.getByName(BCAST_ADDR);

            socket = new MulticastSocket();
            socket.setReuseAddress(true);
            socket.setSoTimeout(1000);
            socket.joinGroup(bcastip);

            exittime = System.currentTimeMillis() + 10 * 1000;

            searchThread = new Thread(searchRunnable);
            searchThread.start();

            byte[] txbuf = DISCOVER_MESSAGE_ROOTDEVICE.getBytes();
            socket.send(new DatagramPacket(txbuf, txbuf.length, bcastip, BCAST_PORT));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final Runnable searchRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "searchRunnable: start.");

            Log.d(LOGTAG, "searchRunnable: self=" + Simple.getConnectedWifiIPAddress());

            ArrayList<String> dupstuff = new ArrayList<>();

            while ((searchThread != null) && (System.currentTimeMillis() < exittime))
            {
                try
                {
                    byte[] rxbuf = new byte[8192];
                    DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);
                    socket.receive(packet);

                    String dupkey = packet.getAddress() + ":" + packet.getPort();
                    if (dupstuff.contains(dupkey)) continue;
                    dupstuff.add(dupkey);

                    String xml = new String(packet.getData(), packet.getOffset(), packet.getLength());

                    Log.d(LOGTAG, "searchRunnable: recv"
                            + " ip=" + packet.getAddress().toString().substring(1)
                            + " port=" + packet.getPort()
                            );

                    String[] lines = xml.split("\r\n");

                    for (String line : lines)
                    {
                        if (! line.startsWith("LOCATION: ")) continue;

                        String urlstr = line.substring(10);
                        Log.d(LOGTAG, "searchRunnable: LOCATION=" + urlstr);

                        if (dupstuff.contains(urlstr)) continue;
                        dupstuff.add(urlstr);

                        byte[] data = readHTTPData(urlstr);
                        if (data == null) continue;

                        buildDeviceDescription(new String(data));

                        break;
                    }
                }
                catch (Exception ignore)
                {
                }
            }

            socket.close();
            socket = null;

            synchronized (mutex)
            {
                searchThread = null;
            }

            Log.d(LOGTAG, "searchRunnable: done.");
        }
    };

    private void buildDeviceDescription(String xmlfuck)
    {
        String ipAddr = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, ipAddrRegex));
        String friendlyName = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, friendlyNameRegex));
        String manufacturer = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, manufacturerRegex));
        String modelName = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, modelNameRegex));
        String UDN = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, UDNRegex));

        if ((ipAddr == null) || (friendlyName == null) || (UDN == null)) return;
        if ((manufacturer == null) || ! manufacturer.equals("Sony")) return;
        if ((modelName == null) || ! modelName.startsWith("BRAVIA")) return;

        Log.d(LOGTAG, " ipAddr=" + ipAddr);
        Log.d(LOGTAG, " friendlyName=" + friendlyName);
        Log.d(LOGTAG, " manufacturer=" + manufacturer);
        Log.d(LOGTAG, " modelName=" + modelName);
        Log.d(LOGTAG, " UDN=" + UDN);

        String ssid = Simple.getConnectedWifiName();

        String caps = "tvremote|stupid|hosted|pincode|select|poweronoff";

        JSONObject sonyremote = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(sonyremote, "device", device);

        JSONObject network = new JSONObject();
        Json.put(sonyremote, "network", network);

        Json.put(device, "uuid", UDN);

        Json.put(device, "type", "tvremote");
        Json.put(device, "driver", "sny");
        Json.put(device, "name", friendlyName);
        Json.put(device, "nick", friendlyName);
        Json.put(device, "model", modelName);
        Json.put(device, "brand", manufacturer);

        Json.put(device, "capabilities", caps);
        Json.put(device, "location", ssid);
        Json.put(device, "fixedwifi", ssid);

        Json.put(network, "ipaddr", ipAddr);
        Json.put(network, "ssid", ssid);

        SNY.instance.onDeviceFound(sonyremote);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", UDN);
        Json.put(status, "wifi", ssid);
        Json.put(status, "ipaddr", ipAddr);

        SNY.instance.onDeviceStatus(status);

        if (ipAddr.equals(Simple.getConnectedWifiIPAddress()))
        {
            SNYPrograms.importSDB(UDN);
        }

        if (ipAddr.equals(Simple.getConnectedWifiIPAddress()))
        {
            JSONObject credentials = SNY.instance.getDeviceCredentials(UDN);
            String authtoken = Json.getString(credentials, "authtoken");
            Long expires = Json.getLong(credentials, "expires");
            Long deadline = System.currentTimeMillis();

            deadline += 4 * 86400 * 1000;

            if ((authtoken == null) || (deadline > expires))
            {
                String username = Simple.getDeviceUserName();

                SNYAuthorize.requestAuth(ipAddr, UDN, friendlyName, username);
            }
        }
    }

    @Nullable
    private byte[] readHTTPData(String urlstr)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[ 8196 ];
            int xfer;

            while ((xfer = input.read(buffer)) >= 0)
            {
                output.write(buffer, 0, xfer);
            }

            input.close();

            return output.toByteArray();
        }
        catch (Exception ignore)
        {
        }

        return null;
    }
}
