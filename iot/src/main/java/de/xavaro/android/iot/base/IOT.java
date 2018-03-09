package de.xavaro.android.iot.base;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;

public class IOT
{
    public static IOTMeme meme;
    public static IOTHuman human;
    public static IOTDevice device;

    public static void initialize()
    {
        IOTBoot.initialize();
    }
}
