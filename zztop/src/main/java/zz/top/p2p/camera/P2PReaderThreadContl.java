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
    public boolean handleData(byte[] headBuff, int headSize, byte[] dataBuff, int dataSize)
    {
        P2PFrame frame = P2PFrame.parse(headBuff, 0, session.isBigEndian);

        Log.d(LOGTAG, "handleData:"
                + " command=" + frame.commandType
                + " seq=" + frame.commandNumber
                + " exsize=" + frame.exHeaderSize
                + " datasize=" + frame.dataSize);

        Log.d(LOGTAG, "handleData: head.authResult=" + frame.authResult);

        if (frame.authResult != 0) return false;

        if ((frame.exHeaderSize < 0) || (frame.dataSize != dataSize))
        {
            Log.d(LOGTAG, "handleData: corrupt...");

            return false;
        }
        else
        {
            if ((frame.commandType == CommandCodes.IPCAM_DEVINFO_RESP)
             || (frame.commandType == CommandCodes.IPCAM_SET_DAYNIGHT_MODE_RESP))
            {
                DeviceInfoData deviceInfo = new DeviceInfoData(session, dataBuff);
                session.onDeviceInfoReceived(deviceInfo);

                Log.d(LOGTAG, "DeviceInfoData: dayNight=" + deviceInfo.day_night_mode);

                return true;
            }

            if ((frame.commandType == CommandCodes.IPCAM_SET_RESOLUTION_RESP)
                || (frame.commandType == CommandCodes.IPCAM_GET_RESOLUTION_RESP))
            {
                ResolutionData resolution = new ResolutionData(session, dataBuff);
                session.onResolutionReceived(resolution);

                return true;
            }

            Log.d(LOGTAG, "handleData: unhandled commandType=" + frame.commandType);
        }

        return true;
    }
}
