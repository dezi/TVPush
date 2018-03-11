package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.view.Gravity;
import android.util.Log;

import de.xavaro.android.gui.base.GUIPlugin;

import zz.top.gls.GLSVideoView;

public class GUIVideoSurface extends GUIPlugin
{
    private final static String LOGTAG = GUISpeechRecogniton.class.getSimpleName();

    private GLSVideoView glsVideoView;

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

        pluginFrameParams.width = 320;
        pluginFrameParams.height = 180;

        pluginFrameParams.gravity = Gravity.TOP;

        pluginFrame.setLayoutParams(pluginFrameParams);

        glsVideoView = new GLSVideoView(getContext());

        pluginFrame.addView(glsVideoView);
    }

    public void setPosition(int left, int top)
    {
        pluginFrameParams.leftMargin = left;
        pluginFrameParams.topMargin = top;

        pluginFrame.setLayoutParams(pluginFrameParams);
    }

    public GLSVideoView getGLSVideoView()
    {
        return glsVideoView;
    }
}