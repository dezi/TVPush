package de.xavaro.android.tpl.comm;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import de.xavaro.android.tpl.base.TPL;
import de.xavaro.android.tpl.handler.TPLHandler;
import de.xavaro.android.tpl.handler.TPLHandlerSysInfo;
import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.simple.Simple;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class TPLDiscover
{
    private static final String LOGTAG = TPLDiscover.class.getSimpleName();

    public static final int TPLINK_PORT = 9999;

    public static void startService()
    {
        if ((TPL.instance != null) && (TPL.instance.discover == null))
        {
            TPL.instance.discover = new TPLDiscover();
            TPL.instance.discover.startThread();
        }
    }

    public static void stopService()
    {
        if ((TPL.instance != null) && (TPL.instance.discover != null))
        {
            TPL.instance.discover.stopThread();
            TPL.instance.discover = null;
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

            //
            // Discover local LAN devices.
            //

            try
            {
                socket = new DatagramSocket();
                socket.setSoTimeout(2000);
                socket.setBroadcast(true);

                String mess = "{\"system\":{\"get_sysinfo\":{}}}";

                byte[] helloPacket = TPLHandler.encryptMessage(mess);
                DatagramPacket hello = new DatagramPacket(helloPacket, helloPacket.length);

                hello.setAddress(InetAddress.getByName("255.255.255.255"));
                hello.setPort(TPLINK_PORT);

                socket.send(hello);
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
                long exittime = System.currentTimeMillis() + 20 * 1000;

                while ((discoverThread != null) && (System.currentTimeMillis() < exittime))
                {
                    try
                    {
                        byte[] rxbuf = new byte[2048];
                        DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);
                        socket.receive(packet);

                        String dupkey = packet.getAddress() + ":" + packet.getPort();
                        if (dupstuff.contains(dupkey)) continue;
                        dupstuff.add(dupkey);

                        String messstr = TPLHandler.decryptMessage(packet.getData(), 0, packet.getLength());

                        JSONObject message = Json.fromStringObject(messstr);
                        if (message == null) continue;

                        String ipaddr = packet.getAddress().toString().substring(1);
                        int ipport = packet.getPort();

                        TPLHandlerSysInfo.buildDeviceDescription(ipaddr, ipport, message, false);
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
}