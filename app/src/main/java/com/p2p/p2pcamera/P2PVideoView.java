package com.p2p.p2pcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.video.draw.EGLConfigChooser;
import com.video.draw.EGLContextFactory;
import com.video.draw.EGLWindowSurfaceFactory;
import com.video.draw.GLSurface;
import com.video.draw.Renderer;

public class P2PVideoView extends FrameLayout
{
    public GLSurface mGlSurface;
    public Renderer mRenderer;
    
    public P2PVideoView(Context context)
    {
        super(context);
        init(context);
    }

    public P2PVideoView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    public P2PVideoView(Context context, AttributeSet attributeSet, int defStyleAttr)
    {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        setBackgroundColor(0x88008800);

        mGlSurface = new GLSurface(context);

        mGlSurface.setEGLConfigChooser(new EGLConfigChooser(8, 8, 8, 8, 0, 0));
        mGlSurface.setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory());
        mGlSurface.setEGLContextFactory(new EGLContextFactory());
        mGlSurface.getHolder().setFormat(-1);
        mGlSurface.setEGLContextClientVersion(2);

        mRenderer = new Renderer();
        mGlSurface.setRenderer(mRenderer);

        mGlSurface.setDebugFlags(3);
        mGlSurface.setRenderMode(0);

        mRenderer.setListener(new Renderer.FrameListener()
        {
            public void onNewFrame()
            {
                mGlSurface.requestRender();
            }

            public void onSurfaceChanged()
            {
            }

            public void onSurfaceCreate()
            {
            }
        });

        addView(mGlSurface);
    }
}
