package de.xavaro.android.gui.base;

import android.content.Context;
import android.util.Log;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public final static int DEFAULT_LEFT = 50;
    public final static int DEFAULT_TOP = 100;

    public final static int DEFAULT_WIDTH = 500;
    public final static int DEFAULT_HEIGHT = 300;

    public GUIFrameLayout contentFrame;

    private boolean isActive;
    private boolean isAttached;

    public GUIPlugin(Context context)
    {
        super(context);

        contentFrame = this;

        this.setPluginSizeDip(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setPluginPositionDip(DEFAULT_LEFT, DEFAULT_TOP);
        this.setPaddingDip(GUIDefs.PADDING_SMALL);

        onHighlightFrame(false);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        isAttached = false;
    }

    public void onStart()
    {
    }

    public void onResume()
    {
    }

    public void onPause()
    {
    }

    public void onStop()
    {
    }

    public void onBackPressed()
    {
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        Log.d(LOGTAG, "onRequestPermissionsResult: STUB!");
    }

    public void setPluginSizeDip(int width, int height)
    {
        setSizeDip(width, height);
    }

    public void setPluginPositionDip(int left, int top)
    {
        params.leftMargin = Simple.dipToPx(left);
        params.topMargin = Simple.dipToPx(top);

        setLayoutParams(params);
    }

    public int getPluginWidth()
    {
        return params.width;
    }

    public int getPluginHeight()
    {
        return params.height;
    }

    public int getPluginWidthDip()
    {
        return Simple.pxToDip(params.width);
    }

    public int getPluginHeightDip()
    {
        return Simple.pxToDip(params.height);
    }

    public int getPluginNettoWidth()
    {
        return params.width - (getPaddingLeft() + getPaddingRight());
    }

    public int getPluginNettoHeight()
    {
        return params.height - (getPaddingTop() + getPaddingBottom());
    }

    public void onHighlightFrame(boolean highlight)
    {
        isActive = highlight;

        if (highlight)
        {
            this.setRoundedCorners(GUIDefs.ROUNDED_NORMAL,
                    GUIDefs.COLOR_PLUGIN_INNER_TRANSPARENT,
                    GUIDefs.COLOR_PLUGIN_FRAME_HIGHLIGHT);
        }
        else
        {
            this.setRoundedCorners(GUIDefs.ROUNDED_NORMAL,
                    GUIDefs.COLOR_PLUGIN_INNER_TRANSPARENT,
                    GUIDefs.COLOR_PLUGIN_FRAME_NORMAL);
        }
    }

    public boolean isActive()
    {
        return isActive;
    }

    public boolean isAttached()
    {
        return isAttached;
    }
}
