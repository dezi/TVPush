package zz.top.p2p.video;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.SparseArray;
import android.widget.FrameLayout;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.google.android.gms.vision.face.Face;

import zz.top.p2p.camera.P2PAVFrame;

public class VideoGLVideoView extends FrameLayout
{
    private static final String LOGTAG = VideoGLVideoView.class.getSimpleName();

    private Handler handler = new Handler();
    private VideoGLSurfaceView surface;
    private FrameLayout maldat;

    public VideoGLVideoView(Context context)
    {
        super(context);
        init(context);
    }

    public VideoGLVideoView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    private SparseArray<Face> lastFaces;
    private int lastFacesWidth;
    private int lastFacesHeight;

    private void init(Context context)
    {
        surface = new VideoGLSurfaceView(context);

        surface.renderer.setOnFacesDetectedListener(new VideoGLRenderer.OnFacesDetectedListener()
        {
            @Override
            public void onFacesDetected(SparseArray<Face> faces, int imageWidth, int imageHeight)
            {
                lastFaces = faces;
                lastFacesWidth = imageWidth;
                lastFacesHeight = imageHeight;

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

                VideoGLFaceDetect.drawFaces(canvas, lastFaces, lastFacesWidth, lastFacesHeight);
            }
        };

        //
        // Do not fuck with this. Empty frame
        // layouts are not drawn at all!
        //

        maldat.setBackgroundColor(Color.TRANSPARENT);

        addView(maldat);
    }

    public void renderFrame(P2PAVFrame avFrame)
    {
        surface.renderFrame(avFrame);
    }

    public void requestRender()
    {
        surface.requestRender();
    }
}
