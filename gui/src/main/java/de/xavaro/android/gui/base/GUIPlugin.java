package de.xavaro.android.gui.base;

import android.widget.FrameLayout;
import android.content.Context;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public final GUIFrameLayout pluginFrame;

    public final FrameLayout.LayoutParams pluginFrameParams;

    public GUIPlugin(Context context)
    {
        super(context);

        //
        // Courtesy copy to make things more transparent.
        //

        pluginFrame = this;
        pluginFrameParams = params;

        onCreate();
    }

    public void onCreate()
    {
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

    public void setPosition(int left, int top)
    {
        pluginFrameParams.leftMargin = left;
        pluginFrameParams.topMargin = top;

        pluginFrame.setLayoutParams(pluginFrameParams);
    }

    public void setPositionDip(int left, int top)
    {
        pluginFrameParams.leftMargin = Simple.dipToPx(left);
        pluginFrameParams.topMargin = Simple.dipToPx(top);

        pluginFrame.setLayoutParams(pluginFrameParams);
    }

    public int getPluginWidth()
    {
        return pluginFrameParams.width;
    }

    public int getPluginWidthDip()
    {
        return Simple.pxToDip(pluginFrameParams.width);
    }

    public int getPluginHeight()
    {
        return pluginFrameParams.height;
    }

    public int getPluginHeightDip()
    {
        return Simple.pxToDip(pluginFrameParams.height);
    }
}
