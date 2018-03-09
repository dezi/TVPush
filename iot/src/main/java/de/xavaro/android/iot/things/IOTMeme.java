package de.xavaro.android.iot.things;

@SuppressWarnings("WeakerAccess")
public class IOTMeme extends IOTBase
{
    private final static String LOGTAG = IOTMeme.class.getSimpleName();

    public String memeHumanUUID;
    public String memeDeviceUUID;

    public IOTMeme(String uuid)
    {
        super(uuid);
    }
}
