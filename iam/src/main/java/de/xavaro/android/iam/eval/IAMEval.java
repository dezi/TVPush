package de.xavaro.android.iam.eval;

import android.support.annotation.Nullable;
import android.util.Log;

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
            evaluateDevtype(result);

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
                continue;
            }

            if (ifContainsRemove(device.nick))
            {
                Json.put(devices, uuid);
                continue;
            }
        }

        addDevices(result, devices);
    }

    private void evaluateDevtype(JSONObject result)
    {
        JSONArray objects = Json.getArray(result, "objects");
        if ((objects == null) || (objects.length() == 0)) return;

        String action = Json.getString(result, "action");
        if ((action == null) || action.isEmpty()) return;

        JSONArray devices = new JSONArray();

        for (int oinx = 0; oinx < objects.length(); oinx++)
        {
            JSONObject object = Json.getObject(objects, oinx);
            if (object == null) continue;

            String objname = Json.getString(object, "object");
            boolean plural = Json.getBoolean(object, "plural");

            if ((objname == null) || objname.isEmpty() || ! plural)
            {
                //
                // For device capability search we
                // like an explicit plural.
                //

                continue;
            }

            String capability = null;

            if (objname.equals("led") && ((action.equals("switchon") || action.equals("switchoff"))))
            {
                capability = "ledonoff";
            }

            if ((capability == null) || capability.isEmpty()) continue;

            //
            // Remove this object from result.
            // On elder APIs objects is a new copy,
            // because remove does not work.
            // So write back objects also.
            //

            objects = Json.removeFromArray(objects, oinx--);

            if (objects.length() == 0)
            {
                Json.remove(result, "objects");
            }
            else
            {
                Json.put(result, "objects", objects);
            }

            //
            // Scan for matching devices.
            //

            JSONArray list = IOTDevices.instance.getListUUIDs();

            for (int dinx = 0; dinx < list.length(); dinx++)
            {
                String uuid = Json.getString(list, dinx);
                IOTDevice device = IOTDevices.getEntry(uuid);
                if ((device == null) || (device.capabilities == null)) continue;

                for (int cinx = 0; cinx < device.capabilities.length(); cinx++)
                {
                    String devcap = Json.getString(device.capabilities, cinx);
                    if ((devcap == null) || devcap.isEmpty()) continue;

                    if (capability.equals(devcap))
                    {
                        Json.put(devices, uuid);
                    }
                }
            }
        }

        addDevices(result, devices);
    }

    private void addDevices(JSONObject result, JSONArray newdevices)
    {
        if ((newdevices == null) || (newdevices.length() == 0)) return;

        JSONArray olddevices = Json.getArray(result, "devices");

        if (olddevices == null)
        {
            olddevices = new JSONArray();
            Json.put(result, "devices", olddevices);
        }

        for (int ninx = 0; ninx < newdevices.length(); ninx++)
        {
            String newuuid = Json.getString(newdevices, ninx);
            if ((newuuid == null) || (newuuid.isEmpty())) continue;

            boolean dup = false;

            for (int oinx = 0; oinx < olddevices.length(); oinx++)
            {
                String olduuid = Json.getString(olddevices, ninx);
                if ((olduuid == null) || (olduuid.isEmpty())) continue;

                dup |= olduuid.equals(newuuid);
            }

            if (! dup) Json.put(olddevices, newuuid);
        }
    }

    private void evaluateObjects(JSONObject result)
    {
        JSONArray objects = new JSONArray();

        boolean plural = ifContainsRemove("alle");

        if (ifContainsRemove("App")
                || ifContainsRemove("Anwendung"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", plural);
            Json.put(object, "object", "application");
        }

        if (ifContainsRemove("Apps")
                || ifContainsRemove("Anwendungen"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", true);
            Json.put(object, "object", "application");
        }

        if (ifContainsRemove("LED"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", plural);
            Json.put(object, "object", "led");
        }

        if (ifContainsRemove("LEDs"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", true);
            Json.put(object, "object", "led");
        }

        if (ifContainsRemove("Kamera")
                || ifContainsRemove("Camera"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", plural);
            Json.put(object, "object", "camera");
        }

        if (ifContainsRemove("Kameras")
                || ifContainsRemove("Cameras"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", true);
            Json.put(object, "object", "camera");
        }

        if (ifContainsRemove("Spracherkennung")
                || ifContainsRemove("Erkennung"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", plural);
            Json.put(object, "object", "speechlistener");
        }

        if (ifContainsRemove("Spracherkennungen")
                || ifContainsRemove("Erkennungen"))
        {
            JSONObject object = new JSONObject();
            Json.put(objects, object);

            Json.put(object, "plural", true);
            Json.put(object, "object", "speechlistener");
        }

        if (objects.length() > 0)
        {
            Json.put(result, "objects", objects);
        }
    }

    private void evaluateCommand(JSONObject result)
    {
        if (ifContainsRemove("anschalten"))
        {
            Json.put(result, "action", "switchon");
            return;
        }

        if (ifContainsRemove("ausschalten"))
        {
            Json.put(result, "action", "switchoff");
            return;
        }

        if (ifContainsRemove("schließen")
                || ifContainsRemove("beenden")
                || ifContainsRemove("ausblenden"))
        {
            Json.put(result, "action", "close");
            return;
        }

        if (ifContainsRemove("auf das display")
                || ifContainsRemove("auf den schirm")
                || ifContainsRemove("auf dem schirm")
                || ifContainsRemove("öffnen")
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