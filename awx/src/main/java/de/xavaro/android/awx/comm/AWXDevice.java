package de.xavaro.android.awx.comm;

import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.xavaro.android.awx.simple.Json;
import de.xavaro.android.awx.simple.Simple;
import de.xavaro.android.awx.utils.AWXHardwareUtils;

@SuppressWarnings("WeakerAccess")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AWXDevice extends BluetoothGattCallback
{
    private final String LOGTAG = AWXDevice.class.getSimpleName();

    public static final SparseArray<AWXDevice> meshid2device = new SparseArray<>();

    private final boolean debug = false;

    private final Context context;
    private final short meshid;
    private final String meshname;
    private final String macaddr;

    private String uuid;
    private String name;
    private String model;
    private String vendor;
    private String version;

    private String meshpass = "f6d292f5";

    private byte[] sessionKey;
    private byte[] sessionRand;

    private BluetoothGatt gatt;

    private BluetoothGattCharacteristic cpair;
    private BluetoothGattCharacteristic cstat;
    private BluetoothGattCharacteristic ccomm;

    private final ArrayList<AWXRequest> executeme = new ArrayList<>();

    public AWXDevice(Context context, short meshid, String meshname, String macaddr)
    {
        this.context = context;
        this.meshid = meshid;
        this.meshname = meshname;
        this.macaddr = macaddr;

        if (meshname.equals("unpaired"))
        {
            meshpass = "1234";
        }

        meshid2device.put(meshid, this);
    }

    public void connect()
    {
        BluetoothAdapter adapter = Simple.getBTAdapter();
        if (adapter == null) return;

        BluetoothDevice device = adapter.getRemoteDevice(macaddr);
        device.connectGatt(context, false, this);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
        if (newState == BluetoothProfile.STATE_CONNECTED)
        {
            Log.d(LOGTAG, "onConnectionStateChange: connected mac=" + macaddr);

            this.gatt = gatt;

            gatt.discoverServices();
        }

        if (newState == BluetoothProfile.STATE_DISCONNECTED)
        {
            Log.d(LOGTAG, "onConnectionStateChange: disconnected mac=" + macaddr);

            executeme.clear();

            this.cpair = null;
            this.cstat = null;
            this.ccomm = null;

            this.gatt = null;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        Log.d(LOGTAG, "onServicesDiscovered: mac=" + macaddr + " status=" + status);
        if (status != 0) return;

        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services)
        {
            List<BluetoothGattCharacteristic> charas = service.getCharacteristics();

            for (BluetoothGattCharacteristic chara : charas)
            {
                if (chara.getUuid().toString().equalsIgnoreCase(AWXDefs.CHARACTERISTIC_MESH_LIGHT_PAIR))
                {
                    cpair = chara;
                    continue;
                }

                if (chara.getUuid().toString().equalsIgnoreCase(AWXDefs.CHARACTERISTIC_MESH_LIGHT_STATUS))
                {
                    cstat = chara;
                    continue;
                }

                if (chara.getUuid().toString().equalsIgnoreCase(AWXDefs.CHARACTERISTIC_MESH_LIGHT_COMMAND))
                {
                    ccomm = chara;
                    continue;
                }

                if (chara.getUuid().toString().equalsIgnoreCase(AWXDefs.CHARACTERISTIC_MESH_LIGHT_OTA))
                {
                    continue;
                }

                executeme.add(new AWXRequest(chara, AWXRequest.MODE_READ_CHARACTERISTIC));
            }
        }

        //
        // Pairing.
        //

        sessionRand = new byte[8];
        new SecureRandom().nextBytes(sessionRand);

        byte[] meshname16 = Arrays.copyOf(meshname.getBytes(), 16);
        byte[] meshpass16 = Arrays.copyOf(meshpass.getBytes(), 16);

        byte[] data = AWXProtocol.getPairValue(meshname16, meshpass16, sessionRand);

        executeme.add(new AWXRequest(cpair, AWXRequest.MODE_WRITE_CHARACTERISTIC, data));
        executeme.add(new AWXRequest(cpair, AWXRequest.MODE_READ_CHARACTERISTIC));

        //
        // Status.
        //

        executeme.add(new AWXRequest(cstat, AWXRequest.MODE_WRITE_CHARACTERISTIC, new byte[]{1}));
        executeme.add(new AWXRequest(cstat, AWXRequest.MODE_ENABLE_NOTIFICATION));

        executeNext();
    }

    @Override
    @SuppressWarnings("UnusedAssignment")
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic chara,
                                     int status)
    {
        if (status != BluetoothGatt.GATT_SUCCESS)
        {
            Log.d(LOGTAG, "onCharacteristicRead: mac=" + macaddr + " status=" + status + " uuid=" + chara.getUuid());

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
                val = AWXDevices.getFriendlyName(val);

                name = val;

                break;

            case AWXDefs.chara_appearance:
                tag = "type";
                val = Simple.getBytesToHexString(data);
                break;

            case AWXDefs.chara_model_number_string:
                tag = "model";
                val = chara.getStringValue(0).trim();

                model = val;

                break;

            case AWXDefs.chara_serial_number_string:
                tag = "serial";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.chara_firmware_revision_string:
                tag = "firmware";
                val = chara.getStringValue(0).trim();

                version = val;

                break;

            case AWXDefs.chara_hardware_revision_string:
                tag = "hardware";
                val = chara.getStringValue(0).trim();
                break;

            case AWXDefs.chara_manufacturer_name_string:
                tag = "vendor";
                val = chara.getStringValue(0).trim();

                vendor = val;

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

                if (data[0] == AWXProtocol.OPCODE_ENC_RSP)
                {
                    byte[] responseRand = new byte[8];
                    System.arraycopy(data, 1, responseRand, 0, responseRand.length);

                    byte[] meshname16 = Arrays.copyOf(meshname.getBytes(), 16);
                    byte[] meshpass16 = Arrays.copyOf(meshpass.getBytes(), 16);

                    sessionKey = AWXProtocol.getSessionKey(meshname16, meshpass16, sessionRand, responseRand);

                    Log.d(LOGTAG, "onCharacteristicRead: paired mac=" + macaddr + " sessionKey=" + Simple.getBytesToHexString(sessionKey));

                    buildDeviceDescription();

                    break;
                }

                if (data[0] == AWXProtocol.OPCODE_ENC_FAIL)
                {
                    Log.e(LOGTAG, "onCharacteristicRead: pairing failed! mac=" + macaddr);

                    break;
                }

                Log.e(LOGTAG, "onCharacteristicRead: pairing unknown mac=" + macaddr + " opcode=" + data[0]);

                break;
        }

        //noinspection ConstantConditions
        if (debug) Log.d(LOGTAG, "onCharacteristicRead:  mac=" + macaddr + " tag=" + tag + " val=" + val);

        executeNext();
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status)
    {
        Log.d(LOGTAG, "onCharacteristicWrite: mac=" + macaddr + " status=" + status + " chara=" + chara.getUuid());

        executeNext();
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic chara)
    {
        byte[] data = chara.getValue();
        byte[] plain = AWXProtocol.decryptValue(macaddr, sessionKey, data);
        byte[] payload = AWXProtocol.getData(plain);

        short targetMeshid = (short) (((payload[9] & 0xff) << 8) + (payload[0] & 0xff));
        AWXDevice targetDevice = meshid2device.get(targetMeshid, null);

        if (targetDevice != null)
        {
            targetDevice.evalNotification(plain);
        }
        else
        {
            Log.e(LOGTAG, "onCharacteristicChanged: offline " + " mac=" + macaddr + " targetMeshid=" + targetMeshid);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
        Log.d(LOGTAG, "onDescriptorRead: mac=" + macaddr + " descriptor=" + descriptor.getUuid());

        executeNext();
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
        Log.d(LOGTAG, "onDescriptorWrite: mac=" + macaddr + " descriptor=" + descriptor.getUuid());

        executeNext();
    }

    private void executeNext()
    {
        if (executeme.size() == 0) return;

        try
        {
            Thread.sleep(50);
        }
        catch (Exception ignore)
        {
        }

        AWXRequest request = executeme.remove(0);

        if (request.mode == AWXRequest.MODE_READ_CHARACTERISTIC)
        {
            gatt.readCharacteristic(request.chara);
        }

        if (request.mode == AWXRequest.MODE_WRITE_CHARACTERISTIC)
        {
            Log.d(LOGTAG, "executeNext: write: mac=" + macaddr + " data=" + Simple.getBytesToHexString(request.data));

            request.chara.setValue(request.data);
            request.chara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            gatt.writeCharacteristic(request.chara);
        }

        if (request.mode == AWXRequest.MODE_ENABLE_NOTIFICATION)
        {
            gatt.setCharacteristicNotification(request.chara,true);

            executeNext();
        }

        if (request.mode == AWXRequest.MODE_DISABLE_NOTIFICATION)
        {
            gatt.setCharacteristicNotification(request.chara,false);

            executeNext();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setColor(int color)
    {
        byte[] plain = AWXProtocol.getValue(
                meshid,
                AWXProtocol.COMMAND_SET_COLOR,
                new byte[]{4, (byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color)}
        );

        byte[] crypt = AWXProtocol.encryptValue(macaddr, sessionKey, plain);

        executeme.add(new AWXRequest(ccomm, AWXRequest.MODE_WRITE_CHARACTERISTIC, crypt));
    }

    private void buildDeviceDescription()
    {
        uuid = Simple.hmacSha1UUID(model, macaddr);

        JSONObject awoxdev = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(awoxdev, "device", device);

        Json.put(device, "uuid", uuid);
        Json.put(device, "did", Integer.toString(meshid));
        Json.put(device, "type", "smartbulb");
        Json.put(device, "name", name);
        Json.put(device, "model", model);
        Json.put(device, "brand", vendor);
        Json.put(device, "version", version);
        Json.put(device, "capabilities", getCapabilities());
        Json.put(device, "driver", "awx");

        JSONObject credentials = new JSONObject();
        Json.put(awoxdev, "credentials", credentials);

        Json.put(credentials, "meshname", meshname);
        Json.put(credentials, "meshpass", meshpass);

        JSONObject network = new JSONObject();
        Json.put(awoxdev, "network", network);

        Json.put(network, "mac", macaddr);

        Log.d(LOGTAG, "buildDeviceDescription json=" + Json.toPretty(awoxdev));

        //AWX.instance.onDeviceFound(awoxdev);
    }

    private String getCapabilities()
    {
        ArrayList<String> props = AWXDevices.getProperties(name);

        String caps;

        if (props.contains(AWXDevices.PROPERTY_PLUG_LED_STATE)
                || props.contains(AWXDevices.PROPERTY_PLUG_SCHEDULE)
                || props.contains(AWXDevices.PROPERTY_PLUG_MESH_SCHEDULE))
        {
            caps = "smartplug|plugonoff|ledonoff";
        }
        else
        {
            caps = "smartbulb|bulbonoff|dimmable|color|colorrgb";
        }

        caps += "|fixed|ble|stupid";

        if (props.contains(AWXDevices.PROPERTY_LIGHT_MODE))
        {
            caps += "|colormode";
        }

        if (props.contains(AWXDevices.PROPERTY_WHITE_TEMPERATURE))
        {
            caps += "|colortemp";
        }

        return caps;
    }

    private void evalNotification(byte[] plain)
    {
        byte[] payload = AWXProtocol.getData(plain);

        short targetMeshid = (short) (((payload[9] & 0xff) << 8) + (payload[0] & 0xff));

        if (targetMeshid != meshid)
        {
            Log.e(LOGTAG, "evalNotification: wrong meshid=" + meshid + " targetMeshid=" + targetMeshid);

            return;
        }

        byte command = AWXProtocol.getCommand(plain);

        if (command == AWXProtocol.COMMAND_NOTIFICATION_RECEIVED)
        {
            int wbright = payload[3] & 0xff;
            int wtemp = payload[4] & 0xff;
            int cbright = payload[5] & 0xff;
            int color = ((payload[6] & 0xff) << 16) + ((payload[7] & 0xff) << 8) + (payload[8] & 0xff);
            int powerstate = payload[2] & 0x01;
            int lightmode = (payload[2] >> 1) & 0x01;

            Log.d(LOGTAG, "evalNotification:"
                    + " mac=" + macaddr
                    + " meshid=" + meshid
                    + " cmd=" + command
                    + " ps=" + powerstate
                    + " lm=" + lightmode
                    + " wb=" + wbright
                    + " wt=" + wtemp
                    + " cb=" + cbright
                    + " c=0x" + Integer.toHexString(color)
                    + " paylod=" + Simple.getBytesToHexString(payload));
        }
    }
}
