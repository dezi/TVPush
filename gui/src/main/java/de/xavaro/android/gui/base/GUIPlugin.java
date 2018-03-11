package de.xavaro.android.gui.base;

import android.content.Context;

import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIPlugin extends GUIFrameLayout
{
    private final static String LOGTAG = GUIPlugin.class.getSimpleName();

    public GUIFrameLayout pluginFrame;

    public GUIPlugin(Context context)
    {
        super(context);

        //
        // Courtesy copy to make things more transparent.
        //

        pluginFrame = this;

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
}
