package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.view.View;

import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.views.GUIEditText;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTLocation;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTThing;

public class GUIPluginTitleIOT extends GUIPluginTitle
{
    private final static String LOGTAG = GUIPluginTitleIOT.class.getSimpleName();

    public String uuid;

    public GUIPluginTitleIOT(Context context)
    {
        super(context);
    }

    public void setIOTObject(String uuid)
    {
        this.uuid = uuid;

        titleIcon.setIOTThing(uuid);

        String hint = "Bitte Nicknamen hier eintragen";

        String toast = ""
                + "Sprechen Sie jetzt die Nicknamen ein"
                + " oder drücken Sie "
                + GUIDefs.UTF_OK
                + " zum Bearbeiten";

        IOTThing iotThing = IOTThing.getEntry(uuid);

        if (iotThing != null)
        {
            setTitleText(iotThing.name);
            setTitleEdit(iotThing.nick, hint, toast);
        }
    }

    public int saveIOTObject(IOTObject iotObjectChanged)
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

    private void saveNick(String newNick)
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

    @Override
    public void onTitleEditFinished(View view)
    {
        saveNick(((GUIEditText) view).getText().toString());
    }
}
