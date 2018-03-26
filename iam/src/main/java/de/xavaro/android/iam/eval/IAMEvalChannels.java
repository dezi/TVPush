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

            if (word.equalsIgnoreCase("I")) word = "das Erste";
            if (word.equalsIgnoreCase("ARD")) word = "das Erste";

            if (word.equalsIgnoreCase("das zweite")) word = "ZDF";

            if (word.equalsIgnoreCase("NTV")) word = "n-tv";

            if (word.equalsIgnoreCase("RTL2")) word = "RTL II";

            if (word.equalsIgnoreCase("Sat 1")) word = "Sat.1";
            if (word.equalsIgnoreCase("SAT 1 Gold")) word = "SAT.1 Gold";

            if (word.equalsIgnoreCase("NDR")) word = "NDR FS SH";
            if (word.equalsIgnoreCase("SWR")) word = "SWR RP";

            if (word.equalsIgnoreCase("MDR")) word = "MDR Sachsen";
            if (word.equalsIgnoreCase("Ostzone")) word = "MDR Sachsen";

            if (word.equalsIgnoreCase("rbb")) word = "rbb Berlin";
            if (word.equalsIgnoreCase("Berlin")) word = "rbb Berlin";

            if (word.equalsIgnoreCase("hr")) word = "hr-fernsehen";
            if (word.equalsIgnoreCase("Hessen")) word = "hr-fernsehen";

            if (word.equalsIgnoreCase("BR")) word = "BR Fernsehen Süd";
            if (word.equalsIgnoreCase("Bayern")) word = "BR Fernsehen Süd";

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
