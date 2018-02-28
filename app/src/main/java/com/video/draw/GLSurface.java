package com.video.draw;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GLSurface extends GLSurfaceView
{
    public GLSurface(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        setDebugFlags(3);
    }

    public GLSurface(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }
}
