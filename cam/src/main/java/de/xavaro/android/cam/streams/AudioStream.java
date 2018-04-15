package de.xavaro.android.cam.streams;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Don't use this class directly.
 */
public abstract class AudioStream extends MediaStream
{

    protected int mAudioSource;
    protected int mOutputFormat;
    protected int mAudioEncoder;
    protected AudioQuality mRequestedQuality = AudioQuality.DEFAULT_AUDIO_QUALITY.clone();
    protected AudioQuality mQuality = mRequestedQuality.clone();

    public AudioStream()
    {
        setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    }

    public void setAudioSource(int audioSource)
    {
        mAudioSource = audioSource;
    }

    public void setAudioQuality(AudioQuality quality)
    {
        mRequestedQuality = quality;
    }

    /**
     * Returns the quality of the stream.
     */
    public AudioQuality getAudioQuality()
    {
        return mQuality;
    }

    protected void setAudioEncoder(int audioEncoder)
    {
        mAudioEncoder = audioEncoder;
    }

    protected void setOutputFormat(int outputFormat)
    {
        mOutputFormat = outputFormat;
    }
}
