package de.xavaro.android.tvpush;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import de.xavaro.android.simple.Comm;
import zz.top.cam.Cameras;

import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Simple;

public class RegistrationService extends Service
{
    private final static String LOGTAG = RegistrationService.class.getSimpleName();

    private static final int port = 42742;
    private static InetAddress multicastAddress;
    private static MulticastSocket socket;

    private boolean running = false;
    private Thread worker = null;

    public static void startService(Context context)
    {
        Intent serviceIntent = new Intent(context, RegistrationService.class);
        context.startService(serviceIntent);
    }

    public static void requestHello(Context context)
    {
        if (socket != null)
        {
            JSONObject helojson = new JSONObject();
            Json.put(helojson, "type", "HELO");

            JSONObject devicejson = new JSONObject();
            Json.put(devicejson, "name", Simple.getDeviceUserName(context));

            Json.put(helojson, "device", devicejson);

            byte[] txbuf = helojson.toString().getBytes();
            DatagramPacket helo = new DatagramPacket(txbuf, txbuf.length);
            helo.setAddress(multicastAddress);
            helo.setPort(port);

            try
            {
                socket.send(helo);
            }
            catch (IOException ignore)
            {
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOGTAG, "onBind...");

        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        Toast.makeText(this, "RegistrationService created", Toast.LENGTH_SHORT).show();

        if (socket == null)
        {
            try
            {
                multicastAddress = InetAddress.getByName("239.255.255.250");

                socket = new MulticastSocket(port);
                socket.setReuseAddress(true);
                socket.setSoTimeout(15000);
                socket.joinGroup(multicastAddress);

                Log.d(LOGTAG, "onCreate: getReceiveBufferSize=" + socket.getReceiveBufferSize());

                requestHello(this);
                requestHello(this);
                requestHello(this);
            }
            catch (Exception ignore)
            {
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "RegistrationService started", Toast.LENGTH_SHORT).show();

        if (worker == null)
        {
            worker = new Comm(this);
            worker.start();
        }

        return START_NOT_STICKY; //START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy...");

        try
        {
            if (worker != null)
            {
                worker.interrupt();
                worker.join();
                worker = null;
            }
        }
        catch (InterruptedException ignore)
        {
        }

        Toast.makeText(this, "RegistrationService Stopped", Toast.LENGTH_SHORT).show();
    }

    private void workerThread()
    {
        Log.d(LOGTAG, "workerThread: started...");

        running = true;

        while (running)
        {
            JSONObject myDevice = new JSONObject();
            Json.put(myDevice, "name", Simple.getDeviceUserName(this));

            JSONObject myCredentials = new JSONObject();

            if (Simple.getFCMToken() != null)
            {
                Json.put(myCredentials, "fcmtoken", Simple.getFCMToken());
            }

            JSONObject myMEME = new JSONObject();
            Json.put(myMEME, "type", "MEME");
            Json.put(myMEME, "device", myDevice);
            Json.put(myMEME, "credentials", myCredentials);

            JSONObject myGAUT = new JSONObject();
            Json.put(myGAUT, "type", "GAUT");
            Json.put(myGAUT, "device", myDevice);

            try
            {
                byte[] rxbuf = new byte[ 8 * 1024 ];
                DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                if (message.length() == 4)
                {
                    Log.d(LOGTAG, "workerThread: " + message);
                    continue;
                }

                JSONObject jsonmess = Json.fromStringObject(message);
                if (jsonmess == null) continue;

                String type = Json.getString(jsonmess, "type");
                if (type == null) continue;

                JSONObject device = Json.getObject(jsonmess, "device");
                JSONObject credentials = Json.getObject(jsonmess, "credentials");

                String deviceName = Json.getString(device, "name");
                String deviceCategory = Json.getString(device, "category");

                Log.d(LOGTAG, "workerThread:"
                        + " type=" + type
                        + " from=" + deviceName
                        + " ip=" + packet.getAddress()
                        + " port=" + packet.getPort());

                if ("HELO".equals(type))
                {
                    byte[] txbuf = myMEME.toString().getBytes();
                    DatagramPacket meme = new DatagramPacket(txbuf, txbuf.length);
                    meme.setAddress(packet.getAddress());
                    meme.setPort(packet.getPort());

                    socket.send(meme);
                }

                if ("MEME".equals(type))
                {
                    if ((deviceCategory != null) && deviceCategory.equalsIgnoreCase("camera"))
                    {
                        byte[] txbuf = myGAUT.toString().getBytes();
                        DatagramPacket gaut = new DatagramPacket(txbuf, txbuf.length);
                        gaut.setAddress(packet.getAddress());
                        gaut.setPort(packet.getPort());

                        socket.send(gaut);

                        Log.d(LOGTAG, "workerThread: GAUT to=" + deviceName);
                    }
                }

                if ("SAUT".equals(type))
                {
                    Log.d(LOGTAG, "workerThread: SAUT from=" + deviceName + " cat=" + deviceCategory);

                    if ((deviceCategory != null) && deviceCategory.equalsIgnoreCase("camera"))
                    {
                        Cameras.addCamera(jsonmess);
                    }
                }
            }
            catch (SocketTimeoutException ignore)
            {
                //
                // Do nothing.
                //
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                running = false;
            }
        }

        worker = null;

        Log.d(LOGTAG, "workerThread: finished...");
    }
}
