package de.xavaro.android.yihome;

public class TNPHead
{
    public static final int LEN_HEAD = 8;

    public static final byte IO_TYPE_AUDIO = (byte) 2;
    public static final byte IO_TYPE_COMMAND = (byte) 3;
    public static final byte IO_TYPE_UNKNOWN = (byte) 0;
    public static final byte IO_TYPE_VIDEO = (byte) 1;
    public static final byte VERSION_ONE = (byte) 1;
    public static final byte VERSION_TWO = (byte) 2;

    public int dataSize;
    public byte ioType;
    public boolean isByteOrderBig;
    public byte[] reserved;
    public byte version;

    public TNPHead(byte b, byte b2, int i, boolean z)
    {
        this.version = b;
        this.ioType = b2;
        this.reserved = new byte[2];
        this.dataSize = i;
        this.isByteOrderBig = z;
    }

    public TNPHead(byte b, int i, boolean z)
    {
        this.version = (byte) 1;
        this.ioType = b;
        this.reserved = new byte[2];
        this.dataSize = i;
        this.isByteOrderBig = z;
    }

    private TNPHead(boolean z)
    {
        this.isByteOrderBig = z;
    }

    public static TNPHead parse(byte[] bArr, boolean z)
    {
        TNPHead tNPHead = new TNPHead(z);
        tNPHead.version = bArr[0];
        tNPHead.ioType = bArr[1];
        tNPHead.dataSize = Packet.byteArrayToInt(bArr, 4, z);

        return tNPHead;
    }

    public byte[] toByteArray()
    {
        byte[] obj = new byte[8];

        obj[0] = this.version;
        obj[1] = this.ioType;
        System.arraycopy(this.reserved, 0, obj, 2, 2);
        System.arraycopy(Packet.intToByteArray(this.dataSize, this.isByteOrderBig), 0, obj, 4, 4);

        return obj;
    }
}
