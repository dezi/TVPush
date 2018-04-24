package de.xavaro.android.awx.comm;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class AWXRequest
{
    public static final int MODE_WRITE_CHARACTERISTIC = 0;
    public static final int MODE_READ_CHARACTERISTIC = 1;
    public static final int MODE_ENABLE_NOTIFICATION = 2;
    public static final int MODE_DISABLE_NOTIFICATION = 3;

    public BluetoothGattCharacteristic chara;
    public byte[] data;
    public int mode;

    public AWXRequest(BluetoothGattCharacteristic chara, int mode)
    {
        this(chara, mode, null);
    }

    public AWXRequest(BluetoothGattCharacteristic chara, int mode, byte[] data)
    {
        this.chara = chara;
        this.data = data;
        this.mode = mode;
    }
}
