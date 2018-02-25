package de.xavaro.android.yihome;

import android.util.Log;

public class P2PReaderThreadCommand extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadCommand.class.getSimpleName();

    public P2PReaderThreadCommand(P2PSession session)
    {
        super(session, CHANNEL_COMMAND);
    }

    @Override
    public void handleData(byte[] data, int size)
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

        if ((head.exHeaderSize < 0) || (head.dataSize != dataSize))
        {
            Log.d(LOGTAG, "handleData: corrupt...");
        }
        else
        {
            byte[] dataBuffer = new byte[ dataSize ];
            System.arraycopy(data, dataOffset, dataBuffer, 0, dataSize);

            if (head.commandType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP)
            {
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoResp resp = AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoResp.parse(dataBuffer, session.isBigEndian);

                Log.d(LOGTAG, "handleData: deviceInfo:"
                    + " version=" + resp.version
                    + " total=" + resp.total
                    + " free=" + resp.free
                );
            }
        }
    }
}
