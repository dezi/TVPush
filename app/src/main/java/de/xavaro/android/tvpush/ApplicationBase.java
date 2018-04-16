package de.xavaro.android.tvpush;

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

        //AdbTest.testShell(this, "192.168.0.11", 5555);

        //AdbTest.testPullPush(this, "192.168.0.11", 5555);
    }
}
