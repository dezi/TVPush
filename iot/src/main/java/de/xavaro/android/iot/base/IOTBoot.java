package de.xavaro.android.iot.base;

import android.util.Log;

import de.xavaro.android.iot.comm.IOTMessage;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import de.xavaro.android.iot.things.IOTDomains;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTHumans;

@SuppressWarnings("WeakerAccess")
public class IOTBoot extends IOTObject
{
    private final static String LOGTAG = IOTBoot.class.getSimpleName();

    @Override
    public String getUUIDKey()
    {
        //
        // This class is a singleton.
        //

        return "iot.root";
    }

    public static void initialize()
    {
        loadOwnIdentity();
    }

    private static void loadOwnIdentity()
    {
        Log.d(LOGTAG, "loadOwnIdentity: domains=" + IOTDomains.getCount());
        Log.d(LOGTAG, "loadOwnIdentity: devices=" + IOTDevices.getCount());
        Log.d(LOGTAG, "loadOwnIdentity: humans=" + IOTHumans.getCount());

        IOTBoot root = new IOTBoot();

        if (! root.loadFromStorage())
        {
            //
            // Create root UUID.
            //

            root.saveToStorage();
        }

        IOT.meme = new IOTMeme(root.uuid);

        if (! IOT.meme.loadFromStorage())
        {
            //
            // Create meme entry.
            //

            IOT.meme.saveToStorage();
        }

        Log.d(LOGTAG, "loadOwnIdentity: meme=" + IOT.meme.uuid);
        Log.d(LOGTAG, "loadOwnIdentity: memeHumanUUID=" + IOT.meme.memeHumanUUID);
        Log.d(LOGTAG, "loadOwnIdentity: memeDeviceUUID=" + IOT.meme.memeDeviceUUID);

        IOTHuman localHuman = IOTHuman.buildLocalHuman();
        IOTDevice localDevice = IOTDevice.buildLocalDevice();

        if ((IOT.meme.memeDeviceUUID == null) || IOT.meme.memeDeviceUUID.isEmpty())
        {
            //
            // Create device entry.
            //

            IOT.device = localDevice;

            Log.d(LOGTAG, "static: new device=" + IOT.device.uuid);

            IOT.meme.memeDeviceUUID = IOT.device.uuid;

            if (IOT.meme.saveToStorage())
            {
                IOT.device.saveToStorage();
            }
        }
        else
        {
            IOT.device = new IOTDevice(IOT.meme.memeDeviceUUID);
            IOT.device.checkAndMergeContent(localDevice, false);
        }

        if ((IOT.meme.memeHumanUUID == null) || IOT.meme.memeHumanUUID.isEmpty())
        {
            //
            // Only create if there are NO humans at all known.
            //

            if (IOTHumans.getCount() == 0)
            {
                IOT.human = localHuman;

                Log.d(LOGTAG, "static: new human=" + IOT.human.uuid);

                IOT.meme.memeHumanUUID = IOT.human.uuid;

                if (IOT.meme.saveToStorage())
                {
                    IOT.human.saveToStorage();
                }
            }
        }
        else
        {
            IOT.human = new IOTHuman(IOT.meme.memeHumanUUID);
            IOT.human.checkAndMergeContent(localHuman, false);
        }

        IOTMessage.sendHELO();
    }
}
