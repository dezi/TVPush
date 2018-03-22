package de.xavaro.android.gui.views;

import android.content.Context;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIListEntryIOT extends GUIListEntry
{
    public String uuid;
    public IOTDevice device;
    public IOTStatus status;
    public IOTCredential credential;

    public GUIListEntryIOT(Context context)
    {
        super(context);
    }

}
