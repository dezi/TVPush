package zz.top.p2p.camera;

public class P2PFrame
{
    public static final int FRAME_SIZE = 40;
    public static final int AUTH_SIZE = 32;

    public short commandNumber;
    public short commandType;
    public short exHeaderSize;
    public short dataSize;

    public boolean isByteOrderBig;

    public int authResult;
    public byte[] authInfo  = new byte[32];

    public P2PFrame(short commandType, short commandNumber, short dataSize, String auth1, String auth2, boolean isByteOrderBig)
    {
        this.commandType = commandType;
        this.commandNumber = commandNumber;
        this.dataSize = dataSize;
        this.isByteOrderBig = isByteOrderBig;

        if ((auth1 != null) && (! auth1.isEmpty()) && (auth2 != null) && (! auth2.isEmpty()))
        {
            String auth = auth1 + "," + auth2;
            System.arraycopy(auth.getBytes(), 0, this.authInfo, 0, auth.length());
        }
    }

    private P2PFrame(boolean isBigEndian)
    {
        this.isByteOrderBig = isBigEndian;
    }

    public static P2PFrame parse(byte[] data, int offset, boolean isBigEndian)
    {
        P2PFrame frame = new P2PFrame(isBigEndian);

        frame.commandType = P2PPacker.byteArrayToShort(data, offset, isBigEndian);
        frame.commandNumber = P2PPacker.byteArrayToShort(data, offset + 2, isBigEndian);
        frame.exHeaderSize = P2PPacker.byteArrayToShort(data, offset + 4, isBigEndian);
        frame.dataSize = P2PPacker.byteArrayToShort(data, offset + 6, isBigEndian);
        frame.authResult = P2PPacker.byteArrayToInt(data, offset + 8, isBigEndian);

        return frame;
    }

    public byte[] build()
    {
        byte[] data = new byte[FRAME_SIZE];

        System.arraycopy(P2PPacker.shortToByteArray(this.commandType, this.isByteOrderBig), 0, data, 0, 2);
        System.arraycopy(P2PPacker.shortToByteArray(this.commandNumber, this.isByteOrderBig), 0, data, 2, 2);
        System.arraycopy(P2PPacker.shortToByteArray(this.exHeaderSize, this.isByteOrderBig), 0, data, 4, 2);
        System.arraycopy(P2PPacker.shortToByteArray(this.dataSize, this.isByteOrderBig), 0, data, 6, 2);

        System.arraycopy(this.authInfo, 0, data, 8, AUTH_SIZE);

        return data;
    }
}