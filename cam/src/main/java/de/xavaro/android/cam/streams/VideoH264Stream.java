package de.xavaro.android.cam.streams;

import java.io.IOException;

import android.graphics.ImageFormat;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.service.textservice.SpellCheckerService.Session;

import de.xavaro.android.cam.packets.H264Packetizer;
import de.xavaro.android.cam.util.MP4Config;

/**
 * A class for streaming H.264 from the camera of an android device using RTP.
 * You should use a {@link Session} instantiated with {@link SessionBuilder} instead of using this class directly.
 * Call {@link #setDestinationAddress(InetAddress)}, {@link #setDestinationPorts(int)} and {@link #setVideoQuality(VideoQuality)}
 * to configure the stream. You can then call {@link #start()} to start the RTP stream.
 * Call {@link #stop()} to stop the stream.
 */
public class VideoH264Stream extends VideoStream
{
    public final static String LOGTAG = "VideoH264Stream";

    private MP4Config mConfig;

    /**
     * Constructs the H.264 stream.
     * Uses CAMERA_FACING_BACK by default.
     */
    public VideoH264Stream()
    {
        this(CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * Constructs the H.264 stream.
     *
     * @param cameraId Can be either CameraInfo.CAMERA_FACING_BACK or CameraInfo.CAMERA_FACING_FRONT
     * @throws IOException
     */
    public VideoH264Stream(int cameraId)
    {
        super(cameraId);

        mMimeType = "video/avc";
        mCameraImageFormat = ImageFormat.NV21;
        mVideoEncoder = MediaRecorder.VideoEncoder.H264;
        mPacketizer = new H264Packetizer();
    }

    /**
     * Returns a description of the stream using SDP. It can then be included in an SDP file.
     */
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

    /**
     * Starts the stream.
     * This will also open the camera and display the preview if {@link #startPreview()} has not already been called.
     */
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

    /**
     * Configures the stream. You need to call this before calling {@link #getSessionDescription()} to apply
     * your configuration of the stream.
     */
    public synchronized void configure() throws IllegalStateException, IOException
    {
        super.configure();
        mMode = mRequestedMode;
        mQuality = mRequestedQuality.clone();
        mConfig = MP4Config.getConfigForVideoQuality(mQuality);
    }
}
