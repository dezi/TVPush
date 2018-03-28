package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.graphics.Color;

import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.things.IOTDevice;

import de.xavaro.android.iot.base.IOTAlive;

public class GUIPluginTitleListIOT extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIPluginTitleListIOT.class.getSimpleName();

    public GUIPluginTitleListIOT(Context context)
    {
        super(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }
}
