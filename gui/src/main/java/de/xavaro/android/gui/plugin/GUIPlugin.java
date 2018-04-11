package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public static int DEFAULT_HORZ_MARGIN = 50;
    public static int DEFAULT_TOP_MARGIN = 50;;
    public static int DEFAULT_BOTTOM_MARGIN = Simple.isPhone()? 50 : 80;

    public GUIFrameLayout contentFrame;

    private boolean isHelper;
    private boolean isActive;
    private boolean isWizzard;
    private boolean isAttached;

    private int numcols;
    private int gravity;

    private int width;
    private int height;

    public GUIPlugin(Context context)
    {
        super(context);

        contentFrame = this;

        numcols = 1;
        gravity = Gravity.START;

        if (Simple.isTV())
        {
            width = (Simple.getDeviceWidthDip() - (4 * DEFAULT_HORZ_MARGIN)) / 3;
        }
        else
        {
            if (Simple.isTablet())
            {
                width = (Simple.getDeviceWidthDip() - (3 * DEFAULT_HORZ_MARGIN)) / 2;
            }
            else
            {
                width = Simple.getDeviceWidthDip() - (2 * DEFAULT_HORZ_MARGIN);
            }
        }

        height = Simple.getDeviceHeightDip() - DEFAULT_TOP_MARGIN - DEFAULT_BOTTOM_MARGIN;

        this.setPluginPositionDip(DEFAULT_HORZ_MARGIN, DEFAULT_TOP_MARGIN);

        this.setPluginSizeDip(width, height);

        this.setPaddingDip(GUIDefs.PADDING_SMALL);

        onHighlightFrame(false);
    }

    public void setIsWizzard(boolean wizzard, boolean helper)
    {
        setIsWizzard(wizzard, helper, 1, Gravity.START);
    }

    public void setIsWizzard(boolean wizzard, boolean helper, int numcols)
    {
        setIsWizzard(wizzard, helper, numcols, Gravity.START);
    }

    public void setIsWizzard(boolean wizzard, boolean helper, int numcols, int gravity)
    {
        isWizzard = wizzard;
        isHelper = helper;

        int newwidth = (numcols * width) + (numcols - 1) * DEFAULT_HORZ_MARGIN;

        this.setPluginSizeDip(newwidth, height);

        if (gravity == Gravity.CENTER)
        {
            int left = DEFAULT_HORZ_MARGIN + newwidth + DEFAULT_HORZ_MARGIN;
            this.setPluginPositionDip(left, DEFAULT_TOP_MARGIN);
        }

        if (gravity == Gravity.END)
        {
            int left = Simple.getDeviceWidthDip() - newwidth - DEFAULT_HORZ_MARGIN;
            this.setPluginPositionDip(left, DEFAULT_TOP_MARGIN);
        }
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

    public boolean onBackPressed()
    {
        return false;
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
