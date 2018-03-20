package de.xavaro.android.gui.base;

import android.widget.FrameLayout;
import android.content.Context;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public final static int DEFAULT_WIDTH = 500;
    public final static int DEFAULT_HEIGTH = 300;

    public GUIFrameLayout contentFrame;

    public GUIPlugin(Context context)
    {
        super(context);

        contentFrame = this;
        contentFrame.setSizeDip(DEFAULT_WIDTH, DEFAULT_HEIGTH);
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
}
