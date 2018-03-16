package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;

import de.xavaro.android.gui.base.GUIPlugin;

public class GUIChannelWizzard extends GUIPlugin
{
    private final static String LOGTAG = GUISpeechRecogniton.class.getSimpleName();

    public GUIChannelWizzard(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        pluginFrame.setBackgroundColor(0x88880000);
    }
}