package de.xavaro.android.cam.streams;

/**
 * A class that represents the quality of an audio stream.
 */
public class AudioQuality
{
    public final static AudioQuality DEFAULT_AUDIO_QUALITY = new AudioQuality(8000, 32000);

    private int samplingRate = 0;
    private int bitRate = 0;

    public AudioQuality(int samplingRate, int bitRate)
    {
        this.samplingRate = samplingRate;
        this.bitRate = bitRate;
    }

    public boolean equals(AudioQuality quality)
    {
        if (quality == null) return false;

        return ((quality.samplingRate == this.samplingRate) && (quality.bitRate == this.bitRate));
    }

    @Override
    public AudioQuality clone()
    {
        return new AudioQuality(samplingRate, bitRate);
    }

    public static AudioQuality parseQuality(String str)
    {
        AudioQuality quality = DEFAULT_AUDIO_QUALITY.clone();

        if (str != null)
        {
            String[] config = str.split("-");

            try
            {
                quality.bitRate = Integer.parseInt(config[0]) * 1000; // conversion to bit/s
                quality.samplingRate = Integer.parseInt(config[1]);
            }
            catch (IndexOutOfBoundsException ignore)
            {
            }
        }

        return quality;
    }
}
