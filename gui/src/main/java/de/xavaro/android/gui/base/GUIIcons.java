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
        return getImageResid(iotobject, false);
    }

    public static int getImageResid(IOTObject iotobject, boolean bunt)
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

            if (device.type != null)
            {
                if (device.type.equals("phone")) return R.drawable.phone_225;
                if (device.type.equals("tablet")) return R.drawable.tablet_530;
                if (device.type.equals("tvremote")) return R.drawable.remote_550;

                if (device.type.equals("tv"))
                {
                    return bunt ? R.drawable.device_tv_bunt_100 :  R.drawable.device_tv_100;
                }

                if (device.type.equals("smartplug"))
                {
                    return bunt ? R.drawable.plug_bunt_400 : R.drawable.plug_400;
                }

                if (device.type.equals("smartbulb"))
                {
                    return bunt ? R.drawable.bulb_bunt_440 : R.drawable.bulb_440;
                }

                if (device.type.equals("camera"))
                {
                    if (device.model != null)
                    {
                        if (device.model.contains("Dome"))
                        {
                            return bunt ? R.drawable.domcam_bunt_500 : R.drawable.domcam_500;
                        }

                        if (device.model.contains("Outdoor"))
                        {
                            return bunt ? R.drawable.outcam_bunt_825 : R.drawable.outcam_825;
                        }
                    }

                    return bunt ? R.drawable.homecam_bunt_560 :  R.drawable.homecam_560;
                }

                if (device.type.equals("beacon"))
                {
                    if (device.model != null)
                    {
                        if (device.model.equals("iBKS Plus"))
                        {
                            return bunt ? R.drawable.beacon_ibks_plus_bunt_340 : R.drawable.beacon_ibks_plus_340;
                        }

                        if (device.model.contains("iBKS"))
                        {
                            return bunt ? R.drawable.beacon_ibks_105_bunt_240 : R.drawable.beacon_ibks_105_240;
                        }
                    }

                    return bunt ? R.drawable.beacon_bunt_220 : R.drawable.beacon_220;
                }
            }
        }

        return R.drawable.device_tv_100;
    }
}
