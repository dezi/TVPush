package de.xavaro.android.gui.base;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.gui.R;

public class GUIIcons
{
    public static int getImageResid(IOTObject iotobject)
    {
        if (iotobject instanceof IOTHuman)
        {
            return R.drawable.human_260;
        }

        if (iotobject instanceof IOTDomain)
        {
            return R.drawable.domain_250;
        }

        if (iotobject instanceof IOTLocation)
        {
            return R.drawable.room_200;
        }

        if (iotobject instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotobject;

            if (device.type.equals("tv")) return R.drawable.device_tv_100;
            if (device.type.equals("phone")) return R.drawable.phone_225;
            if (device.type.equals("tablet")) return R.drawable.tablet_530;
            if (device.type.equals("camera")) return R.drawable.domcam_500;
            if (device.type.equals("remote")) return R.drawable.remote_550;
            if (device.type.equals("beacon")) return R.drawable.beacon_220;
            if (device.type.equals("smartplug")) return R.drawable.plug_400;
            if (device.type.equals("smartbulb")) return R.drawable.bulb_440;
        }

        return R.drawable.device_tv_100;
    }
}
