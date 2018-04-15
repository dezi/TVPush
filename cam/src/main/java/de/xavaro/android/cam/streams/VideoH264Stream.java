package de.xavaro.android.cam.streams;

import java.io.IOException;

import android.graphics.ImageFormat;
import android.media.MediaRecorder;

import de.xavaro.android.cam.packets.H264Packetizer;
import de.xavaro.android.cam.util.MP4Config;

public class VideoH264Stream extends VideoStream
{
    public final static String LOGTAG = "VideoH264Stream";

    private MP4Config mConfig;

    public VideoH264Stream(int cameraId)
    {
        super(cameraId);

        mMimeType = "video/avc";
        mCameraImageFormat = ImageFormat.NV21;
        mVideoEncoder = MediaRecorder.VideoEncoder.H264;
        mPacketizer = new H264Packetizer();
    }

    public synchronized String getSessionDescription() throws IllegalStateException
    {
        if (mConfig == null)
            throw new IllegalStateException("You need to call configure() first !");

        return "m=video " + String.valueOf(getDestinationPorts()[0]) + " RTP/AVP 96\r\n" +
                "a=rtpmap:96 H264/90000\r\n" +
                "a=fmtp:96 packetization-mode=1;"
                + "profile-level-id=" + mConfig.getPFLB64()
                + ";sprop-parameter-sets=" + mConfig.getSPSB64() + "," + mConfig.getPPSB64()
                + ";\r\n";
    }

    public synchronized void start() throws IllegalStateException, IOException
    {
        if (!mStreaming)
        {
            configure();

            byte[] pps = mConfig.getPPS();
            byte[] sps = mConfig.getSPS();

            ((H264Packetizer) mPacketizer).setStreamParameters(pps, sps);

            super.start();
        }
    }

    public synchronized void configure() throws IllegalStateException, IOException
    {
        super.configure();
        mMode = mRequestedMode;
        mQuality = mRequestedQuality.clone();
        mConfig = MP4Config.getConfigForVideoQuality(mQuality);
    }
}
