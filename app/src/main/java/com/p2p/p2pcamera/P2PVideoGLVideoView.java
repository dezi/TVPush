package com.p2p.p2pcamera;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.SparseArray;
import android.widget.FrameLayout;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import de.xavaro.android.tvpush.ApplicationBase;

public class P2PVideoGLVideoView extends FrameLayout
{
    private static final String LOGTAG = P2PVideoGLVideoView.class.getSimpleName();

    private Handler handler = new Handler();
    private P2PVideoGLSurfaceView surface;
    private FrameLayout maldat;

    public P2PVideoGLVideoView(Context context)
    {
        super(context);
        init(context);
    }

    public P2PVideoGLVideoView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    private SparseArray<Face> lastFaces;

    private void init(Context context)
    {
        surface = new P2PVideoGLSurfaceView(context);

        surface.renderer.setOnFacesDetectedListener(new P2PVideoGLRenderer.OnFacesDetectedListener()
        {
            @Override
            public void onFacesDetected(SparseArray<Face> faces)
            {
                lastFaces = faces;

                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        maldat.invalidate();
                    }
                });
            }
        });

        addView(surface);

        maldat = new FrameLayout(context)
        {
            @Override
            protected void onDraw(Canvas canvas)
            {
                super.onDraw(canvas);

                if (lastFaces != null)
                {
                    Log.d(LOGTAG, "maldat: onDraw faces=" + lastFaces.size());
                }
            }
        };

        //
        // Do not fuck with this. Empty frame
        // layouts are not drawn at all!
        //

        maldat.setBackgroundColor(Color.TRANSPARENT);

        addView(maldat);
    }

    public void setSourceDecoder(P2PVideoGLDecoder decoder)
    {
        surface.renderer.setSourceDecoder(decoder);
    }

    public void setSourceDimensions(int width, int height)
    {
        surface.renderer.setSourceDimensions(width, height);
    }

    public void requestRender()
    {
        surface.requestRender();
    }
}
