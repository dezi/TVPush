package de.xavaro.android.p2pcamera;

import android.text.TextUtils;

public class P2PFrame
{
    public static final int FRAME_SIZE = 40;

    public byte[] authInfo;
    public int authResult;
    public short commandNumber;
    public short commandType;
    public short dataSize;
    public short exHeaderSize;
    public boolean isByteOrderBig;

    public P2PFrame(short s, short s2, short s3, String str, String str2, int i, boolean z)
    {
        this.authInfo = new byte[32];
        this.authResult = -1;
        this.commandType = s;
        this.commandNumber = s2;
        this.exHeaderSize = (short) 0;
        this.dataSize = s3;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2))
        {
            this.authResult = i;
            this.isByteOrderBig = z;
            return;
        }
        this.authResult = -1;
        String str3 = str + "," + str2;
        System.arraycopy(str3.getBytes(), 0, this.authInfo, 0, str3.length());
        this.isByteOrderBig = z;
    }

    private P2PFrame(boolean z)
    {
        this.authInfo = new byte[32];
        this.authResult = -1;
        this.isByteOrderBig = z;
    }

    public static P2PFrame parse(byte[] data, boolean isBigEndian)
    {
        P2PFrame tNPIOCtrlHead = new P2PFrame(isBigEndian);

        tNPIOCtrlHead.commandType = P2PPacker.byteArrayToShort(data, 0, isBigEndian);
        tNPIOCtrlHead.commandNumber = P2PPacker.byteArrayToShort(data, 2, isBigEndian);
        tNPIOCtrlHead.exHeaderSize = P2PPacker.byteArrayToShort(data, 4, isBigEndian);
        tNPIOCtrlHead.dataSize = P2PPacker.byteArrayToShort(data, 6, isBigEndian);
        tNPIOCtrlHead.authResult = P2PPacker.byteArrayToInt(data, 8, isBigEndian);

        return tNPIOCtrlHead;
    }

    public byte[] build()
    {
        byte[] obj = new byte[40];

        System.arraycopy(P2PPacker.shortToByteArray(this.commandType, this.isByteOrderBig), 0, obj, 0, 2);
        System.arraycopy(P2PPacker.shortToByteArray(this.commandNumber, this.isByteOrderBig), 0, obj, 2, 2);
        System.arraycopy(P2PPacker.shortToByteArray(this.exHeaderSize, this.isByteOrderBig), 0, obj, 4, 2);
        System.arraycopy(P2PPacker.shortToByteArray(this.dataSize, this.isByteOrderBig), 0, obj, 6, 2);
        System.arraycopy(this.authInfo, 0, obj, 8, 32);

        return obj;
    }
}