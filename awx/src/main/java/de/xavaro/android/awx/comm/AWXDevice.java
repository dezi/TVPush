package de.xavaro.android.awx.comm;

import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGatt;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.xavaro.android.awx.simple.Simple;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AWXDevice extends BluetoothGattCallback
{
    private final String LOGTAG = AWXDevice.class.getSimpleName();

    private short meshid;
    private String meshname = "pDmpKfcw";
    private String meshpass = "f6d292f5";

    private String macaddr;

    private byte[] sessionKey;
    private byte[] sessionRand;

    private BluetoothGattCharacteristic cpair;
    private BluetoothGattCharacteristic cstatus;
    private BluetoothGattCharacteristic ccommand;

    private final ArrayList<AWXRequest> executeme = new ArrayList<>();

    public AWXDevice(short meshid, String meshname, String macaddr)
    {
        this.meshid = meshid;
        this.meshname = meshname;
        this.macaddr = macaddr;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
        if (newState == BluetoothProfile.STATE_CONNECTED)
        {
            Log.d(LOGTAG, "onConnectionStateChange: connected.");

            gatt.discoverServices();

            return;
        }

        if (newState == BluetoothProfile.STATE_DISCONNECTED)
        {
            Log.d(LOGTAG, "onConnectionStateChange: disconnected.");

            return;
        }

        Log.d(LOGTAG, "onConnectionStateChange:"
                + " status=" + status
                + " newState=" + newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        Log.d(LOGTAG, "onServicesDiscovered: status=" + status);
        if (status != 0) return;

        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services)
        {
            Log.d(LOGTAG, "onServicesDiscovered: service=" + service.getUuid());

            List<BluetoothGattCharacteristic> charas = service.getCharacteristics();

            for (BluetoothGattCharacteristic chara : charas)
            {
                Log.d(LOGTAG, "onServicesDiscovered: characteristic=" + chara.getUuid());

                if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_PAIR))
                {
                    cpair = chara;
                    continue;
                }

                if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_STATUS))
                {
                    cstatus = chara;
                    continue;
                }

                if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_COMMAND))
                {
                    ccommand = chara;
                    continue;
                }

                if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_OTA))
                {
                    continue;
                }

                executeme.add(new AWXRequest(gatt, chara, null, AWXRequest.MODE_READ_CHARACTERISTIC));
            }
        }

        Log.d(LOGTAG, "onServicesDiscovered: done...");

        //
        // Pairing.
        //

        sessionRand = new byte[8];
        new SecureRandom().nextBytes(sessionRand);

        byte[] meshname16 = Arrays.copyOf(meshname.getBytes(), 16);
        byte[] meshpass16 = Arrays.copyOf(meshpass.getBytes(), 16);

        byte[] data = AWXProtocol.getPairValue(meshname16, meshpass16, sessionRand);

        executeme.add(new AWXRequest(gatt, cpair, data, AWXRequest.MODE_WRITE_CHARACTERISTIC));
        executeme.add(new AWXRequest(gatt, cpair, null, AWXRequest.MODE_READ_CHARACTERISTIC));

        //
        // Status.
        //

        executeme.add(new AWXRequest(gatt, cstatus, new byte[]{1}, AWXRequest.MODE_WRITE_CHARACTERISTIC));
        executeme.add(new AWXRequest(gatt, cstatus, null, AWXRequest.MODE_ENABLE_NOTIFICATION));

        executeNext(gatt);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic chara,
                                     int status)
    {
        if (status != BluetoothGatt.GATT_SUCCESS)
        {
            Log.d(LOGTAG, " onCharacteristicRead status=" + status + " uuid=" + chara.getUuid());

            return;
        }

        byte[] data = chara.getValue();

        String tag = null;
        String val = null;

        switch(chara.getUuid().toString())
        {
            case AWXDefs.chara_device_name:
                tag = "name";
                val = chara.getStringValue(0).trim();

                String friendly = AWXDevices.getFriendlyName(val);
                Log.d(LOGTAG, " onCharacteristicRead tag=" + tag + " val=" + val + " friendly=" + friendly);

                break;

            case AWXDefs.chara_appearance:
                tag = "type";
                val = Simple.getBytesToHexString(data);
                break;

            case AWXDefs.chara_model_number_string:
                tag = "modnum";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.chara_serial_number_string:
                tag = "serial";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.chara_firmware_revision_string:
                tag = "firmware";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.chara_hardware_revision_string:
                tag = "hardware";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.chara_manufacturer_name_string:
                tag = "vendor";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.CHARACTERISTIC_MESH_LIGHT_STATUS:
                tag = "light_status";
                val = Simple.getBytesToHexString(data);
                break;

            case AWXDefs.CHARACTERISTIC_MESH_LIGHT_COMMAND:
                tag = "light_command";
                val = Simple.getBytesToHexString(data);
                break;

            case AWXDefs.CHARACTERISTIC_MESH_LIGHT_OTA:
                tag = "light_ota";
                val = Simple.getBytesToHexString(data);
                break;

            case AWXDefs.CHARACTERISTIC_MESH_LIGHT_PAIR:
                tag = "light_pair";
                val = Simple.getBytesToHexString(data);

                if (data[0] == 13)
                {
                    byte[] sessionRandom = new byte[8];
                    System.arraycopy(data, 1, sessionRandom, 0, sessionRandom.length);

                    byte[] meshname16 = Arrays.copyOf(meshname.getBytes(), 16);
                    byte[] meshpass16 = Arrays.copyOf(meshpass.getBytes(), 16);

                    sessionKey = AWXProtocol.getSessionKey(meshname16, meshpass16, this.sessionRand, sessionRandom);

                    Log.d(LOGTAG, "onCharacteristicRead: paired!!!!!! sessionKey=" + Simple.getBytesToHexString(sessionKey));

                    setColor(gatt, ccommand, 0x008800);
                }
                else
                {
                    if (data[0] == 14)
                    {
                        Log.e(LOGTAG, "onCharacteristicRead: OPCODE_ENC_FAIL !");
                    }
                    else
                    {
                        if (data[0] == 7)
                        {
                            Log.e(LOGTAG, "onCharacteristicRead: IRGENDWAS !");
                        }
                    }
                }

                break;
        }

        Log.d(LOGTAG, "onCharacteristicRead: tag=" + tag + " val=" + val);

        executeNext(gatt);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status)
    {
        Log.d(LOGTAG, "onCharacteristicWrite: status=" + status + " chara=" + chara.getUuid());

        executeNext(gatt);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic chara)
    {
        byte[] data = chara.getValue();
        byte[] plain = AWXProtocol.decryptValue(macaddr, sessionKey, data);
        byte[] payload = AWXProtocol.getData(plain);

        byte command = AWXProtocol.getCommand(plain);

        short meshIdDev = Short.parseShort(Integer.toString((payload[9] & 255) + 256, 16).substring(1) + Integer.toString((payload[0] & 255) + 256, 16).substring(1), 16);

        Log.d(LOGTAG, "onCharacteristicChanged:"
                + " m1=" + meshid
                + " m2=" + meshIdDev
                + " " + ((byte) ((meshIdDev >> 8) & 0xff))
                + " " + ((byte) ((meshIdDev ) & 0xff))
                + " cmd=" + command
                + " paylod=" + Simple.getBytesToHexString(payload));

        executeNext(gatt);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
        Log.d(LOGTAG, "onDescriptorRead: descriptor=" + descriptor.getUuid());

        executeNext(gatt);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
        Log.d(LOGTAG, "onDescriptorWrite: descriptor=" + descriptor.getUuid());

        executeNext(gatt);
    }

    private void executeNext(BluetoothGatt gatt)
    {
        if (executeme.size() == 0) return;

        AWXRequest request = executeme.remove(0);

        if (request.mode == AWXRequest.MODE_READ_CHARACTERISTIC)
        {
            gatt.readCharacteristic(request.chara);
        }

        if (request.mode == AWXRequest.MODE_WRITE_CHARACTERISTIC)
        {
            Log.d(LOGTAG, "executeNext: write: data=" + Simple.getBytesToHexString(request.data));

            request.chara.setValue(request.data);
            request.chara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            gatt.writeCharacteristic(request.chara);
        }

        if (request.mode == AWXRequest.MODE_ENABLE_NOTIFICATION)
        {
            gatt.setCharacteristicNotification(request.chara,true);

            executeNext(gatt);
        }

        if (request.mode == AWXRequest.MODE_DISABLE_NOTIFICATION)
        {
            gatt.setCharacteristicNotification(request.chara,false);

            executeNext(gatt);
        }
    }

    private void setColor(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int color)
    {
        byte[] data = AWXProtocol.getValue(meshid, (byte) -30, new byte[]{(byte) 4, (byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color)});

        Log.d(LOGTAG, "setColor: plain=" + Simple.getBytesToHexString(data));

        byte[] cryp = AWXProtocol.encryptValue(macaddr, sessionKey, data);

        Log.d(LOGTAG, "setColor: crypt=" + Simple.getBytesToHexString(cryp));

        executeme.add(new AWXRequest(gatt, chara, cryp, AWXRequest.MODE_WRITE_CHARACTERISTIC));
    }
}
