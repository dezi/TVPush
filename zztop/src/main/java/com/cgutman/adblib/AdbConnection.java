package com.cgutman.adblib;


import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;

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

    private boolean connectAttempted;
    private boolean connected;
    private boolean sentSignature;

    private HashMap<Integer, AdbStream> openStreams;

    public AdbConnection()
    {
        openStreams = new HashMap<>();
        connectionThread = createConnectionThread();
    }

    public static AdbConnection create(Socket socket, AdbCrypto crypto) throws IOException
    {
        AdbConnection newConn = new AdbConnection();

        newConn.crypto = crypto;
        newConn.socket = socket;

        newConn.inputStream = socket.getInputStream();
        newConn.outputStream = socket.getOutputStream();

        return newConn;
    }

    private Thread createConnectionThread()
    {
        final AdbConnection conn = this;

        return new Thread(new Runnable()
        {
            @Override
            public void run()
            {
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

                            default:
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        break;
                    }
                }
				
				/* This thread takes care of cleaning up pending streams */
                synchronized (conn)
                {
                    cleanupStreams();
                    conn.notifyAll();
                    conn.connectAttempted = false;
                }
            }
        });
    }

    public int getMaxData()
    {
        return maxData;
    }

    public void connect() throws IOException, InterruptedException
    {
        if (connected)
            throw new IllegalStateException("Already connected");
		
		/* Write the CONNECT packet */
        outputStream.write(AdbProtocol.generateConnect());
        outputStream.flush();
		
		/* Start the connection thread to respond to the peer */
        connectAttempted = true;
        connectionThread.start();
		
		/* Wait for the connection to go live */
        synchronized (this)
        {
            if (!connected)
                wait();

            if (!connected)
            {
                throw new IOException("Connection failed");
            }
        }
    }

    public AdbStream open(String destination) throws IOException, InterruptedException
    {
        int localId = ++lastLocalId;

        if (!connectAttempted)
            throw new IllegalStateException("connect() must be called first");
		
        synchronized (this)
        {
            if (!connected) wait();

            if (!connected)
            {
                throw new IOException("Connection failed");
            }
        }
		
        AdbStream stream = new AdbStream(this, localId);
        openStreams.put(localId, stream);
		
        outputStream.write(AdbProtocol.generateOpen(localId, destination));
        outputStream.flush();
		
        synchronized (stream)
        {
            stream.wait();
        }
		
        if (stream.isClosed())
        {
            throw new ConnectException("Stream open actively rejected by remote peer");
        }

        return stream;
    }

    private void cleanupStreams()
    {
        for (AdbStream stream : openStreams.values())
        {
            try
            {
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
    }
}
