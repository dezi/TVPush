package de.xavaro.android.iot.things;

import android.util.Log;

import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class IOTRoot extends IOTBase
{
    private final static String LOGTAG = IOTRoot.class.getSimpleName();

    public static IOTRoot root;
    public static IOTMeme meme;
    public static IOTHuman human;
    public static IOTDevice device;

    @Override
    public String getUUIDKey()
    {
        //
        // This class is a singleton.
        //

        return "iot." + IOTRoot.class.getSimpleName();
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

        root = new IOTRoot();

        if (! root.loadFromStorage())
        {
            //
            // Create root UUID.
            //

            root.saveToStorage();
        }

        meme = new IOTMeme(root.uuid);

        if (! meme.loadFromStorage())
        {
            //
            // Create meme entry.
            //

            meme.saveToStorage();
        }

        Log.d(LOGTAG, "loadOwnIdentity: meme=" + meme.uuid);
        Log.d(LOGTAG, "loadOwnIdentity: memeHumanUUID=" + meme.memeHumanUUID);
        Log.d(LOGTAG, "loadOwnIdentity: memeDeviceUUID=" + meme.memeDeviceUUID);

        if ((meme.memeDeviceUUID == null) || meme.memeDeviceUUID.isEmpty())
        {
            //
            // Create device entry.
            //

            device = new IOTDevice();

            Log.d(LOGTAG, "static: new device=" + device.uuid);

            device.type = Simple.getDeviceType();
            device.nick = Simple.getDeviceUserName();
            device.name = Simple.getDeviceUserName();
            device.brand = Simple.getDeviceBrandName();
            device.model = Simple.getDeviceModelName();
            device.version =  Simple.getAndroidVersion();

            device.location = "@" + Simple.getConnectedWifiName();

            device.capabilities = Json.jsonArrayFromSeparatedString(Simple.getDeviceCapabilities(), "\\|");

            meme.memeDeviceUUID = device.uuid;

            if (meme.saveToStorage())
            {
                device.saveToStorage();
            }
        }
        else
        {
            device = new IOTDevice(meme.memeDeviceUUID);
        }

        if ((meme.memeHumanUUID == null) || meme.memeHumanUUID.isEmpty())
        {
            //
            // Only create if there are NO humans at all known.
            //

            if (IOTHumans.getCount() == 0)
            {
                human = new IOTHuman();

                Log.d(LOGTAG, "static: new human=" + human.uuid);

                human.nick = Simple.getDeviceUserName();
            }

            meme.memeHumanUUID = human.uuid;

            if (meme.saveToStorage())
            {
                human.saveToStorage();
            }
        }
        else
        {
            human = new IOTHuman(meme.memeHumanUUID);
        }
    }
}
