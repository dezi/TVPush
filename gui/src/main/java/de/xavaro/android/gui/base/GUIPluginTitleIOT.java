package de.xavaro.android.gui.base;

import android.content.Context;
import android.view.View;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.views.GUIEditText;
import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTDomains;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTHumans;
import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.iot.things.IOTLocations;

public class GUIPluginTitleIOT extends GUIPluginTitle
{
    private final static String LOGTAG = GUIPluginTitleIOT.class.getSimpleName();

    public IOTObject iotObject;

    public GUIPluginTitleIOT(Context context)
    {
        super(context);
    }

    public void setIOTObject(IOTObject iotObject)
    {
        this.iotObject = iotObject;

        if (iotObject instanceof IOTDevice)
        {
            String hint = "Bitte Nicknamen hier eintragen";

            String toast = "Sprechen Sie jetzt die Nicknamen ein"
                    + " oder drücken Sie "
                    + GUIDefs.UTF_OK
                    + " zum Bearbeiten";

            setTitleIcon(R.drawable.device_tv_100);
            setTitleText(((IOTDevice) iotObject).name);
            setTitleEdit(((IOTDevice) iotObject).nick, hint, toast);
        }
    }

    public int saveIOTObject(IOTObject iotObjectChanged)
    {
        int saved = IOTDefs.IOT_SAVE_UNCHANGED;

        if (iotObjectChanged instanceof IOTHuman)
        {
            saved = IOTHumans.addEntry((IOTHuman) iotObjectChanged, true);
        }

        if (iotObjectChanged instanceof IOTDevice)
        {
            saved = IOTDevices.addEntry((IOTDevice) iotObjectChanged, true);
        }

        if (iotObjectChanged instanceof IOTDomain)
        {
            saved = IOTDomains.addEntry((IOTDomain) iotObjectChanged, true);
        }

        if (iotObjectChanged instanceof IOTLocation)
        {
            saved = IOTLocations.addEntry((IOTLocation) iotObjectChanged, true);
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

    private int saveNick(String newNick)
    {
        if (iotObject instanceof IOTDevice)
        {
            IOTDevice saveme = new IOTDevice(iotObject.uuid);

            saveme.nick = newNick;

            return saveIOTObject(saveme);
        }

        return IOTDefs.IOT_SAVE_FAILED;
    }

    @Override
    public void onTitleEditFinished(View view)
    {
        saveNick(((GUIEditText) view).getText().toString());
    }
}
