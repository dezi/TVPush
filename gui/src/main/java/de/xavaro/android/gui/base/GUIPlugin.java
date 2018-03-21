package de.xavaro.android.gui.base;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public final static int DEFAULT_LEFT = 50;
    public final static int DEFAULT_TOP = 100;

    public final static int DEFAULT_WIDTH = 500;
    public final static int DEFAULT_HEIGTH = 300;

    public GUIFrameLayout contentFrame;

    private boolean isActive;

    public GUIPlugin(Context context)
    {
        super(context);

        contentFrame = this;

        this.setPluginSizeDip(DEFAULT_WIDTH, DEFAULT_HEIGTH);
        this.setPluginPositionDip(DEFAULT_LEFT, DEFAULT_TOP);
        this.setPaddingDip(GUIDefs.PADDING_SMALL, GUIDefs.PADDING_ZERO, GUIDefs.PADDING_SMALL, GUIDefs.PADDING_SMALL);

        onHighlightFrame(false);
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

    public int getPluginWidthDip()
    {
        return Simple.pxToDip(params.width);
    }

    public int getPluginHeight()
    {
        return params.height;
    }

    public int getPluginHeightDip()
    {
        return Simple.pxToDip(params.height);
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
}
