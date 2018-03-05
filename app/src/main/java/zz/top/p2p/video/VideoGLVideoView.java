package zz.top.p2p.video;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseArray;
import android.widget.FrameLayout;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

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

                drawFaces(canvas);
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

    private void drawFaces(Canvas canvas)
    {
        if (lastFaces == null) return;

        float scalex = canvas.getWidth() / (float) lastFacesWidth;
        float scaley = canvas.getHeight() / (float) lastFacesHeight;

        //Log.d(LOGTAG, "maldat: onDraw faces=" + lastFaces.size());

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

        for (int i = 0; i < lastFaces.size(); ++i)
        {
            Face face = lastFaces.valueAt(i);

            paint.setColor(Color.RED);

            int left = (int) (face.getPosition().x * scalex);
            int top = (int) (face.getPosition().y * scaley);
            int right = left + (int) (face.getWidth() * scalex);
            int bottom = top + (int) (face.getHeight() * scaley);

            int cx = (left + right) / 2;
            int cy = (top + bottom) / 2;

            canvas.drawCircle(cx, cy, 5, paint);

            paint.setColor(Color.GREEN);

            for (Landmark landmark : face.getLandmarks())
            {
                cx = (int) (landmark.getPosition().x * scalex);
                cy = (int) (landmark.getPosition().y * scaley);

                canvas.drawCircle(cx, cy, 5, paint);
            }
        }
    }
}
