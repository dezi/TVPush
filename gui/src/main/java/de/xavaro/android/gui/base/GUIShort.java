package de.xavaro.android.gui.base;

import android.support.annotation.Nullable;

import de.xavaro.android.gui.plugin.GUIPlugin;
import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.iot.things.IOTThing;

public class GUIShort
{
    @Nullable
    public static GUIPlugin getWizzard(Class wclass)
    {
        return GUI.instance.desktopActivity.getWizzard(wclass.getSimpleName());
    }

    public static boolean isWizzardPresent(Class wclass)
    {
        GUIPlugin wizzard = getWizzard(wclass);
        return (wizzard != null) && (wizzard.getParent() != null);
    }

    public static void saveName(String uuid, String newName)
    {
        IOTThing iotThing = IOTThing.getEntry(uuid);

        IOTThing saveme = null;

        if (iotThing instanceof IOTHuman) saveme = new IOTHuman(iotThing.uuid);
        if (iotThing instanceof IOTDevice) saveme = new IOTDevice(iotThing.uuid);
        if (iotThing instanceof IOTDomain) saveme = new IOTDomain(iotThing.uuid);
        if (iotThing instanceof IOTLocation) saveme = new IOTLocation(iotThing.uuid);

        if (saveme != null)
        {
            saveme.name = newName;

            saveIOTObject(saveme);
        }
    }

    public static void saveNick(String uuid, String newNick)
    {
        IOTThing iotThing = IOTThing.getEntry(uuid);

        IOTThing saveme = null;

        if (iotThing instanceof IOTHuman) saveme = new IOTHuman(iotThing.uuid);
        if (iotThing instanceof IOTDevice) saveme = new IOTDevice(iotThing.uuid);
        if (iotThing instanceof IOTDomain) saveme = new IOTDomain(iotThing.uuid);
        if (iotThing instanceof IOTLocation) saveme = new IOTLocation(iotThing.uuid);

        if (saveme != null)
        {
            saveme.nick = newNick;

            saveIOTObject(saveme);
        }
    }

    public static int saveIOTObject(IOTObject iotObjectChanged)
    {
        int saved = IOTDefs.IOT_SAVE_UNCHANGED;

        if (iotObjectChanged instanceof IOTHuman)
        {
            saved = IOTHuman.list.addEntry((IOTHuman) iotObjectChanged, true, true);
        }

        if (iotObjectChanged instanceof IOTDevice)
        {
            saved = IOTDevice.list.addEntry((IOTDevice) iotObjectChanged, true, true);
        }

        if (iotObjectChanged instanceof IOTDomain)
        {
            saved = IOTDomain.list.addEntry((IOTDomain) iotObjectChanged, true, true);
        }

        if (iotObjectChanged instanceof IOTLocation)
        {
            saved = IOTLocation.list.addEntry((IOTLocation) iotObjectChanged, true, true);
        }

        if (saved != IOTDefs.IOT_SAVE_UNCHANGED)
        {
            if (saved < 0)
            {
                String mess = "Speichern fehlgeschlagen";

                GUI.instance.desktopActivity.displayToastMessage(mess, 10, true);
            }
            else
            {
                String mess = "Gespeichert";

                GUI.instance.desktopActivity.displayToastMessage(mess, 3, true);
            }
        }

        return saved;
    }
}
