package de.xavaro.android.iot;

import android.util.Log;

import org.json.JSONArray;

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

    static
    {
        loadOwnIdentity();
    }

    @Override
    public String getUUIDKey()
    {
        //
        // This class is a singleton.
        //

        return "iot." + IOTRoot.class.getSimpleName();
    }

    private static void loadOwnIdentity()
    {
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

        Log.d(LOGTAG, "static:"
                + " meme=" + meme.uuid
                + " memeHumanUUID=" + meme.memeHumanUUID
                + " memeDeviceUUID=" + meme.memeDeviceUUID);

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

        if ((meme.memeDeviceUUID == null) || meme.memeDeviceUUID.isEmpty())
        {
            //
            // Only create if there are NO humans at all known.
            //
        }
        else
        {
            human = new IOTHuman(meme.memeHumanUUID);
        }
    }

    private static void loadAllItems()
    {
        JSONArray list = IOTDevices.devices.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            if (uuid == null) continue;

            IOTDevice device = IOTDevices.devices.getDevice(uuid);

            Log.d(LOGTAG, "loadAllItems: IOTDevice:"
                    + " uuid=" + device.uuid
                    + " name=" + device.name);
        }
    }
}
