package com.p2p.p2pcamera;

import android.util.Log;

import com.p2p.p2pcamera.p2pcommands.DeviceInfoData;
import com.p2p.p2pcamera.p2pcommands.ResolutionData;

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
        P2PFrame head = P2PFrame.parse(data, session.isBigEndian);

        Log.d(LOGTAG, "handleData:"
                + " command=" + head.commandType
                + " seq=" + head.commandNumber
                + " exsize=" + head.exHeaderSize
                + " datasize=" + head.dataSize);

        int dataOffset = P2PFrame.FRAME_SIZE + head.exHeaderSize;
        int dataSize = size - dataOffset;

        Log.d(LOGTAG, "handleData: head.authResult=" + head.authResult);

        if (head.authResult != 0) return false;

        if ((head.exHeaderSize < 0) || (head.dataSize != dataSize))
        {
            Log.d(LOGTAG, "handleData: corrupt...");

            return false;
        }
        else
        {
            byte[] dataBuffer = new byte[ dataSize ];
            System.arraycopy(data, dataOffset, dataBuffer, 0, dataSize);

            if (head.commandType == P2PCommandCodes.IPCAM_DEVINFO_RESP)
            {
                DeviceInfoData deviceInfo = new DeviceInfoData(session, dataBuffer);
                session.onDeviceInfoReceived(deviceInfo);

                return true;
            }

            if ((head.commandType == P2PCommandCodes.IPCAM_SET_RESOLUTION_RESP)
                || (head.commandType == P2PCommandCodes.IPCAM_GET_RESOLUTION_RESP))
            {
                ResolutionData resolution = new ResolutionData(session, dataBuffer);
                session.onResolutionReceived(resolution);

                return true;
            }

            Log.d(LOGTAG, "handleData: unhandled commandType=" + head.commandType);
        }

        return true;
    }
}
