package de.xavaro.android.awx.comm;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class AWXRequest
{
    public static final int MODE_WRITE_CHARACTERISTIC = 0;
    public static final int MODE_READ_CHARACTERISTIC = 1;
    public static final int MODE_ENABLE_NOTIFICATION = 2;
    public static final int MODE_DISABLE_NOTIFICATION = 3;

    public BluetoothGatt gatt;
    public BluetoothGattCharacteristic chara;
    public byte[] data;
    public int mode;

    public AWXRequest(BluetoothGatt gatt, BluetoothGattCharacteristic chara, byte[] data, int mode)
    {
        this.gatt = gatt;
        this.chara = chara;
        this.data = data;
        this.mode = mode;
    }
}
