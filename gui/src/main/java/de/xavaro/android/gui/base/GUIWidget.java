package de.xavaro.android.gui.base;

import android.widget.FrameLayout;
import android.content.Context;

public class GUIWidget extends FrameLayout
{
    private final static String LOGTAG = GUIWidget.class.getSimpleName();

    public GUIWidget(Context context)
    {
        super(context);

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
}
