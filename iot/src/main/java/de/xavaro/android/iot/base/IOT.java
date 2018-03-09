package de.xavaro.android.iot.base;

import android.content.Context;

import de.xavaro.android.iot.comm.IOTMessage;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;

public class IOT
{
    public static IOTMeme meme;
    public static IOTHuman human;
    public static IOTDevice device;

    public static IOTMessage message;

    public static void initialize(Context context)
    {
        IOTBoot.initialize();

        IOTMessage.initialize();

        IOTService.startService(context);
    }
}
