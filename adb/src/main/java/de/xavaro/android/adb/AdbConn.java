package de.xavaro.android.adb;

import android.content.Context;
import android.support.annotation.Nullable;

import android.util.SparseArray;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Closeable;
import java.net.Socket;

@SuppressWarnings({"WeakerAccess", "unused", "SynchronizationOnLocalVariableOrMethodParameter"})
public class AdbConn implements Closeable
{
    private static final String LOGTAG = AdbConn.class.getSimpleName();

    private Context context;
    private String ipaddr;
    private int ipport;

    private int maxData;
    private int lastlocId;

    private boolean attempted;
    private boolean connected;
    private boolean sentSignature;

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private AdbAuth adbAuth;
    private Thread connThread;

    private final SparseArray<AdbStream> openStreams = new SparseArray<>();

    public AdbConn(Context context, String ipaddr, int ipport)
    {
        this.context = context;

        this.ipaddr = ipaddr;
        this.ipport = ipport;
    }

    public boolean connect()
    {
        Log.d(LOGTAG, "connect: start connect ip=" + ipaddr + " port=" + ipport);

        adbAuth = AdbAuth.createAdbAuth(context);

        if (adbAuth == null)
        {
            Log.e(LOGTAG, "connect: RSA crypto not available!");

            return false;
        }

        if (socket == null)
        {
            try
            {
                attempted = false;
                connected = false;

                Log.d(LOGTAG, "connect: open socket...");

                socket = new Socket(ipaddr, ipport);
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(3000);

                Log.d(LOGTAG, "connect: open socket done.");

                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                Log.d(LOGTAG, "connect: get streams done.");

                connThread = new Thread(connRun);
            }
            catch (Exception ex)
            {
                socket = null;

                inputStream = null;
                outputStream = null;

                connThread = null;

                ex.printStackTrace();

                return false;
            }
        }

        if (! connected)
        {
            Log.d(LOGTAG, "connect: attempting connect.");

            attempted = true;
            connThread.start();

            if (writePacket(AdbProtocol.buildConnect()))
            {
                Log.d(LOGTAG, "connect: wrote build connect.");

                synchronized (this)
                {
                    if (!connected)
                    {
                        AdbSimple.wait(this);
                    }

                    if (connected)
                    {
                        Log.d(LOGTAG, "connect: connection success.");
                    }
                    else
                    {
                        Log.e(LOGTAG, "connect: connection failed!");
                    }
                }
            }
        }

        return connected;
    }

    private void closeStreams()
    {
        synchronized (openStreams)
        {
            for (int inx = 0; inx < openStreams.size(); inx++)
            {
                try
                {
                    int streamId = openStreams.keyAt(inx);
                    AdbStream stream = openStreams.get(streamId);
                    stream.close();
                }
                catch (Exception ignore)
                {
                }
            }

            openStreams.clear();
        }
    }

    @Override
    public void close()
    {
        Log.d(LOGTAG, "close: closing connection.");

        closeStreams();

        if (connThread != null)
        {
            try
            {
                connThread.interrupt();
                connThread.join();
            }
            catch (Exception ignore)
            {
            }
        }

        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (Exception ignore)
            {
            }
        }

