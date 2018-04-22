package de.xavaro.android.awx.comm;

import android.annotation.SuppressLint;
import android.util.Log;

import com.telink.crypto.AES;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AWXProtocol
{
    private static final String LOGTAG = AWXProtocol.class.getSimpleName();

    public static final String CHARACTERISTIC_MESH_LIGHT_COMMAND = "00010203-0405-0607-0809-0a0b0c0d1912";
    public static final String CHARACTERISTIC_MESH_LIGHT_OTA = "00010203-0405-0607-0809-0a0b0c0d1913";
    public static final String CHARACTERISTIC_MESH_LIGHT_PAIR = "00010203-0405-0607-0809-0a0b0c0d1914";
    public static final String CHARACTERISTIC_MESH_LIGHT_STATUS = "00010203-0405-0607-0809-0a0b0c0d1911";
    static final byte COMMAND_GET_ALARMS_RECEIVED = (byte) -25;
    static final byte COMMAND_GET_ALARMS_SENT = (byte) -26;
    static final byte COMMAND_GET_DEVICE_ADDRESS = (byte) -21;
    static final byte COMMAND_GET_GROUPS_SENT = (byte) -35;
    static final byte COMMAND_GET_GROUP_RECEIVED = (byte) -44;
    static final byte COMMAND_GET_PIR_CURRENT_LUMINOSITY = (byte) 18;
    static final byte COMMAND_GET_PIR_SETTINGS = (byte) 17;
    static final byte COMMAND_GET_PLUG_MESH_CONSUMPTION_DAILY = (byte) 21;
    static final byte COMMAND_GET_PLUG_MESH_CONSUMPTION_HOURLY = (byte) 20;
    static final byte COMMAND_GET_PLUG_MESH_CURRENT_DATA = (byte) 19;
    static final byte COMMAND_GET_PLUG_MESH_SCHEDULE = (byte) 22;
    static final byte COMMAND_GET_SCENES_RECEIVED = (byte) -63;
    static final byte COMMAND_GET_SCENES_SENT = (byte) -64;
    static final byte COMMAND_GET_STATUS_RECEIVED = (byte) -37;
    static final byte COMMAND_GET_STATUS_SENT = (byte) -38;
    static final byte COMMAND_GET_TIME_RECEIVED = (byte) -23;
    static final byte COMMAND_GET_TIME_SENT = (byte) -24;
    static final byte COMMAND_GET_USER_NOTIFY_RECEIVED = (byte) -40;
    static final byte COMMAND_GET_USER_NOTIFY_SENT = (byte) -22;
    static final byte COMMAND_KICK_OUT = (byte) -29;
    static final byte COMMAND_LOAD_SCENE = (byte) -17;
    static final byte COMMAND_MISC = (byte) -48;
    static final byte COMMAND_MISC_2 = (byte) -9;
    static final byte COMMAND_NOTIFICATION_RECEIVED = (byte) -36;
    static final byte COMMAND_SET_ALARM = (byte) -27;
    static final byte COMMAND_SET_COLOR = (byte) -30;
    static final byte COMMAND_SET_COLOR_BRIGHTNESS = (byte) -14;
    static final byte COMMAND_SET_COLOR_SEQUENCE_COLOR_DURATION = (byte) -11;
    static final byte COMMAND_SET_COLOR_SEQUENCE_FADE_DURATION = (byte) -10;
    static final byte COMMAND_SET_COLOR_SEQUENCE_PRESET = (byte) -56;
    static final byte COMMAND_SET_GROUP = (byte) -41;
    static final byte COMMAND_SET_LED_STATE = (byte) 23;
    static final byte COMMAND_SET_LIGHT_MODE = (byte) 51;
    static final byte COMMAND_SET_MESH_ADDRESS_RECEIVED = (byte) -31;
    static final byte COMMAND_SET_MESH_ADDRESS_SENT = (byte) -32;
    static final byte COMMAND_SET_PIR_SETTINGS = (byte) 0;
    static final byte COMMAND_SET_POWER_STATE = (byte) -48;
    static final byte COMMAND_SET_SCHEDULE = (byte) -34;
    static final byte COMMAND_SET_TIME = (byte) -28;
    static final byte COMMAND_SET_WHITE_BRIGHTNESS = (byte) -15;
    static final byte COMMAND_SET_WHITE_TEMPERATURE = (byte) -16;
    static final byte COMMAND_STORE_SCENE = (byte) -1;
    static final byte EXTENDED_COMMAND_RESCUE_PEBBLE = (byte) 1;
    static final byte EXTENDED_COMMAND_SET_ALL_GROUPS = (byte) 18;
    static final int INDEX_DAWN_SIMULATOR = 1;
    static final int INDEX_NIGHTLIGHT = 8;
    static final int INDEX_PRESENCE_SIMULATOR = 0;
    static final int INDEX_PROGRAM = 15;
    static final int INDEX_RANGE_1 = 0;
    static final int INDEX_RANGE_2 = 1;
    static final int INDEX_RANGE_3 = 2;
    static final int INDEX_RANGE_4 = 3;
    static final int INDEX_RANGE_5 = 4;
    static final int INDEX_RANGE_6 = 5;
    static final int INDEX_TIMER = 0;
    static final byte OPCODE_ENC_FAIL = (byte) 14;
    static final byte OPCODE_ENC_REQ = (byte) 12;
    static final byte OPCODE_ENC_RSP = (byte) 13;
    static final byte OPCODE_PAIR_CONFIRM = (byte) 7;
    static final byte OPCODE_PAIR_LTK = (byte) 6;
    static final byte OPCODE_PAIR_NETWORK_NAME = (byte) 4;
    static final byte OPCODE_PAIR_PASS = (byte) 5;
    static final byte PARAMETER_EXTENDED_COMMANDS = (byte) -18;
    static final byte PARAM_USER_NOTIFY_DAILY_SCHEDULE = (byte) 8;
    static final byte PARAM_USER_NOTIFY_DEVICE_ADDRESS = (byte) 0;
    static final byte PARAM_USER_NOTIFY_SEQUENCE_COLOR_DURATION = (byte) 1;
    static final byte PARAM_USER_NOTIFY_SEQUENCE_TRANSITION_DURATION = (byte) 2;
    static final byte PARAM_USER_NOTIFY_SIMPLE_SCHEDULE = (byte) 7;
    static final byte RELAY_TIMES = (byte) 16;
    static final String SERVICE_TELINK = "00010203-0405-0607-0809-0a0b0c0d1910";
    private static final byte[] SRC = new byte[]{(byte) 0, (byte) 0};
    static final int TYPE_DAWN_SIMULATOR = 2;
    static final int TYPE_NIGHTLIGHT = 1;
    static final int TYPE_PLUG_MESH_SCHEDULE = 4;
    static final int TYPE_PRESENCE_SIMULATOR = 6;
    static final int TYPE_TIMER = 3;
    private static final byte[] VENDOR_ID = new byte[]{(byte) 96, (byte) 1};
    private static int sSequenceNumber;

    @SuppressLint({"GetInstance"})
    static byte[] encrypt(byte[] key, byte[] content)
    {
        key = AWXByteUtils.reverse(key);
        content = AWXByteUtils.reverse(content);
        try
        {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(1, secretKeySpec);
            content = cipher.doFinal(content);
        }
        catch (Exception e)
        {
        }
        return content;
    }

    static byte[] getPairValue(byte[] meshName, byte[] meshPassword, byte[] sessionRandom)
    {
        byte[] encrypted = encrypt(Arrays.copyOf(sessionRandom, 16), AWXByteUtils.xor(meshName, meshPassword));
        byte[] value = new byte[17];
        value[0] = OPCODE_ENC_REQ;
        System.arraycopy(sessionRandom, 0, value, 1, sessionRandom.length);
        System.arraycopy(AWXByteUtils.reverse(encrypted), 0, value, 9, 8);
        return value;
    }

    static byte[] getSessionKey(byte[] meshName, byte[] meshPassword, byte[] sessionRandom, byte[] responseRandom)
    {
        byte[] random = new byte[16];
        System.arraycopy(sessionRandom, 0, random, 0, sessionRandom.length);
        System.arraycopy(responseRandom, 0, random, 8, responseRandom.length);
        return AWXByteUtils.reverse(encrypt(AWXByteUtils.xor(meshName, meshPassword), random));
    }

    static byte[] encryptValue(String address, byte[] sessionKey, byte[] value)
    {
        byte[] vector = new byte[8];
        System.arraycopy(AWXByteUtils.reverse(AWXHardwareUtils.getAddress(address)), 0, vector, 0, 4);
        vector[4] = (byte) 1;
        System.arraycopy(value, 0, vector, 5, 3);
        return AES.encrypt(sessionKey, vector, value);
    }

    static byte[] decryptValue(String address, byte[] sessionKey, byte[] value)
    {
        byte[] vector = new byte[8];
        System.arraycopy(AWXByteUtils.reverse(AWXHardwareUtils.getAddress(address)), 0, vector, 0, 3);
        System.arraycopy(value, 0, vector, 3, 5);
        return AES.decrypt(sessionKey, vector, value);
    }

    static byte[] getValue(short dest, byte command, byte[] data)
    {
        return getValue(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(dest).array(), command, data);
    }

    static byte[] getValue(byte[] dest, byte command, byte[] data)
    {
        sSequenceNumber++;
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.put((byte) (sSequenceNumber & 255));
        buffer.put((byte) ((sSequenceNumber >> 8) & 255));
        buffer.put((byte) ((sSequenceNumber >> 16) & 255));
        buffer.put(SRC);
        buffer.put(dest);
        buffer.put(command);
        buffer.put(VENDOR_ID);
        buffer.put(data);
        return buffer.array();
    }

    static byte getCommand(byte[] value)
    {
        return value[7];
    }

    static byte[] getData(byte[] value)
    {
        byte[] data = new byte[(value.length - 10)];
        System.arraycopy(value, 10, data, 0, data.length);
        return data;
    }
}
