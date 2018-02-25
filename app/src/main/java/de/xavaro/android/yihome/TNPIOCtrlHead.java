package de.xavaro.android.yihome;

import android.text.TextUtils;

public class TNPIOCtrlHead
{
    public static final int LEN_HEAD = 40;
    public byte[] authInfo;
    public int authResult;
    public short commandNumber;
    public short commandType;
    public short dataSize;
    public short exHeaderSize;
    public boolean isByteOrderBig;

    public TNPIOCtrlHead(short s, short s2, short s3, String str, String str2, int i, boolean z)
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

    private TNPIOCtrlHead(boolean z)
    {
        this.authInfo = new byte[32];
        this.authResult = -1;
        this.isByteOrderBig = z;
    }

    public static TNPIOCtrlHead parse(byte[] bArr, boolean z)
    {
        TNPIOCtrlHead tNPIOCtrlHead = new TNPIOCtrlHead(z);
        tNPIOCtrlHead.commandType = Packet.byteArrayToShort(bArr, 0, z);
        tNPIOCtrlHead.commandNumber = Packet.byteArrayToShort(bArr, 2, z);
        tNPIOCtrlHead.exHeaderSize = Packet.byteArrayToShort(bArr, 4, z);
        tNPIOCtrlHead.dataSize = Packet.byteArrayToShort(bArr, 6, z);
        tNPIOCtrlHead.authResult = Packet.byteArrayToInt(bArr, 8, z);
        return tNPIOCtrlHead;
    }

    public byte[] toByteArray()
    {
        byte[] obj = new byte[40];

        System.arraycopy(Packet.shortToByteArray(this.commandType, this.isByteOrderBig), 0, obj, 0, 2);
        System.arraycopy(Packet.shortToByteArray(this.commandNumber, this.isByteOrderBig), 0, obj, 2, 2);
        System.arraycopy(Packet.shortToByteArray(this.exHeaderSize, this.isByteOrderBig), 0, obj, 4, 2);
        System.arraycopy(Packet.shortToByteArray(this.dataSize, this.isByteOrderBig), 0, obj, 6, 2);
        System.arraycopy(this.authInfo, 0, obj, 8, 32);

        return obj;
    }
}