package de.xavaro.android.tvpush;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import de.xavaro.android.gui.base.GUIApplication;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.systems.Systems;

public class ApplicationBase extends GUIApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Simple.removeALLPrefs(this);

        Simple.initialize(this);

        Systems.initialize(this);
    }
}
