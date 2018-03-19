package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.view.Gravity;
import android.util.Log;

import de.xavaro.android.gui.base.GUIPlugin;

import pub.android.interfaces.cam.Camera;

import zz.top.gls.GLSVideoView;

public class GUIVideoSurface extends GUIPlugin
{
    private final static String LOGTAG = GUISpeechRecogniton.class.getSimpleName();

    private Camera camera;
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

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public Camera getCamera()
    {
        return camera;
    }
}