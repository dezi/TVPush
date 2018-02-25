package de.xavaro.android.yihome;

import android.util.Log;

public class TNPReaderCommandThread extends TNPReaderThread
{
    private static final String LOGTAG = TNPReaderCommandThread.class.getSimpleName();

    public TNPReaderCommandThread(int sessionHandle, boolean isByteOrderBig)
    {
        super(sessionHandle, CHANNEL_COMMAND, isByteOrderBig);
    }

    @Override
    public void handleData(byte[] data, int size)
    {
        TNPIOCtrlHead head = TNPIOCtrlHead.parse(data, isByteOrderBig);

        Log.d(LOGTAG, "handleData:"
                + " command=" + head.commandType
                + " seq=" + head.commandNumber
                + " exsize=" + head.exHeaderSize
                + " datasize=" + head.dataSize);

        int dataOffset = TNPIOCtrlHead.HEADER_SIZE + head.exHeaderSize;
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
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoResp resp = AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoResp.parse(dataBuffer, isByteOrderBig);

                Log.d(LOGTAG, "handleData: deviceInfo:"
                    + " version=" + resp.version
                    + " total=" + resp.total
                    + " free=" + resp.free
                );
            }
        }
    }
}
