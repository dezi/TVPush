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

import de.xavaro.android.common.Json;
import de.xavaro.android.common.Simple;

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
            Json.put(helojson, "device_name", Simple.getDeviceUserName(context));
            Json.put(helojson, "fcmtoken", Simple.getFCMToken());

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
            }
            catch (Exception ignore)
            {
            }
        }
    }

    private SpeechRecognition recognition;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "RegistrationService started", Toast.LENGTH_SHORT).show();

        if (worker == null)
        {
            worker = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    workerThread();
                }
            });

            worker.start();
        }

        if (recognition == null)
        {
            recognition = new SpeechRecognition(this);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy...");

        if (running)
        {
            running = false;

            try
            {
                if (worker != null)
                {
                    worker.join();
                }
            }
            catch (InterruptedException ignore)
            {
            }
        }

        Toast.makeText(this, "RegistrationService Stopped", Toast.LENGTH_SHORT).show();
    }

    private void workerThread()
    {
        Log.d(LOGTAG, "workerThread: started...");

        running = true;

        while (running)
        {
            try
            {
                byte[] rxbuf = new byte[ 8192 ];
                DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                //Log.d(LOGTAG, "####" + message);

                JSONObject jsonmess = Json.fromStringObject(message);
                if (jsonmess == null) continue;

                if (Json.equals(jsonmess, "type", "HELO"))
                {
                    Log.d(LOGTAG, "workerThread: HELO from=" + Json.getString(jsonmess, "device_name"));

                    JSONObject mejson = new JSONObject();

                    Json.put(mejson, "type", "MEME");
                    Json.put(mejson, "device_name", Simple.getDeviceUserName(this));
                    Json.put(mejson, "fcmtoken", Simple.getFCMToken());

                    byte[] txbuf = mejson.toString().getBytes();
                    DatagramPacket meme = new DatagramPacket(txbuf, txbuf.length);
                    meme.setAddress(multicastAddress);
                    meme.setPort(port);

                    socket.send(meme);
                }

                if (Json.equals(jsonmess, "type", "MEME"))
                {
                    String devicename = Json.getString(jsonmess, "device_name");
                    if (devicename == null) devicename = Json.getString(jsonmess, "devicename");

                    Log.d(LOGTAG, "workerThread: MEME from=" + devicename);
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
