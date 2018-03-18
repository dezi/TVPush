package de.xavaro.android.iot.base;

import android.util.Log;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import de.xavaro.android.iot.things.IOTLocations;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTHumans;

@SuppressWarnings("WeakerAccess")
public class IOTBoot extends IOTObject
{
    private final static String LOGTAG = IOTBoot.class.getSimpleName();

    public String bootHumanUUID;
    public String bootDeviceUUID;

    @Override
    public String getUUIDKey()
    {
        //
        // This class is a singleton.
        //

        return "iot." + getClass().getSimpleName();
    }

    public static void initialize()
    {
        loadOwnIdentity();
    }

    private static void loadOwnIdentity()
    {
        Log.d(LOGTAG, "loadOwnIdentity: domains=" + IOTLocations.getCount());
        Log.d(LOGTAG, "loadOwnIdentity: devices=" + IOTDevices.getCount());
        Log.d(LOGTAG, "loadOwnIdentity: humans=" + IOTHumans.getCount());

        IOT.boot = new IOTBoot();

        if (! IOT.boot.loadFromStorage())
        {
            //
            // Create root UUID.
            //

            IOT.boot.saveToStorage();
        }

        Log.d(LOGTAG, "loadOwnIdentity: bootHumanUUID=" + IOT.boot.bootHumanUUID);
        Log.d(LOGTAG, "loadOwnIdentity: bootDeviceUUID=" + IOT.boot.bootDeviceUUID);

        IOTHuman localHuman = IOTHuman.buildLocalHuman();
        IOTDevice localDevice = IOTDevice.buildLocalDevice();

        if ((IOT.boot.bootDeviceUUID == null) || IOT.boot.bootDeviceUUID.isEmpty())
        {
            //
            // Create device entry.
            //

            IOT.device = localDevice;

            Log.d(LOGTAG, "static: new device=" + IOT.device.uuid);

            IOT.boot.bootDeviceUUID = IOT.device.uuid;

            if (IOT.boot.saveToStorage())
            {
                IOT.device.saveToStorage();
            }
        }
        else
        {
            IOT.device = new IOTDevice(IOT.boot.bootDeviceUUID);
            IOT.device.checkAndMergeContent(localDevice, false);
        }

        if ((IOT.boot.bootHumanUUID == null) || IOT.boot.bootHumanUUID.isEmpty())
        {
            //
            // Only create if there are NO humans at all known.
            //

            if (IOTHumans.getCount() == 0)
            {
                IOT.human = localHuman;

                Log.d(LOGTAG, "static: new human=" + IOT.human.uuid);

                IOT.boot.bootHumanUUID = IOT.human.uuid;

                if (IOT.boot.saveToStorage())
                {
                    IOT.human.saveToStorage();
                }
            }
        }
        else
        {
            IOT.human = new IOTHuman(IOT.boot.bootHumanUUID);
            IOT.human.checkAndMergeContent(localHuman, false);
        }
    }
}
