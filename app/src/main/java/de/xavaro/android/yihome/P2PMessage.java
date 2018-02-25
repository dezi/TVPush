package de.xavaro.android.yihome;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class P2PMessage
{
    public final int reqId;
    public final byte[] data;

    public P2PMessage(int reqId, byte[] data)
    {
        this.reqId = reqId;
        this.data = data;
    }
}
