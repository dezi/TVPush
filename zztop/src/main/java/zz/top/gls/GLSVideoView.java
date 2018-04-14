package zz.top.gls;

import android.widget.FrameLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.os.Handler;

import com.google.android.gms.vision.face.Face;

import de.xavaro.android.pub.interfaces.pub.PUBSurface;

public class GLSVideoView extends FrameLayout implements PUBSurface
{
    private static final String LOGTAG = GLSVideoView.class.getSimpleName();

    private Handler handler = new Handler();
    private GLSSurfaceView surface;
    private FrameLayout maldat;
    private boolean malfaces;

    public GLSVideoView(Context context)
    {
        super(context);
        init(context);
    }

    public GLSVideoView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    private SparseArray<Face> lastFaces;
    private int lastFacesWidth;
    private int lastFacesHeight;

    private void init(Context context)
    {
        surface = new GLSSurfaceView(context);

        addView(surface);

        maldat = new FrameLayout(context)
        {
            @Override
            protected void onDraw(Canvas canvas)
            {
                super.onDraw(canvas);

                GLSFaceDetect.drawFaces(canvas, lastFaces, lastFacesWidth, lastFacesHeight);
            }
        };

        //
        // Do not fuck with this. Empty frame
        // layouts are not drawn at all!
        //

        maldat.setBackgroundColor(Color.TRANSPARENT);

        addView(maldat);
    }

    @Override
    public void setZoom(int zoom, int step)
    {
        surface.setZoom(zoom, step);
    }

    public void setFaceDetecion(boolean enabled)
    {
        if (enabled)
        {
            surface.renderer.setOnFacesDetectedListener(onFacesDetectedListener);
        }
        else
        {
            surface.renderer.setOnFacesDetectedListener(null);
        }
    }

    public void setFaceDetecionDraw(boolean enabled)
    {
        malfaces = enabled;
    }

    public void onFacesDetected(SparseArray<Face> faces, int imageWidth, int imageHeight)
    {
    }

    public void renderFrame(GLSFrame avFrame)
    {
        surface.renderFrame(avFrame);
    }

    private final GLSRenderer.OnFacesDetectedListener onFacesDetectedListener =
            new GLSRenderer.OnFacesDetectedListener()
    {
        @Override
        public void onFacesDetected(SparseArray<Face> faces, int imageWidth, int imageHeight)
        {
            lastFaces = faces;
            lastFacesWidth = imageWidth;
            lastFacesHeight = imageHeight;

            GLSVideoView.this.onFacesDetected(faces, imageWidth, imageHeight);

            if (malfaces)
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        maldat.invalidate();
                    }
                });
            }
        }
    };
}
