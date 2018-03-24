package de.xavaro.android.adb;

import android.support.annotation.Nullable;

import android.util.SparseArray;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Closeable;
import java.net.Socket;

@SuppressWarnings({"WeakerAccess", "unused", "SynchronizationOnLocalVariableOrMethodParameter"})
public class AdbConnection implements Closeable
{
    private static final String LOGTAG = AdbConnection.class.getSimpleName();

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

    private AdbCrypto crypto;
    private Thread connThread;
    private SparseArray<AdbStream> openStreams;

    public AdbConnection(String ipaddr, int ipport)
    {
        this.ipaddr = ipaddr;
        this.ipport = ipport;
        this.crypto = AdbCrypto.setupCrypto("pub.key", "priv.key");
    }

    public boolean connect()
    {
        if (socket == null)
        {
            try
            {
                attempted = false;
                connected = false;

                Log.d(LOGTAG, "AdbConnection: open socket...");

                socket = new Socket(ipaddr, ipport);
                socket.setTcpNoDelay(true);

                Log.d(LOGTAG, "AdbConnection: open socket done.");

                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                Log.d(LOGTAG, "AdbConnection: get streams done.");

                openStreams = new SparseArray<>();
                connThread = new Thread(connRun);
            }
            catch (Exception ex)
            {
                socket = null;

                inputStream = null;
                outputStream = null;

                openStreams = null;
                connThread = null;

                ex.printStackTrace();

                return false;
            }
        }

        if (! connected)
        {
            attempted = true;
            connThread.start();

            if (writePacket(AdbProtocol.generateConnect()))
            {
                synchronized (this)
                {
                    if (!connected)
                    {
                        AdbSimple.wait(this);
                    }

                    if (!connected)
                    {
                        Log.e(LOGTAG, "Connection failed!");
                    }
                }
            }
        }

        return connected;
    }

    @Override
    public void close()
    {
        if (connThread != null)
        {
            connThread.interrupt();
            connThread = null;
        }

        try
        {
            socket.close();
        }
        catch (Exception ignore)
        {
        }

        Log.d(LOGTAG, "close: connection closed.");
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final Runnable connRun = new Runnable()
    {
        @Override
        public void run()
        {
            AdbConnection conn = AdbConnection.this;

            AdbStream waitingStream;

            while (!connThread.isInterrupted())
            {
                try
                {
                    AdbMessage msg = AdbMessage.readAdbMessage(inputStream);

                    if (msg == null)
                    {
                        Log.e(LOGTAG, "connRun: Connection lost!");

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

                            waitingStream = openStreams.get(msg.arg1);

                            Log.d(LOGTAG, "connRun: recv"
                                    + " cmd=" + msg.cmdstr
                                    + " locId=" + msg.arg1
                                    + " remId=" + msg.arg0
                                    + " stream=" + (waitingStream != null)
                            );

                            if (waitingStream == null) continue;

                            synchronized (waitingStream)
                            {
                                if (msg.command == AdbProtocol.CMD_CLSE)
                                {
                                    conn.openStreams.remove(msg.arg1);
                                    waitingStream.notifyClose();
                                }

                                if (msg.command == AdbProtocol.CMD_OKAY)
                                {
                                    waitingStream.updateRemoteId(msg.arg0);
                                    waitingStream.readyForWrite();
                                    waitingStream.notify();
                                }

                                if (msg.command == AdbProtocol.CMD_WRTE)
                                {
                                    waitingStream.addPayload(msg.payload);
                                    waitingStream.sendReady();
                                }
                            }

                            break;

                        case AdbProtocol.CMD_AUTH:

                            Log.d(LOGTAG, "connRun: AUTH type=" + msg.arg0);

                            if (msg.arg0 == AdbProtocol.AUTH_TYPE_TOKEN)
                            {
                                Log.d(LOGTAG, "connRun: AUTH TOKEN.");

                                byte[] packet;

                                if (conn.sentSignature)
                                {
                                    Log.d(LOGTAG, "connRun: send RSA-KEY.");

                                    packet = AdbProtocol.generateAuth(AdbProtocol.AUTH_TYPE_RSA_PUBLIC,
                                            conn.crypto.getAdbPublicKeyPayload());
                                }
                                else
                                {
                                    Log.d(LOGTAG, "connRun: send SIGNATURE.");

                                    packet = AdbProtocol.generateAuth(AdbProtocol.AUTH_TYPE_SIGNATURE,
                                            conn.crypto.signAdbTokenPayload(msg.payload));

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
            openStreams.put(locId, stream);

            if (writePacket(AdbProtocol.generateOpen(locId, service)))
            {
                synchronized (stream)
                {
                    AdbSimple.wait(stream);
                }

                if (stream.isClosed())
                {
                    Log.e(LOGTAG, "Stream rejected by remote host!");

                    openStreams.remove(locId);

                    stream = null;
                }
            }
        }

        return stream;
    }

    private void closeStreams()
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
