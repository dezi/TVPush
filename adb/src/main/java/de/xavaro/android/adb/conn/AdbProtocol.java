package de.xavaro.android.adb.conn;

import java.nio.ByteBuffer;

@SuppressWarnings({"WeakerAccess", "unused"})
public class AdbProtocol
{
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

    public static byte[] buildMessage(int command, int arg0, int arg1, byte[] payload)
    {
        AdbMess msg = new AdbMess(command, arg0, arg1, payload);
        return msg.getMessageBytes();
   }

    public static byte[] buildConnect()
    {
        byte[] dest = CONNECT_SERVICE.getBytes();

        ByteBuffer data = ByteBuffer.allocate(dest.length + 1);

        data.put(dest);
        data.put((byte) 0);

        return buildMessage(CMD_CNXN, CONNECT_VERSION, CONNECT_MAXDATA, data.array());
    }

    public static byte[] buildAuth(int type, byte[] data)
    {
        return buildMessage(CMD_AUTH, type, 0, data);
    }

    public static byte[] buildOpen(int locId, String service)
    {
        byte[] servdata = service.getBytes();

        ByteBuffer data = ByteBuffer.allocate(servdata.length + 1);

        data.put(servdata);
        data.put((byte) 0);

        return buildMessage(CMD_OPEN, locId, 0, data.array());
    }

    public static byte[] buildWrite(int locId, int remId, byte[] data)
    {
        return buildMessage(CMD_WRTE, locId, remId, data);
    }

    public static byte[] buildClose(int locId, int remId)
    {
        return buildMessage(CMD_CLSE, locId, remId, null);
    }

    public static byte[] buildOkay(int locId, int remId)
    {
        return buildMessage(CMD_OKAY, locId, remId, null);
    }
}
