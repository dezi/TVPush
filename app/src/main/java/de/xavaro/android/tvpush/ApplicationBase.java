package de.xavaro.android.tvpush;

import android.util.Base64;
import android.util.Log;

import com.cgutman.adblib.AdbTest;

import java.net.Socket;
import java.util.List;

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

        // neeeeeeeeiiiiiiiiiinnnnn Simple.removeALLPrefs(this);

        Simple.initialize(this);

        Systems.initialize(this);

        AdbTest.main();
    }
}
