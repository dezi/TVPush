package de.xavaro.android.gui.base;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public final static int DEFAULT_HORZ_MARGIN = 50;
    public final static int DEFAULT_VERT_MARGIN = 100;

    public final static int DEFAULT_WIDTH = 300;

    public GUIFrameLayout contentFrame;

    private boolean isHelper;
    private boolean isActive;
    private boolean isWizzard;
    private boolean isAttached;

    public GUIPlugin(Context context)
    {
        super(context);

        contentFrame = this;

        this.setPluginPositionDip(DEFAULT_HORZ_MARGIN, DEFAULT_VERT_MARGIN);

        this.setPluginSizeDip(DEFAULT_WIDTH,
                Simple.getDeviceHeightDip() - 2 * DEFAULT_VERT_MARGIN);

        this.setPaddingDip(GUIDefs.PADDING_SMALL);

        onHighlightFrame(false);
    }

    public void setIsWizzard(boolean helper)
    {
        isWizzard = true;
        isHelper = helper;

        this.setPluginPositionDip(DEFAULT_WIDTH + 2 * DEFAULT_HORZ_MARGIN, DEFAULT_VERT_MARGIN);

        int width = Simple.getDeviceWidthDip() - DEFAULT_WIDTH - 3 * DEFAULT_HORZ_MARGIN;
        int height = Simple.getDeviceHeightDip() - 2 * DEFAULT_VERT_MARGIN;

        this.setPluginSizeDip(width, height);
    }

    public boolean isWizzard()
    {
        return isWizzard;
    }

    public boolean isHelper()
    {
        return isHelper;
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

        if (Simple.isTV())
        {
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
        else
        {
            if (highlight)
            {
                this.setRoundedCorners(GUIDefs.ROUNDED_NORMAL,
                        Color.WHITE,
                        GUIDefs.COLOR_PLUGIN_FRAME_HIGHLIGHT);
            }
            else
            {
                this.setRoundedCorners(GUIDefs.ROUNDED_NORMAL,
                        Color.WHITE,
                        GUIDefs.COLOR_PLUGIN_FRAME_NORMAL);
            }
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
