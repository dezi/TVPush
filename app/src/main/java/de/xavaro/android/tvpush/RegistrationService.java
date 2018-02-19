package de.xavaro.android.tvpush;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import de.xavaro.android.common.Json;

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

    public static void requestHello()
    {
        if (socket != null)
        {
            String fcm_token = FirebaseInstanceId.getInstance().getToken();
            Log.d(LOGTAG, "requestHello: fcm_token=" + fcm_token);

            JSONObject helojson = new JSONObject();

            Json.put(helojson, "type", "HELO");
            Json.put(helojson, "fcmtoken", fcm_token);

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

        Toast.makeText(this, "RegistrationService created", Toast.LENGTH_LONG).show();

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "RegistrationService started", Toast.LENGTH_LONG).show();

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

        Toast.makeText(this, "RegistrationService Stopped", Toast.LENGTH_LONG).show();
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

                JSONObject jmess = Json.fromStringObject(message);
                if (jmess == null) continue;

                if (Json.equals(jmess, "type", "HELO"))
                {
                    String fcm_token = FirebaseInstanceId.getInstance().getToken();
                    Log.d(LOGTAG, "workerThread: fcm_token=" + fcm_token);

                    JSONObject mejson = new JSONObject();

                    Json.put(mejson, "type", "MEME");
                    Json.put(mejson, "fcmtoken", fcm_token);

                    byte[] txbuf = mejson.toString().getBytes();
                    DatagramPacket meme = new DatagramPacket(txbuf, txbuf.length);
                    meme.setAddress(multicastAddress);
                    meme.setPort(port);

                    socket.send(meme);
                }
            }
            catch (Exception ex)
            {
                running = false;
            }
        }

        worker = null;

        Log.d(LOGTAG, "workerThread: finished...");
    }
}
