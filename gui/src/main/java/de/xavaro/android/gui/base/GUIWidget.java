package de.xavaro.android.gui.base;

import android.widget.FrameLayout;
import android.content.Context;

import de.xavaro.android.gui.views.GUIFrameLayout;

public class GUIWidget extends GUIFrameLayout
{
    private final static String LOGTAG = GUIWidget.class.getSimpleName();

    public FrameLayout widgetFrame;

    public GUIWidget(Context context)
    {
        super(context);

        widgetFrame = this;

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
