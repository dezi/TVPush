package de.xavaro.android.iam.eval;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.iam.simple.Json;

public class IAMEvalChannels
{
    @Nullable
    public static JSONObject isChannel(String word)
    {
        if (channels == null) return null;

        Iterator<String> uuids = channels.keys();

        while (uuids.hasNext())
        {
            String uuid = uuids.next();

            JSONArray list = Json.getArray(channels, uuid);
            if (list == null) continue;

            for (int inx = 0; inx < list.length(); inx++)
            {
                JSONObject channel = Json.getObject(list, inx);
                String name = Json.getString(channel, "name");
                String dial = Json.getString(channel, "dial");
                String type = Json.getString(channel, "type");

                if (name == null) continue;

                String match = name;
                if (match.endsWith(" HD")) match = match.substring(0, match.length() - 3);

                if (word.equalsIgnoreCase(match))
                {
                    JSONObject result = new JSONObject();

                    Json.put(result, "uuid", uuid);
                    Json.put(result, "name", name);
                    Json.put(result, "dial", dial);
                    Json.put(result, "type", type);

                    return result;
                }
            }
        }

        return null;
    }

    private static JSONObject channels;

    public static void setChannelsForDevices(JSONObject channels)
    {
        IAMEvalChannels.channels = channels;
    }
}
