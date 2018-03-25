package de.xavaro.android.adb;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("WeakerAccess")
public class AdbMess
{
    private static final String LOGTAG = AdbMess.class.getSimpleName();

    public static final int ADB_HEADER_LENGTH = 24;

    public String cmdstr;

    public int command;
    public int arg0;
    public int arg1;
    public int payloadLength;
    public int checksum;
    public int magic;

    public byte[] payload;

    public AdbMess()
    {
    }

    public AdbMess(int command, int arg0, int arg1, byte[] payload)
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

        this.command = command;
        this.arg0 = arg0;
        this.arg1 = arg1;
        this.payload = (payload == null) ? new byte[0] : payload;
        this.payloadLength = this.payload.length;
    }

    public byte[] getMessageBytes()
    {
        ByteBuffer message;

        message = ByteBuffer.allocate(AdbMess.ADB_HEADER_LENGTH + payload.length).order(ByteOrder.LITTLE_ENDIAN);

        message.putInt(command);
        message.putInt(arg0);
        message.putInt(arg1);
        message.putInt(payload.length);
        message.putInt(getPayloadChecksum());
        message.putInt(~command);
        message.put(payload);

        return message.array();
    }

    @Nullable
    public static AdbMess readAdbMessage(InputStream inputStream)
    {
        try
        {
            AdbMess msg = new AdbMess();

            ByteBuffer packet = ByteBuffer.allocate(ADB_HEADER_LENGTH).order(ByteOrder.LITTLE_ENDIAN);

            int bytesRead = inputStream.read(packet.array(), 0, ADB_HEADER_LENGTH);
            if (bytesRead != ADB_HEADER_LENGTH) return null;

            msg.command = packet.getInt();
            msg.arg0 = packet.getInt();
            msg.arg1 = packet.getInt();
            msg.payloadLength = packet.getInt();
            msg.checksum = packet.getInt();
            msg.magic = packet.getInt();

            byte[] cmdstr = new byte[ 4 ];

            cmdstr[ 0 ] = (byte) msg.command;
            cmdstr[ 1 ] = (byte) (msg.command >> 8);
            cmdstr[ 2 ] = (byte) (msg.command >> 16);
            cmdstr[ 3 ] = (byte) (msg.command >> 24);

            msg.cmdstr = new String(cmdstr);

            msg.payload = new byte[msg.payloadLength];
            bytesRead = inputStream.read(msg.payload);
            if (bytesRead != msg.payload.length) return null;

            return msg;
        }
        catch (SocketTimeoutException ignore)
        {
            //Log.e(LOGTAG, "readAdbMessage: socket timed out!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean validateMessage()
    {
        if (command == ~magic)
        {
            if ((payloadLength == 0) || (getPayloadChecksum() == checksum))
            {
                return true;
            }
        }

        return false;
    }

    public int getPayloadChecksum()
    {
        int checksum = 0;

        for (byte bite : payload) checksum += bite & 0xff;

        return checksum;
    }
}
