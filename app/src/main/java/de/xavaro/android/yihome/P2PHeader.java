package de.xavaro.android.yihome;

public class P2PHeader
{
    public static final int HEADER_SIZE = 8;

    public static final byte IO_TYPE_UNKNOWN = (byte) 0;
    public static final byte IO_TYPE_VIDEO = (byte) 1;
    public static final byte IO_TYPE_AUDIO = (byte) 2;
    public static final byte IO_TYPE_COMMAND = (byte) 3;

    public static final byte VERSION_ONE = (byte) 1;
    public static final byte VERSION_TWO = (byte) 2;

    public int dataSize;
    public byte ioType;
    public boolean isBigEndian;
    public byte[] reserved;
    public byte version;

    public P2PHeader(byte version, byte ioType, int dataSize, boolean isBigEndian)
    {
        this.version = version;
        this.ioType = ioType;
        this.reserved = new byte[2];
        this.dataSize = dataSize;
        this.isBigEndian = isBigEndian;
    }

    public P2PHeader(byte ioType, int dataSize, boolean isBigEndian)
    {
        this.version = VERSION_ONE;
        this.ioType = ioType;
        this.reserved = new byte[2];
        this.dataSize = dataSize;
        this.isBigEndian = isBigEndian;
    }

    private P2PHeader(boolean isBigEndian)
    {
        this.isBigEndian = isBigEndian;
    }

    public static P2PHeader parse(byte[] data, boolean isBigEndian)
    {
        P2PHeader tNPHead = new P2PHeader(isBigEndian);

        tNPHead.version = data[0];
        tNPHead.ioType = data[1];
        tNPHead.dataSize = Packet.byteArrayToInt(data, 4, isBigEndian);

        return tNPHead;
    }

    public byte[] build()
    {
        byte[] obj = new byte[8];

        obj[0] = this.version;
        obj[1] = this.ioType;

        System.arraycopy(this.reserved, 0, obj, 2, 2);
        System.arraycopy(Packet.intToByteArray(this.dataSize, this.isBigEndian), 0, obj, 4, 4);

        return obj;
    }
}
