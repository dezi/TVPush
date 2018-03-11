package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.view.Gravity;
import android.util.Log;

import de.xavaro.android.gui.base.GUIPlugin;

import zz.top.gls.GLSVideoView;

public class GUIVideoSurface extends GUIPlugin
{
    private final static String LOGTAG = GUISpeechRecogniton.class.getSimpleName();

    public GLSVideoView videoView;

    public GUIVideoSurface(Context context)
    {
        super(context);
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate:");

        super.onCreate();

        pluginFrameParams.leftMargin = 50;
        pluginFrameParams.topMargin = 50;

        pluginFrameParams.width = 480;
        pluginFrameParams.height = 270;

        pluginFrameParams.gravity = Gravity.TOP;

        pluginFrame.setLayoutParams(pluginFrameParams);

        videoView = new GLSVideoView(getContext());

        pluginFrame.addView(videoView);
    }
}