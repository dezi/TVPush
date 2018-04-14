package de.xavaro.android.systems;

import android.support.annotation.Nullable;

import android.app.Application;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.pub.interfaces.all.SubSystemHandler;

import de.xavaro.android.gui.base.GUI;

public class Systems
{
    private static final String LOGTAG = Systems.class.getSimpleName();

    private static Map<String, SubSystemHandler> subSystems = new HashMap<>();

    public static void initialize(Application application)
    {
        startSubsystem(application, "gui");
        startSubsystem(application, "iot");

        startSubsystem(application, "spr");
        startSubsystem(application, "iam");
        startSubsystem(application, "bcn");
        startSubsystem(application, "adb");

        startSubsystem(application, "sny");
        startSubsystem(application, "tpl");
        startSubsystem(application, "brl");
        startSubsystem(application, "edx");
        startSubsystem(application, "p2p");
    }

    @Nullable
    private static SubSystemHandler createSubsystem(Application application, String drv)
    {
        switch (drv)
        {
            case "gui" : return new SystemsGUI(application);
            case "iot" : return new SystemsIOT(application);

            case "spr" : return new SystemsSPR(application);
            case "iam" : return new SystemsIAM(application);
            case "bcn" : return new SystemsBCN(application);
            case "adb" : return new SystemsADB(application);

            case "sny" : return new SystemsSNY(application);
            case "tpl" : return new SystemsTPL(application);
            case "brl" : return new SystemsBRL(application);
            case "edx" : return new SystemsEDX(application);
            case "p2p" : return new SystemsP2P(application);
        }

        return null;
    }

    public static void startSubsystem(Application application, String subsystem)
    {
        String[] parts = subsystem.split("\\.");
        String drv = parts[ 0 ];

        SubSystemHandler sub = null;

        try
        {
            sub = subSystems.get(drv);
        }
        catch (Exception ignore)
        {
        }

        if (sub == null)
        {
            //
            // Completely new subsystem.
            //

            sub = createSubsystem(application, drv);
        }

        if (sub == null)
        {
            Log.e(LOGTAG, "startSubsystem: UNKNOWN subsystem=" + subsystem);

            return;
        }

        JSONObject infos = sub.getSubsystemInfo();
        int mode = Json.getInt(infos, "mode");

        if (mode == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
        {
            //
            // Important to bootstrap system.
            //

            sub.setInstance();

            GUI.instance.subSystems.registerSubsystem(infos);

            sub.startSubsystem(subsystem);
        }
        else
        {
            GUI.instance.subSystems.registerSubsystem(infos);

            if (GUI.instance.subSystems.isSubsystemActivated(drv))
            {
                sub.setInstance();
                sub.startSubsystem(subsystem);
            }
        }

        subSystems.put(drv, sub);
    }

    public static void stopSubsystem(String subsystem)
    {
        String[] parts = subsystem.split("\\.");
        String drv = parts[ 0 ];

        SubSystemHandler sub = null;

        try
        {
            sub = subSystems.get(drv);
        }
        catch (Exception ignore)
        {
        }

        if (sub == null)
        {
            Log.e(LOGTAG, "stopSubsystem: UNKNOWN subsystem=" + subsystem);
            return;
        }

        sub.stopSubsystem(subsystem);

        if (drv.equals(subsystem))
        {
            //
            // Completely unload subsystem.
            //

            subSystems.remove(drv);
        }
    }

    public static JSONObject getSubsystemSettings(Application application, String subsystem)
    {
        String[] parts = subsystem.split("\\.");
        String drv = parts[0];

        SubSystemHandler sub = null;

        try
        {
            sub = subSystems.get(drv);
        }
        catch (Exception ignore)
        {
        }

        if (sub == null)
        {
            //
            // Completely new subsystem for infos.
            // Will be released soon after again.
            //

            sub = createSubsystem(application, drv);
        }

        if (sub == null)
        {
            Log.e(LOGTAG, "stopSubsystem: UNKNOWN subsystem=" + subsystem);
            return null;
        }

        return sub.getSubsystemSettings();
    }
}