        if (outputStream != null)
        {
            try
            {
                outputStream.close();
            }
            catch (Exception ignore)
            {
            }
        }

        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (Exception ignore)
            {
            }
        }

        outputStream = null;
        inputStream = null;
        connThread = null;
        socket = null;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final Runnable connRun = new Runnable()
    {
        @Override
        public void run()
        {
            AdbConn conn = AdbConn.this;

            while (!connThread.isInterrupted())
            {
                try
                {
                    AdbMess msg = AdbMess.readAdbMessage(inputStream);

                    if (msg == null)
                    {
                        Log.e(LOGTAG, "connRun: connection lost!");

                        break;
                    }

                    if (!msg.validateMessage())
                    {
                        Log.e(LOGTAG, "connRun: message corrupted!");

                        break;
                    }

                    switch (msg.command)
                    {
                        case AdbProtocol.CMD_CLSE:
                        case AdbProtocol.CMD_OKAY:
                        case AdbProtocol.CMD_WRTE:

                            AdbStream stream;

                            synchronized (openStreams)
                            {
                                stream = openStreams.get(msg.arg1);
                            }

                            Log.d(LOGTAG, "connRun: recv"
                                    + " cmd=" + msg.cmdstr
                                    + " locId=" + msg.arg1
                                    + " remId=" + msg.arg0
                                    + " stream=" + (stream != null)
                            );

                            if (stream == null) continue;

                            synchronized (stream)
                            {
                                if (msg.command == AdbProtocol.CMD_CLSE)
                                {
                                    synchronized (openStreams)
                                    {
                                        conn.openStreams.remove(msg.arg1);
                                    }

                                    stream.notifyClose();
                                }

                                if (msg.command == AdbProtocol.CMD_OKAY)
                                {
                                    stream.updateRemoteId(msg.arg0);
                                    stream.readyForWrite();
                                    stream.notify();
                                }

                                if (msg.command == AdbProtocol.CMD_WRTE)
                                {
                                    stream.addPayload(msg.payload);
                                    stream.sendOkay();
                                }
                            }

                            break;

                        case AdbProtocol.CMD_AUTH:

                            Log.d(LOGTAG, "connRun: AUTH type=" + msg.arg0);

                            if (msg.arg0 == AdbProtocol.AUTH_TYPE_TOKEN)
                            {
                                Log.d(LOGTAG, "connRun: AUTH type=token");

                                byte[] packet;

                                if (conn.sentSignature)
                                {
                                    Log.d(LOGTAG, "connRun: send RSA-KEY.");

                                    packet = AdbProtocol.buildAuth(
                                            AdbProtocol.AUTH_TYPE_RSA_PUBLIC,
                                            conn.adbAuth.getAdbPublicKeyPayload());
                                }
                                else
                                {
                                    Log.d(LOGTAG, "connRun: send SIGNATURE.");

                                    packet = AdbProtocol.buildAuth(
                                            AdbProtocol.AUTH_TYPE_SIGNATURE,
                                            conn.adbAuth.signAdbTokenPayload(msg.payload));

                                    conn.sentSignature = true;
                                }

                                writePacket(packet);
                            }
                            else
                            {
                                Log.d(LOGTAG, "connRun: AUTH UNKNOWN.");
                            }

                            break;

                        case AdbProtocol.CMD_CNXN:

                            Log.d(LOGTAG, "connRun: CNXN maxData=" + msg.arg1);

                            synchronized (conn)
                            {
                                conn.maxData = msg.arg1;
                                conn.connected = true;
                                conn.notifyAll();
                            }

                            break;
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();

                    break;
                }
            }

            synchronized (conn)
            {
                conn.attempted = false;
                conn.connected = false;

                closeStreams();
                conn.notifyAll();
            }
        }
    };

    public int getMaxData()
    {
        return maxData;
    }

    public boolean writePacket(byte[] data)
    {
        try
        {
            outputStream.write(data);
            outputStream.flush();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    private boolean checkConnected()
    {
        if (attempted)
        {
            synchronized (this)
            {
                if (!connected)
                {
                    Log.d(LOGTAG, "checkConnected: Waiting for connect to become ready.");

                    AdbSimple.wait(this);
                }

                if (!connected)
                {
                    Log.e(LOGTAG, "checkConnected: Connection failed!");
                }
            }
        }

        return connected;
    }

    @Nullable
    public AdbStream openService(String service)
    {
        AdbStream stream = null;

        if (checkConnected())
        {
            int locId = ++lastlocId;

            stream = new AdbStream(this, locId);

            synchronized (openStreams)
            {
                openStreams.put(locId, stream);
            }

            if (writePacket(AdbProtocol.buildOpen(locId, service)))
            {
                synchronized (stream)
                {
                    AdbSimple.wait(stream);
                }

                if (stream.isClosed())
                {
                    Log.e(LOGTAG, "Stream rejected by remote host!");

                    synchronized (openStreams)
                    {
                        openStreams.remove(locId);
                    }

                    stream = null;
                }
            }
        }

        return stream;
    }
}
