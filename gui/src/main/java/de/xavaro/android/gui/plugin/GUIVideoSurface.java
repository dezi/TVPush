package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.view.Gravity;

import de.xavaro.android.gui.base.GUIPlugin;

import pub.android.interfaces.pub.PUBCamera;

import zz.top.gls.GLSVideoView;

public class GUIVideoSurface extends GUIPlugin
{
    private final static String LOGTAG = GUIToastBar.class.getSimpleName();

    private PUBCamera camera;
    private GLSVideoView glsVideoView;

    public GUIVideoSurface(Context context)
    {
        super(context);

        params.width = 320;
        params.height = 180;

        params.gravity = Gravity.TOP;

        setLayoutParams(params);

        glsVideoView = new GLSVideoView(getContext());

        contentFrame.addView(glsVideoView);
    }

    public GLSVideoView getGLSVideoView()
    {
        return glsVideoView;
    }

    public void setCamera(PUBCamera camera)
    {
        this.camera = camera;
    }

    public PUBCamera getCamera()
    {
        return camera;
    }
}