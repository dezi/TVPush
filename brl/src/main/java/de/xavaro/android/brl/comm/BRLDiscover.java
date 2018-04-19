package de.xavaro.android.brl.comm;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.xavaro.android.brl.base.BRL;
import de.xavaro.android.brl.simple.Json;
import de.xavaro.android.brl.simple.Simple;

public class BRLDiscover
{
    private static final String LOGTAG = BRLDiscover.class.getSimpleName();

    public static final int BRL_COMM_PORT = 80;

    private static final int DISCOVERY_PACKET_LENGTH = 48;
    private static final int PACKET_OFFSET_MAC_ADDR = 0x3a;

    private static final String MAC_ADDR_FORMAT_STR = "%02x:%02x:%02x:%02x:%02x:%02x";

    public static void startService()
    {
        if ((BRL.instance != null) && (BRL.instance.discover == null))
        {
            BRL.instance.discover = new BRLDiscover();
            BRL.instance.discover.startThread();
        }
    }

    public static void stopService()
    {
        if ((BRL.instance != null) && (BRL.instance.discover != null))
        {
            BRL.instance.discover.stopThread();
            BRL.instance.discover = null;
        }
    }

    private Thread discoverThread;
    private DatagramSocket socket;
    private final Object mutex = new Object();

    private void startThread()
    {
        synchronized (mutex)
        {
            if (discoverThread == null)
            {
                discoverThread = new Thread(discoverRunnable);
                discoverThread.start();
            }
        }
    }

    private void stopThread()
    {
        synchronized (mutex)
        {
            if (discoverThread != null)
            {
                discoverThread.interrupt();
                discoverThread = null;
            }
        }
    }

