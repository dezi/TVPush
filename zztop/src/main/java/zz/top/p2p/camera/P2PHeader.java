package zz.top.p2p.camera;

public class P2PHeader
{
    public static final int HEADER_SIZE = 8;

    public static final byte IO_TYPE_UNKNOWN = 0;
    public static final byte IO_TYPE_VIDEO = 1;
    public static final byte IO_TYPE_AUDIO = 2;
    public static final byte IO_TYPE_COMMAND = 3;

    public static final byte VERSION_ONE = 1;
    public static final byte VERSION_TWO = 2;

    public byte version;
    public byte ioType;
    public byte reserved1;
    public byte reserved2;
    public int dataSize;
    public boolean isBigEndian;

    public P2PHeader(byte version, byte ioType, int dataSize, boolean isBigEndian)
    {
        this.version = version;
        this.ioType = ioType;
        this.dataSize = dataSize;
        this.isBigEndian = isBigEndian;
    }

    public P2PHeader(byte ioType, int dataSize, boolean isBigEndian)
    {
        this.version = VERSION_ONE;
        this.ioType = ioType;
        this.dataSize = dataSize;
        this.isBigEndian = isBigEndian;
    }

    private P2PHeader(boolean isBigEndian)
    {
        this.isBigEndian = isBigEndian;
    }

    public static P2PHeader parse(byte[] data, int offset, boolean isBigEndian)
    {
        P2PHeader header = new P2PHeader(isBigEndian);

        header.version = data[offset];
        header.ioType = data[offset + 1];
        header.reserved1 = data[offset + 2];
        header.reserved2 = data[offset + 3];

        header.dataSize = P2PPacker.byteArrayToInt(data, offset +4, isBigEndian);

        return header;
    }

    public byte[] build()
    {
        byte[] data = new byte[8];

        data[0] = version;
        data[1] = ioType;
        data[2] = reserved1;
        data[3] = reserved2;

        System.arraycopy(P2PPacker.intToByteArray(this.dataSize, this.isBigEndian), 0, data, 4, 4);

        return data;
    }
}
