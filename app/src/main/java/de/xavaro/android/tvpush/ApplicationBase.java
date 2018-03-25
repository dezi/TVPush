package de.xavaro.android.tvpush;

import android.util.Log;

import de.xavaro.android.adb.AdbServiceCheck;

import de.xavaro.android.adb.AdbServicePull;
import de.xavaro.android.adb.AdbTest;
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

        AdbTest.testShell(this, "192.168.0.11", 5555);

        //AdbServiceCheck adbServiceCheck = new AdbServiceCheck(this, "192.168.0.11", 5555);
        //adbServiceCheck.start();

        /*
        AdbServicePull adbServicePull = new AdbServicePull(this, "192.168.0.11", 5555)
        {
            protected String onGetRemoteFileName()
            {
                return "/storage/E06D-EF93/sdb.xml";
            }

            protected void onServiceSuccess()
            {

            }

            protected void onServiceFailed()
            {

            }
        };

        adbServicePull.start();
        */
    }
}
