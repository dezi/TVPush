package de.xavaro.android.iot.base;

@SuppressWarnings("WeakerAccess")
public class IOTMeme extends IOTObject
{
    public String memeHumanUUID;
    public String memeDeviceUUID;

    public IOTMeme(String uuid)
    {
        super(uuid);
    }
}