    private final Runnable discoverRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "discoverRunnable: start.");

            String ipaddr = Simple.getConnectedWifiIPAddress();
            if (ipaddr == null) return;

            byte[] helloPacket = getDiscoveryPacket(ipaddr, 0);
            DatagramPacket hello = new DatagramPacket(helloPacket, helloPacket.length);

            //
            // Discover local LAN devices.
            //

            try
            {
                socket = new DatagramSocket();
                socket.setSoTimeout(1000);
                socket.setBroadcast(true);

                hello.setAddress(InetAddress.getByName("255.255.255.255"));
                hello.setPort(BRL_COMM_PORT);
            }
            catch (Exception ignore)
            {
            }

            //
            // Collect responses for a certain time.
            //

            if (socket != null)
            {
                ArrayList<String> dupstuff = new ArrayList<>();
                long exittime = System.currentTimeMillis() + 10 * 1000;

                while ((discoverThread != null) && (System.currentTimeMillis() < exittime))
                {
                    try
                    {
                        socket.send(hello);

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
            }

            synchronized (mutex)
            {
                discoverThread = null;
            }

            Log.d(LOGTAG, "discoverRunnable: done.");
        }
    };

    @SuppressWarnings("SameParameterValue")
    private byte[] getDiscoveryPacket(String ipaddr, int ipport)
    {
        byte[] data = new byte[DISCOVERY_PACKET_LENGTH];

        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();

        int rawOffset = tz.getRawOffset();
        int tzOffset = rawOffset / 3600;

        //Log.d(LOGTAG, "getDiscoveryPacket: Raw offset=" + rawOffset);
        //Log.d(LOGTAG, "getDiscoveryPacket: Calculated offset getRawOffset/1000/-3600=" + tzOffset);

        int min = cal.get(Calendar.MINUTE);
        int hr = cal.get(Calendar.HOUR);

        int year = cal.get(Calendar.YEAR);
        int dayOfWk = dayOfWeekConv(cal.get(Calendar.DAY_OF_WEEK));
        int dayOfMn = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;

        //Log.d(LOGTAG, "getDiscoveryPacket: min=" + min + " hr=" + hr);
        //Log.d(LOGTAG, "getDiscoveryPacket: year=" + year + " dayOfWk=" + dayOfWk);
        //Log.d(LOGTAG, "getDiscoveryPacket: dayOfMn=" + dayOfMn + " month=" + month);

        if (tzOffset < 0)
        {
            data[0x08] = (byte) (0xff + tzOffset - 1);
            data[0x09] = (byte) 0xff;
            data[0x0a] = (byte) 0xff;
            data[0x0b] = (byte) 0xff;

            //Log.d(LOGTAG, "getDiscoveryPacket: tzOffset<0: 0x08=" + Integer.toHexString(0xff + tzOffset - 1) + " 0x09-0x0b=0xff");
        }
        else
        {
            data[0x08] = (byte) tzOffset;
            data[0x09] = (byte) 0x00;
            data[0x0a] = (byte) 0x00;
            data[0x0b] = (byte) 0x00;

            //Log.d(LOGTAG, "getDiscoveryPacket: tzOffset>0: 0x08=" + Integer.toHexString(tzOffset) + " 0x09-0x0b=0x00");
        }

        data[0x0c] = (byte) (year & 0xff);
        data[0x0d] = (byte) (year >> 8);

        data[0x0e] = (byte) min;
        data[0x0f] = (byte) hr;

        data[0x10] = (byte) (year % 100);

        data[0x11] = (byte) dayOfWk;
        data[0x12] = (byte) dayOfMn;
        data[0x13] = (byte) month;

        String[] ipparts = ipaddr.split("\\.");

        if (ipparts.length == 4)
        {
            data[0x18] = (byte) (Integer.parseInt(ipparts[0]) & 0xff);
            data[0x19] = (byte) (Integer.parseInt(ipparts[1]) & 0xff);
            data[0x1a] = (byte) (Integer.parseInt(ipparts[2]) & 0xff);
            data[0x1b] = (byte) (Integer.parseInt(ipparts[3]) & 0xff);
        }

        data[0x1c] = (byte) (ipport & 0xff);
        data[0x1d] = (byte) (ipport >> 8);

        data[0x26] = 6;

        short checksum = (short) 0xbeaf;

        for (byte bite : data)
        {
            checksum += bite & 0xff;
        }

        data[0x20] = (byte) (checksum & 0xff);
        data[0x21] = (byte) (checksum >> 8);

        Log.d(LOGTAG, "getDiscoveryPacket: checksum=" + Integer.toHexString(checksum & 0xffff));

        return data;
    }

    private static int dayOfWeekConv(int fieldVal)
    {
        // @formatter:off

        switch (fieldVal)
        {
            case Calendar.SUNDAY:    return 6;
            case Calendar.MONDAY:    return 0;
            case Calendar.TUESDAY:   return 1;
            case Calendar.WEDNESDAY: return 2;
            case Calendar.THURSDAY:  return 3;
            case Calendar.FRIDAY:    return 4;
            case Calendar.SATURDAY:  return 5;
        }

        // @formatter:on

        return -1;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void buildDeviceDescription(DatagramPacket packet)
    {
        byte[] data = packet.getData();
        int dlen = packet.getLength();

        String ipaddr = packet.getAddress().getHostAddress();

        String macaddr = String.format(MAC_ADDR_FORMAT_STR,
                data[PACKET_OFFSET_MAC_ADDR + 0],
                data[PACKET_OFFSET_MAC_ADDR + 1],
                data[PACKET_OFFSET_MAC_ADDR + 2],
                data[PACKET_OFFSET_MAC_ADDR + 3],
                data[PACKET_OFFSET_MAC_ADDR + 4],
                data[PACKET_OFFSET_MAC_ADDR + 5]);

        int deviceType = (data[0x34] & 0xff) + ((data[0x35] & 0xff) << 8);
        String model = BRLTypes.getFriendlyName(deviceType);

        Log.d(LOGTAG, "buildDeviceDescription:"
                + " dlen=" + dlen
                + " ipaddr=" + ipaddr
                + " ipport=" + packet.getPort()
                + " macaddr=" + macaddr
                + " deviceType=0x" + Integer.toHexString(deviceType & 0xffff)
                + " model=" + model
        );

        String uuid = Simple.hmacSha1UUID(Integer.valueOf(deviceType).toString(), macaddr);
        String ssid = Simple.getConnectedWifiName();
        String caps = BRLUtil.getCapabilities(model);

        int ipport = packet.getPort();

        JSONObject broadlink = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(broadlink, "device", device);

        JSONObject network = new JSONObject();
        Json.put(broadlink, "network", network);

        Json.put(device, "uuid", uuid);
        Json.put(device, "name", model);
        Json.put(device, "model", model);
        Json.put(device, "type", "smartplug");
        Json.put(device, "driver", "brl");
        Json.put(device, "brand", "Broadlink");
        Json.put(device, "macaddr", macaddr);
        Json.put(device, "version", "1.0");

        Json.put(device, "capabilities", caps);
        Json.put(device, "location", ssid);
        Json.put(device, "fixedwifi", ssid);

        Json.put(network, "ipaddr", ipaddr);
        Json.put(network, "ssid", ssid);

        BRL.instance.onDeviceFound(broadlink);

        //Log.d(LOGTAG, "buildDeviceDescription: device=" + Json.toPretty(broadlink));

        //
        // Device status.
        //

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);
        Json.put(status, "wifi", ssid);
        Json.put(status, "ipaddr", ipaddr);
        Json.put(status, "ipport", ipport);

        //
        // Aquire status if all set.
        //

        if (BRLUtil.containsCap("smartplug", caps))
        {
            Integer res = BRLCommand.getPowerStatus(ipaddr, macaddr);
            if (res != null) Json.put(status, "plugstate", res);
        }

        if (BRLUtil.containsCap("temperature", caps))
        {
            Double temp = BRLCommand.getTemperature(ipaddr, macaddr);
            if (temp != null) Json.put(status, "temperature", temp);
        }

        if (BRLUtil.containsCap("sensor", caps))
        {
            JSONObject sensor = BRLCommand.getSensorData(ipaddr, macaddr);

            if (sensor != null)
            {
                Json.put(status, "temperature", Json.getDouble(sensor, "temperature"));
                Json.put(status, "humidity", Json.getDouble(sensor, "humidity"));

                Json.put(status, "lightlevel", Json.getInt(sensor, "lightlevel"));
                Json.put(status, "noiselevel", Json.getInt(sensor, "noiselevel"));
                Json.put(status, "airquality", Json.getInt(sensor, "airquality"));
            }
        }

        if (BRLUtil.containsCap("irremote", caps))
        {
            BRLCommand.enterLearning(ipaddr, macaddr);

            try
            {
                Thread.sleep(10 * 1000);
            }
            catch (Exception ignore)
            {
            }

            BRLCommand.getLearnedData(ipaddr, macaddr);

        }

        BRL.instance.onDeviceStatus(status);

        //Log.d(LOGTAG, "buildDeviceDescription: status=" + Json.toPretty(status));
    }
}