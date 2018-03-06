package zz.top.p2p.camera;

import android.util.Log;

import zz.top.p2p.commands.DeviceInfoData;
import zz.top.p2p.commands.CommandCodes;
import zz.top.p2p.commands.ResolutionData;

public class P2PReaderThreadContl extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadContl.class.getSimpleName();

    public P2PReaderThreadContl(P2PSession session)
    {
        super(session, CHANNEL_COMMAND);
    }

    @Override
    public boolean handleData(byte[] data, int size)
    {
        P2PFrame frame = P2PFrame.parse(data, 0, session.isBigEndian);

        Log.d(LOGTAG, "handleData:"
                + " command=" + frame.commandType
                + " seq=" + frame.commandNumber
                + " exsize=" + frame.exHeaderSize
                + " datasize=" + frame.dataSize);

        int dataOffset = P2PFrame.FRAME_SIZE + frame.exHeaderSize;
        int dataSize = size - dataOffset;

        Log.d(LOGTAG, "handleData: head.authResult=" + frame.authResult);

        if (frame.authResult != 0) return false;

        if ((frame.exHeaderSize < 0) || (frame.dataSize != dataSize))
        {
            Log.d(LOGTAG, "handleData: corrupt...");

            return false;
        }
        else
        {
            byte[] dataBuffer = new byte[ dataSize ];
            System.arraycopy(data, dataOffset, dataBuffer, 0, dataSize);

            if ((frame.commandType == CommandCodes.IPCAM_DEVINFO_RESP)
             || (frame.commandType == CommandCodes.IPCAM_SET_DAYNIGHT_MODE_RESP))
            {
                DeviceInfoData deviceInfo = new DeviceInfoData(session, dataBuffer);
                session.onDeviceInfoReceived(deviceInfo);

                Log.d(LOGTAG, "DeviceInfoData: dayNight=" + deviceInfo.day_night_mode);

                return true;
            }

            if ((frame.commandType == CommandCodes.IPCAM_SET_RESOLUTION_RESP)
                || (frame.commandType == CommandCodes.IPCAM_GET_RESOLUTION_RESP))
            {
                ResolutionData resolution = new ResolutionData(session, dataBuffer);
                session.onResolutionReceived(resolution);

                return true;
            }

            Log.d(LOGTAG, "handleData: unhandled commandType=" + frame.commandType);
        }

        return true;
    }
}
