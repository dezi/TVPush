package de.xavaro.android.iam.eval;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iam.simple.Json;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class IAMEval
{
    private final static String LOGTAG = IAMEval.class.getSimpleName();

    public String message;

    public JSONObject speech;
    public JSONArray results;

    @Nullable
    public static JSONArray evaluateSpeech(JSONObject speech)
    {
        IAMEval eval = new IAMEval(speech);
        return eval.evaluateSpeech();
    }

    public IAMEval(JSONObject speech)
    {
        this.speech = speech;

        JSONArray results = Json.getArray(speech, "results");

        if ((results != null) && (results.length() != 0))
        {
            JSONObject result = Json.getObject(results, 0);
            message = Json.getString(result, "text");
        }

        this.results = new JSONArray();
    }

    private JSONArray evaluateSpeech()
    {
        if ((message != null) && ! message.isEmpty())
        {
            JSONObject result = new JSONObject();

            evaluateDevices(result);
            evaluateObjects(result);
            evaluateCommand(result);

            if (Json.has(result, "action"))
            {
                Json.put(results, result);

                return results;
            }
        }

        return null;
    }

    private void evaluateDevices(JSONObject result)
    {
        JSONArray list = IOTDevices.instance.getListUUIDs();

        JSONArray devices = new JSONArray();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevices.getEntry(uuid);

            if (ifContainsRemove(device.name))
            {
                Json.put(devices, uuid);
            }

            if (ifContainsRemove(device.nick))
            {
                Json.put(devices, uuid);
            }
        }

        if (devices.length() > 0)
        {
            Json.put(result, "devices", devices);
        }
    }

    private void evaluateObjects(JSONObject result)
    {
        JSONArray objects = new JSONArray();

        if (ifContainsRemove("App")
                || ifContainsRemove("Anwendung"))
        {
            Json.put(result, "object", "application");
            return;
        }

        if (ifContainsRemove("Kamera")
                || ifContainsRemove("Camera"))
        {
            Json.put(result, "object", "camera");
            return;
        }

        if (ifContainsRemove("Spracherkennung")
                || ifContainsRemove("Erkennung"))
        {
            Json.put(result, "object", "speechlistener");
            return;
        }

        if (objects.length() > 0)
        {
            Json.put(result, "objects", objects);
        }
    }

    private void evaluateCommand(JSONObject result)
    {
        if (ifContainsRemove("schließen")
                || ifContainsRemove("ausblenden"))
        {
            Json.put(result, "action", "close");
            return;
        }

        if (ifContainsRemove("öffnen")
                || ifContainsRemove("anzeigen")
                || ifContainsRemove("einblenden"))
        {
            Json.put(result, "action", "open");
            return;
        }

        if (ifContainsRemove("anwählen")
                || ifContainsRemove("auswählen")
                || ifContainsRemove("aktivieren"))
        {
            Json.put(result, "action", "select");
            return;
        }

        if (ifContainsRemove("bewegen")
                || ifContainsRemove("verschieben"))
        {
            Json.put(result, "action", "move");
            return;
        }
    }

    private boolean ifContainsRemove(String target)
    {
        if ((target != null) && ! target.isEmpty())
        {
            String locmess = " " + message + " ";
            String loctarg = " " + target + " ";

            int len = target.length();
            int pos;

            pos = locmess.indexOf(loctarg);

            if (pos >= 0)
            {
                message = trim(locmess.substring(0, pos) + locmess.substring(pos + len));

                return true;
            }

            pos = locmess.toLowerCase().indexOf(loctarg.toLowerCase());

            if (pos >= 0)
            {
                message = trim(locmess.substring(0, pos) + locmess.substring(pos + len));

                return true;
            }
        }

        return false;
    }

    private String trim(String str)
    {
        str = str.trim();
        str = str.replace("  ", " ");

        return str;
    }
}