package de.xavaro.android.adb;

import android.support.annotation.Nullable;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings({"WeakerAccess", "unused"})
public class AdbProtocol
{
    public static final int ADB_HEADER_LENGTH = 24;

    public static final int AUTH_TYPE_TOKEN = 1;
    public static final int AUTH_TYPE_SIGNATURE = 2;
    public static final int AUTH_TYPE_RSA_PUBLIC = 3;

    public static final int CMD_SYNC = 0x434e5953;
    public static final int CMD_CNXN = 0x4e584e43;
    public static final int CMD_AUTH = 0x48545541;
    public static final int CMD_OPEN = 0x4e45504f;
    public static final int CMD_OKAY = 0x59414b4f;
    public static final int CMD_CLSE = 0x45534c43;
    public static final int CMD_WRTE = 0x45545257;

    public static final int CONNECT_VERSION = 0x01000000;
    public static final int CONNECT_MAXDATA = 4096;

    public static final String CONNECT_SERVICE = "host::";

    public static boolean validateMessage(AdbMessage msg)
    {
        if (msg == null) return false;

        if (msg.command != ~msg.magic) return false;

        if (msg.payloadLength != 0)
        {
            if (getPayloadChecksum(msg.payload) != msg.checksum)
            {
                return false;
            }
        }

        return true;
    }

    private static int getPayloadChecksum(byte[] payload)
    {
        int checksum = 0;

        for (byte bite : payload) checksum += bite & 0xff;

        return checksum;
    }

    public static byte[] generateMessage(int cmd, int arg0, int arg1, byte[] payload)
    {
        //
        // From original adb implementation:
        //
		// struct message
        // {
        // 		unsigned command;       // command identifier constant
        // 		unsigned arg0;          // first argument
        // 		unsigned arg1;          // second argument
        // 		unsigned data_length;   // length of payload (0 is allowed)
        // 		unsigned data_check;    // checksum of data payload
        // 		unsigned magic;         // command ^ 0xffffffff
        // }
        //

		if (payload == null) payload = new byte[0];

        ByteBuffer message;

        message = ByteBuffer.allocate(ADB_HEADER_LENGTH + payload.length).order(ByteOrder.LITTLE_ENDIAN);

        message.putInt(cmd);
        message.putInt(arg0);
        message.putInt(arg1);
        message.putInt(payload.length);
        message.putInt(getPayloadChecksum(payload));
        message.putInt(~cmd);
        message.put(payload);

        return message.array();
    }

    public static byte[] generateConnect()
    {
        byte[] dest = CONNECT_SERVICE.getBytes();

        ByteBuffer data = ByteBuffer.allocate(dest.length + 1);

        data.put(dest);
        data.put((byte) 0);

        return generateMessage(CMD_CNXN, CONNECT_VERSION, CONNECT_MAXDATA, data.array());
    }

    public static byte[] generateAuth(int type, byte[] data)
    {
        return generateMessage(CMD_AUTH, type, 0, data);
    }

    public static byte[] generateOpen(int localId, String service)
    {
        byte[] dest = service.getBytes();

        ByteBuffer data = ByteBuffer.allocate(dest.length + 1);

        data.put(dest);
        data.put((byte) 0);

        return generateMessage(CMD_OPEN, localId, 0, data.array());
    }

    public static byte[] generateWrite(int localId, int remoteId, byte[] data)
    {
        return generateMessage(CMD_WRTE, localId, remoteId, data);
    }

    public static byte[] generateClose(int localId, int remoteId)
    {
        return generateMessage(CMD_CLSE, localId, remoteId, null);
    }

    public static byte[] generateOkay(int localId, int remoteId)
    {
        return generateMessage(CMD_OKAY, localId, remoteId, null);
    }

    @Nullable
    public static AdbMessage readAdbMessage(InputStream in)
    {
        try
        {
            AdbMessage msg = new AdbMessage();

            ByteBuffer packet = ByteBuffer.allocate(ADB_HEADER_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
            int bytesRead = in.read(packet.array(), 0, ADB_HEADER_LENGTH);
            if (bytesRead != ADB_HEADER_LENGTH) return null;

            msg.command = packet.getInt();
            msg.arg0 = packet.getInt();
            msg.arg1 = packet.getInt();
            msg.payloadLength = packet.getInt();
            msg.checksum = packet.getInt();
            msg.magic = packet.getInt();

            msg.payload = new byte[msg.payloadLength];
            bytesRead = in.read(msg.payload);
            if (bytesRead != msg.payload.length) return null;

            return msg;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static class AdbMessage
    {
        public int command;
        public int arg0;
        public int arg1;
        public int payloadLength;
        public int checksum;
        public int magic;
        public byte[] payload;
    }
}
