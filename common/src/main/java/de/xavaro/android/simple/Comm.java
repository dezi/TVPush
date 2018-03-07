package de.xavaro.android.simple;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import zz.top.cam.Cameras;

public class Comm extends Thread
{
    private static final String LOGTAG = Comm.class.getSimpleName();

    private static final int bcast_port = 42742;
    private static final String bcast_addr = "239.255.255.250";

    private static MulticastSocket socket;
    private static InetAddress bcastip;

    private boolean running;

    private JSONObject myDevice;
    private JSONObject myCredentials;

    private JSONObject myHELO;
    private JSONObject myMEME;
    private JSONObject myGAUT;
    private JSONObject mySAUT;

    public Comm(Context context)
    {
        try
        {
            bcastip = InetAddress.getByName(bcast_addr);

            socket = new MulticastSocket(bcast_port);
            socket.setReuseAddress(true);
            socket.setSoTimeout(15000);
            socket.joinGroup(bcastip);

            Log.d(LOGTAG, "onCreate: MulticastSocket ip=" + bcast_addr + " port=" + bcast_port);
        }
        catch (Exception ex)
        {
            socket = null;
        }

        updateProfile(context);
    }

    public void updateProfile(Context context)
    {
        myDevice = new JSONObject();
        Json.put(myDevice, "name", Simple.getDeviceUserName(context));

        myCredentials = new JSONObject();

        if (Simple.getFCMToken() != null)
        {
            Json.put(myCredentials, "fcmtoken", Simple.getFCMToken());
        }

        myHELO = new JSONObject();
        Json.put(myHELO, "type", "HELO");
        Json.put(myHELO, "device", myDevice);

        myMEME = new JSONObject();
        Json.put(myMEME, "type", "MEME");
        Json.put(myMEME, "device", myDevice);

        myGAUT = new JSONObject();
        Json.put(myGAUT, "type", "GAUT");
        Json.put(myGAUT, "device", myDevice);

        mySAUT = new JSONObject();
        Json.put(mySAUT, "type", "SAUT");
        Json.put(mySAUT, "device", myDevice);
        Json.put(myMEME, "credentials", myCredentials);
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "run: started...");

        running = true;

        while (running)
        {
            try
            {
                byte[] rxbuff = new byte[ 8 * 1024 ];
                DatagramPacket rxpack = new DatagramPacket(rxbuff, rxbuff.length);
                socket.receive(rxpack);

                String message = new String(rxpack.getData(), 0, rxpack.getLength());

                if (message.length() == 4)
                {
                    Log.d(LOGTAG, "run: simple "
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " simple=" + message);
                    continue;
                }

                JSONObject jsonmess = Json.fromStringObject(message);

                if (jsonmess == null)
                {
                    Log.d(LOGTAG, "run: junk"
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " message=" + message);
                    continue;
                }

                String type = Json.getString(jsonmess, "type");

                if (type == null)
                {
                    Log.d(LOGTAG, "run: no type"
                            + " ip=" + rxpack.getAddress()
                            + " port=" + rxpack.getPort()
                            + " message=" + message);
                    continue;
                }

                JSONObject device = Json.getObject(jsonmess, "device");
                JSONObject credentials = Json.getObject(jsonmess, "credentials");

                String deviceName = Json.getString(device, "name");
                String deviceCategory = Json.getString(device, "category");

                Log.d(LOGTAG, "run:"
                        + " ip=" + rxpack.getAddress()
                        + " port=" + rxpack.getPort()
                        + " type=" + type
                        + " from=" + deviceName);

                //
                // Messages with responses.
                //

                JSONObject response = null;

                if ("HELO".equals(type))
                {
                    response = myMEME;
                }

                if ("MEME".equals(type))
                {
                    if ((deviceCategory != null) && deviceCategory.equalsIgnoreCase("camera"))
                    {
                        response = myGAUT;
                    }
                }

                if ("GAUT".equals(type))
                {
                    response = mySAUT;
                }

                if (response != null)
                {
                    type = Json.getString(response, "type");

                    byte[] txbuff = response.toString().getBytes();
                    DatagramPacket txpack = new DatagramPacket(txbuff, txbuff.length);
                    txpack.setAddress(rxpack.getAddress());
                    txpack.setPort(rxpack.getPort());

                    socket.send(txpack);

                    Log.d(LOGTAG, "run: send"
                            + " ip=" + txpack.getAddress()
                            + " port=" + txpack.getPort()
                            + " type=" + type);

                    continue;
                }

                //
                // Messages w/o responses.
                //

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

        socket.close();

        Log.d(LOGTAG, "run: finished...");
    }
}
