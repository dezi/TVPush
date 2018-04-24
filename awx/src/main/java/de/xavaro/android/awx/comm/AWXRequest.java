package de.xavaro.android.awx.comm;

import android.bluetooth.BluetoothGattCharacteristic;

public class AWXRequest
{
    public static final int MODE_WRITE_CHARACTERISTIC = 0;
    public static final int MODE_CRYPT_CHARACTERISTIC = 1;
    public static final int MODE_READ_CHARACTERISTIC = 2;
    public static final int MODE_ENABLE_NOTIFICATION = 3;
    public static final int MODE_DISABLE_NOTIFICATION = 4;
    public static final int MODE_DISCONNECT_GATT = 5;

    public static final int CHARA_PAIR = 1;
    public static final int CHARA_STAT = 2;
    public static final int CHARA_COMM = 3;

    public BluetoothGattCharacteristic chara;
    public byte[] data;
    public int cint;
    public int mode;

    public AWXRequest(BluetoothGattCharacteristic chara, int mode)
    {
        this.chara = chara;
        this.mode = mode;
    }

    public AWXRequest(int cint, int mode)
    {
        this.cint = cint;
        this.mode = mode;
    }

    public AWXRequest(int cint, int mode, byte[] data)
    {
        this.cint = cint;
        this.mode = mode;
        this.data = data;
    }
}
