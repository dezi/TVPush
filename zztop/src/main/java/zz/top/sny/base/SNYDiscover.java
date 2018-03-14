package zz.top.sny.base;

import android.support.annotation.Nullable;

import android.os.Build;
import android.text.Html;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URL;

import zz.top.utl.Json;
import zz.top.utl.Simple;

public class SNYDiscover
{
    private final static String LOGTAG = SNYDiscover.class.getSimpleName();

    private final static String BCAST_ADDR = "239.255.255.250";
    private final static int BCAST_PORT = 1900;

    public static ArrayList<String> dups;
    public static MulticastSocket socket;
    public static InetAddress bcastip;
    public static int bcastport;
    public static long exittime;

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

    public static void discover(int seconds)
    {
        try
        {
            bcastip = InetAddress.getByName(BCAST_ADDR);
            bcastport = BCAST_PORT;

            if (socket == null)
            {
                socket = new MulticastSocket(bcastport);
                socket.setReuseAddress(true);
                socket.setSoTimeout(15000);
                socket.joinGroup(bcastip);

                exittime = System.currentTimeMillis() + seconds * 1000;

                Thread search = new Thread(searchThread);
                search.start();

                byte[] txbuf = DISCOVER_MESSAGE_ROOTDEVICE.getBytes();
                socket.send(new DatagramPacket(txbuf, txbuf.length, bcastip, bcastport));
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private final static Runnable searchThread = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "searchThread: start.");

            dups = new ArrayList<>();

            while (System.currentTimeMillis() < exittime)
            {
                try
                {
                    byte[] rxbuf = new byte[8192];
                    DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);
                    socket.receive(packet);

                    String xml = new String(packet.getData(), packet.getOffset(), packet.getLength());

                    Log.d(LOGTAG, "searchThread:"
                            + " ip=" + packet.getAddress().toString()
                            + " port=" + packet.getPort()
                            //+ " xml=" + xml
                            );

                    String[] lines = xml.split("\r\n");

                    for (String line : lines)
                    {
                        if (! line.startsWith("LOCATION: ")) continue;

                        String urlstr = line.substring(10);
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

            Log.d(LOGTAG, "searchThread: done.");
        }
    };

    public static void buildDeviceDescription(String xmlfuck)
    {
        String ipAddr = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, ipAddrRegex));
        String friendlyName = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, friendlyNameRegex));
        String manufacturer = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, manufacturerRegex));
        String modelName = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, modelNameRegex));
        String UDN = SNYUtil.HTMLdefuck(SNYUtil.matchStuff(xmlfuck, UDNRegex));

        if (ipAddr == null) return;
        if (friendlyName == null) return;
        if (manufacturer == null) return;
        if (modelName == null) return;
        if (UDN == null) return;

        Log.d(LOGTAG, " ipAddr=" + ipAddr);
        Log.d(LOGTAG, " friendlyName=" + friendlyName);
        Log.d(LOGTAG, " manufacturer=" + manufacturer);
        Log.d(LOGTAG, " modelName=" + modelName);
        Log.d(LOGTAG, " UDN=" + UDN);

        if (! manufacturer.equals("Sony")) return;
        if (! modelName.startsWith("BRAVIA")) return;

        if (dups.contains(UDN)) return;
        dups.add(UDN);

        String ssid = Simple.getConnectedWifiName();

        String caps = "tvremote|stupid|select|poweronoff";

        JSONObject sonydev = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(sonydev, "device", device);

        JSONObject credentials = new JSONObject();
        Json.put(sonydev, "credentials", credentials);

        JSONObject network = new JSONObject();
        Json.put(sonydev, "network", network);

        Json.put(device, "uuid", UDN);

        Json.put(device, "type", "tvremote");
        Json.put(device, "name", friendlyName);
        Json.put(device, "nick", friendlyName);
        Json.put(device, "model", modelName);
        Json.put(device, "brand", manufacturer);

        Json.put(device, "capabilities", caps);
        Json.put(device, "driver", "sny");
        Json.put(device, "location", ssid);
        Json.put(device, "fixedwifi", ssid);

        Json.put(network, "ipaddr", ipAddr);
        Json.put(network, "ssid", ssid);

        //
        // UUID = 5b12df94-9e63-77bf-7c8c-d66a430994fb
        // COOKIE = 18DF5D5C3B06220A1D6186896BC1462CB2F74616
        //

        if (UDN.equals("5b12df94-9e63-77bf-7c8c-d66a430994fb"))
        {
            Json.put(credentials, "cookie", "18DF5D5C3B06220A1D6186896BC1462CB2F74616");
        }

        SNY.instance.onDeviceFound(sonydev);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", UDN);
        Json.put(status, "wifi", ssid);
        Json.put(status, "ipaddr", ipAddr);

        SNY.instance.onDeviceStatus(status);
    }

    @Nullable
    public static byte[] readHTTPData(String urlstr)
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
