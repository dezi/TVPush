package de.xavaro.android.yihome;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class P2PMessage
{
    public final short reqId;
    public final byte[] data;

    public P2PMessage(short reqId, byte[] data)
    {
        this.reqId = reqId;
        this.data = data;
    }
}
