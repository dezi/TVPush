package de.xavaro.android.iot.base;

import android.util.Log;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.iot.things.IOTHuman;

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
        Log.d(LOGTAG, "loadOwnIdentity: domains=" + IOTLocation.list.getCount());
        Log.d(LOGTAG, "loadOwnIdentity: devices=" + IOTDevice.list.getCount());
        Log.d(LOGTAG, "loadOwnIdentity: humans=" + IOTHuman.list.getCount());

        IOT.boot = new IOTBoot();

        if (! IOT.boot.loadFromStorage())
        {
            //
            // Create root UUID.
            //

            IOT.boot.saveToStorage(false);
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

            if (IOT.boot.saveToStorage(false))
            {
                IOT.device.saveToStorage(false);
            }
        }
        else
        {
            //
            // Just in case the bootstrapping buildLocalDevice was changed.
            //

            IOT.device = new IOTDevice(IOT.boot.bootDeviceUUID);
            IOT.device.checkAndMergeContent(localDevice, false, false);
        }

        if ((IOT.boot.bootHumanUUID == null) || IOT.boot.bootHumanUUID.isEmpty())
        {
            //
            // Only create if there are NO humans at all known.
            //

            if (IOTHuman.list.getCount() == 0)
            {
                IOT.human = localHuman;

                Log.d(LOGTAG, "static: new human=" + IOT.human.uuid);

                IOT.boot.bootHumanUUID = IOT.human.uuid;

                if (IOT.boot.saveToStorage(false))
                {
                    IOT.human.saveToStorage(false);
                }
            }
        }
        else
        {
            //
            // Just in case the bootstrapping buildLocalHuman was changed.
            //

            IOT.human = new IOTHuman(IOT.boot.bootHumanUUID);
            IOT.human.checkAndMergeContent(localHuman, false, false);
        }
    }
}
