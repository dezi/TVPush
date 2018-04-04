package de.xavaro.android.adb.conn;

import android.support.annotation.Nullable;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.Closeable;
import java.util.Queue;

@SuppressWarnings("WeakerAccess")
public class AdbStream implements Closeable
{
    private static final String LOGTAG = AdbStream.class.getSimpleName();

    private int localId;
    private int remoteId;

    private final AdbConn adbConn;
    private final AtomicBoolean writeReady;
    private final Queue<byte[]> readQueue;

    private boolean isClosed;

    public AdbStream(AdbConn adbConn, int localId)
    {
        this.isClosed = false;
        this.adbConn = adbConn;
        this.localId = localId;
        this.readQueue = new ConcurrentLinkedQueue<>();
        this.writeReady = new AtomicBoolean(false);
    }

    void addPayload(byte[] payload)
    {
        synchronized (readQueue)
        {
            readQueue.add(payload);
            readQueue.notifyAll();
        }
    }

    void sendOkay()
    {
        byte[] packet = AdbProtocol.buildOkay(localId, remoteId);

        adbConn.writePacket(packet);
    }

    void updateRemoteId(int remoteId)
    {
        this.remoteId = remoteId;
    }

    void readyForWrite()
    {
        writeReady.set(true);
    }

    void notifyClose()
    {
        isClosed = true;
		
        synchronized (this)
        {
            notifyAll();
        }

        synchronized (readQueue)
        {
            readQueue.notifyAll();
        }
    }

    @Nullable
    public byte[] read()
    {
        byte[] data;

        synchronized (readQueue)
        {
            while (((data = readQueue.poll()) == null) && ! isClosed())
            {
                AdbSimple.wait(readQueue);
            }
        }

        return data;
    }

    public void write(String payload)
    {
        write(payload.getBytes());
    }

    public void write(byte[] payload)
    {
        synchronized (this)
        {
            while ((!isClosed) && (! writeReady.compareAndSet(true, false)))
            {
                AdbSimple.wait(this);
            }

            if (isClosed)
            {
                Log.e(LOGTAG, "read: Stream was closed!");

                return;
            }
        }
		
        byte[] packet = AdbProtocol.buildWrite(localId, remoteId, payload);

        adbConn.writePacket(packet);

        Log.d(LOGTAG, "write: locId=" + localId + " remId=" + remoteId + " size=" + payload.length);
    }

    @Override
    public void close()
    {
        synchronized (this)
        {
            if (isClosed) return;
            notifyClose();
        }

        byte[] packet = AdbProtocol.buildClose(localId, remoteId);

        adbConn.writePacket(packet);
    }

    public boolean isClosed()
    {
        return isClosed && (readQueue.size() == 0);
    }
}
