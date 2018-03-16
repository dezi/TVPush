package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import de.xavaro.android.gui.base.GUIPlugin;
import pub.android.interfaces.cam.Camera;
import zz.top.gls.GLSVideoView;

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

        pluginFrameParams.gravity = Gravity.TOP;
        pluginFrame.setLayoutParams(pluginFrameParams);

        pluginFrame.setBackgroundColor(0x88880000);
    }

    public void setPosition(int left, int top)
    {
        pluginFrameParams.leftMargin = left;
        pluginFrameParams.topMargin = top;

        pluginFrame.setLayoutParams(pluginFrameParams);
    }

    public int getPluginWidth()
    {
        return pluginFrameParams.width;
    }

    public int getPluginHeight()
    {
        return pluginFrameParams.height;
    }
}