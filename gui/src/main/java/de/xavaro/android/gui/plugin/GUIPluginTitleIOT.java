package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.view.View;

import de.xavaro.android.gui.base.GUIShort;
import de.xavaro.android.gui.views.GUIEditText;

import de.xavaro.android.gui.base.GUIDefs;

import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTLocation;

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

        setTitleIconIOTThing(uuid);

        IOTThing iotThing = IOTThing.getEntry(uuid);

        if (iotThing != null)
        {
            String hint = "Bitte Nicknamen hier eintragen";

            String toast = ""
                    + "Sprechen Sie jetzt die Nicknamen ein"
                    + " oder drücken Sie "
                    + GUIDefs.UTF_OK
                    + " zum Bearbeiten";

            setNickEdit(iotThing.nick, hint, toast);

            if ((iotThing instanceof IOTLocation) || (iotThing instanceof IOTDomain))
            {
                hint = "Bitte Namen hier eintragen";

                toast = ""
                        + "Sprechen Sie jetzt den Namen ein"
                        + " oder drücken Sie "
                        + GUIDefs.UTF_OK
                        + " zum Bearbeiten";

                setNameEdit(iotThing.name, hint, toast);
            }
            else
            {
                setNameInfo(iotThing.name);
            }
        }
    }

    @Override
    public void onNameEditFinished(View view)
    {
        GUIShort.saveName(uuid, ((GUIEditText) view).getText().toString());
    }

    @Override
    public void onNickEditFinished(View view)
    {
        GUIShort.saveNick(uuid, ((GUIEditText) view).getText().toString());
    }
}
