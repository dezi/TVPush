package com.cgutman.adblib;

import android.support.annotation.Nullable;

import android.util.Log;
import android.util.SparseArray;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@SuppressWarnings({"WeakerAccess", "unused"})
public class AdbConnection implements Closeable
{
    private static final String LOGTAG = AdbConnection.class.getSimpleName();

    private Socket socket;

    private int lastLocalId;

    public OutputStream outputStream;
    private InputStream inputStream;

    private AdbCrypto crypto;
    private Thread connectionThread;

    private int maxData;

    private boolean attempted;
    private boolean connected;
    private boolean sentSignature;

    private SparseArray<AdbStream> openStreams;

    public AdbConnection(String ipaddr, int ipport, AdbCrypto crypto) throws IOException
    {
        this.crypto = crypto;

        socket = new Socket(ipaddr, ipport);
        socket.setTcpNoDelay(true);

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        openStreams = new SparseArray<>();

        connectionThread = new Thread(connectionRunner);

    }

    private final Runnable connectionRunner = new Runnable()
    {
        @Override
        public void run()
        {
            AdbConnection conn = AdbConnection.this;

            while (!connectionThread.isInterrupted())
            {
                try
                {
                    AdbProtocol.AdbMessage msg = AdbProtocol.AdbMessage.readAdbMessage(inputStream);

                    if (!AdbProtocol.validateMessage(msg))
                    {
                        Log.e(LOGTAG, "createConnectionThread: message corrupted!");

                        continue;
                    }

                    switch (msg.command)
                    {
                        case AdbProtocol.CMD_OKAY:
                        case AdbProtocol.CMD_WRTE:
                        case AdbProtocol.CMD_CLSE:

                            if (!conn.connected) continue;

                            AdbStream waitingStream = openStreams.get(msg.arg1);

                            if (waitingStream == null)
                            {
                                Log.e(LOGTAG, "createConnectionThread: no waiting stream!");

                                continue;
                            }

                            synchronized (waitingStream)
                            {
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

                                if (msg.command == AdbProtocol.CMD_CLSE)
                                {
                                    conn.openStreams.remove(msg.arg1);
                                    waitingStream.notifyClose();
                                }
                            }

                            break;

                        case AdbProtocol.CMD_AUTH:

                            byte[] packet;

                            if (msg.arg0 == AdbProtocol.AUTH_TYPE_TOKEN)
                            {
                                if (conn.sentSignature)
                                {
                                    packet = AdbProtocol.generateAuth(AdbProtocol.AUTH_TYPE_RSA_PUBLIC,
                                            conn.crypto.getAdbPublicKeyPayload());
                                }
                                else
                                {
                                    packet = AdbProtocol.generateAuth(AdbProtocol.AUTH_TYPE_SIGNATURE,
                                            conn.crypto.signAdbTokenPayload(msg.payload));

                                    conn.sentSignature = true;
                                }

                                conn.outputStream.write(packet);
                                conn.outputStream.flush();
                            }
                            break;

                        case AdbProtocol.CMD_CNXN:

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
                cleanupStreams();
                conn.notifyAll();
                conn.attempted = false;
            }
        }
    };

    public int getMaxData()
    {
        return maxData;
    }

    private boolean writeAndFlush(byte[] data)
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

    public boolean connect()
    {
        if (! connected)
        {
            if (writeAndFlush(AdbProtocol.generateConnect()))
            {
                attempted = true;
                connectionThread.start();

                synchronized (this)
                {
                    if (!connected) AdbSimple.wait(this);

                    if (!connected)
                    {
                        Log.e(LOGTAG, "Connection failed!");
                    }
                }
            }
        }

        return connected;
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
            int localId = ++lastLocalId;

            stream = new AdbStream(this, localId);
            openStreams.put(localId, stream);

            if (writeAndFlush(AdbProtocol.generateOpen(localId, service)))
            {
                synchronized (stream)
                {
                    AdbSimple.wait(stream);
                }

                if (stream.isClosed())
                {
                    Log.e(LOGTAG, "Stream rejected by remote host!");

                    openStreams.remove(localId);
                    stream = null;
                }
            }
        }

        return stream;
    }

    private void cleanupStreams()
    {
        for (int inx = 0; inx < openStreams.size(); inx++)
        {
            try
            {
                int streamId = openStreams.keyAt(inx);
                AdbStream stream = openStreams.get(streamId);
                stream.close();
            }
            catch (IOException ignore)
            {
            }
        }
		
        openStreams.clear();
    }

    @Override
    public void close()
    {
        if (connectionThread != null)
        {
            connectionThread.interrupt();
            connectionThread = null;
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
}
