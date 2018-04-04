package de.xavaro.android.tvpush;

import android.content.ComponentName;
import android.content.Intent;

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

        try
        {

            /*
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.google.android.voicesearch",
                    "com.google.android.voicesearch.VoiceSearchPreferences"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            */

            /*
            final Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                    "com.google.android.voicesearch.VoiceSearchPreferences"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            */

            /*
            final Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                    "com.google.android.apps.gsa.settingsui.VoiceSearchPreferences"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            */
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
