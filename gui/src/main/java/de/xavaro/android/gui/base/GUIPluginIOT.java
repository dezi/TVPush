package de.xavaro.android.gui.base;

import android.content.Context;

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

public class GUIPluginIOT extends GUIPlugin
{
    private final static String LOGTAG = GUIPluginIOT.class.getSimpleName();

    public IOTObject iotObject;

    public GUIPluginIOT(Context context)
    {
        super(context);
    }

    public void setIOTObject(IOTObject iotObject)
    {
        this.iotObject = iotObject;
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
}
