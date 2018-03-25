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

    private AdbServicePull adbServicePull;

    @Override
    public void onCreate()
    {
        super.onCreate();

        // neeeeeeeeiiiiiiiiiinnnnn Simple.removeALLPrefs(this);

        Simple.initialize(this);

        Systems.initialize(this);

        //AdbTest.testShell(this, "192.168.0.11", 5555);

        //AdbServiceCheck adbServiceCheck = new AdbServiceCheck(this, "192.168.0.11", 5555);
        //Log.d(LOGTAG, "onCreate: adbServiceCheck:" + adbServiceCheck.startSync());


        adbServicePull = new AdbServicePull(
                this, "192.168.0.11", 5555,
                "/storage/E06D-EF93/sdb.xml")
        {
            @Override
            protected void onServiceSuccess()
            {
                Log.d(LOGTAG, "AdbServicePull: onServiceSuccess size=" + adbServicePull.outputStream.size());
            }

            @Override
            protected void onServiceFailed()
            {
                Log.d(LOGTAG, "AdbServicePull: onServiceFailed.");
            }
        };

        boolean success = adbServicePull.startSync();

        Log.d(LOGTAG, "onCreate: AdbServicePull: startsync: success=" + success);
        if (success ) Log.d(LOGTAG, "onCreate: AdbServicePull: startsync: size=" + adbServicePull.outputStream.size());
    }
}
