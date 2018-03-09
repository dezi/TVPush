package de.xavaro.android.iot.things;

@SuppressWarnings("WeakerAccess")
public class IOTMeme extends IOTBaseThing
{
    public String memeHumanUUID;
    public String memeDeviceUUID;

    public IOTMeme(String uuid)
    {
        super(uuid);
    }
}
