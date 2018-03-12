package de.xavaro.android.iam.eval;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.iam.simple.Json;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class IAMEval
{
    private final static String LOGTAG = IAMEval.class.getSimpleName();

    public JSONObject speech;

    public String message;
    public String lastwrd;

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
    }

    private JSONArray evaluateSpeech()
    {
        if ((message != null) && ! message.isEmpty())
        {
            String action = evaluateAction();

            if ((action != null) && ! action.isEmpty())
            {
                JSONArray devices = evaluateDevices(action);
                JSONArray objects = evaluateObjects(action);

                JSONArray results = new JSONArray();

                if (devices != null)
                {
                    for (int dinx = 0; dinx < devices.length(); dinx++)
                    {
                        Json.put(results, Json.getObject(devices, dinx));
                    }
                }

                if (objects != null)
                {
                    for (int dinx = 0; dinx < objects.length(); dinx++)
                    {
                        Json.put(results, Json.getObject(objects, dinx));
                    }
                }

                return (results.length() > 0) ? results : null;
            }
        }

        return null;
    }

    private JSONArray evaluateObjects(String action)
    {
        JSONArray objects = new JSONArray();

        boolean plural = ifContainsRemove("alle");

        boolean suchwas = true;

        while (suchwas)
        {
            suchwas = false;

            if (ifContainsRemove("normale Beleuchtung")
                    || ifContainsRemove("Puff Beleuchtung")
                    || ifContainsRemove("Bordell Beleuchtung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "bulb");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("App")
                    || ifContainsRemove("Anwendung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "application");
                Json.put(object, "target", lastwrd);
                suchwas = true;
            }

            if (ifContainsRemove("Apps")
                    || ifContainsRemove("Anwendungen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "application");
                Json.put(object, "target", lastwrd);
                suchwas = true;
            }

            if (ifContainsRemove("LED"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "led");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("LEDs"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "led");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Steckdose"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "plug");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Steckdosen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "plug");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Licht")
                    || ifContainsRemove("Lampe"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "bulb");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Lichter")
                    || ifContainsRemove("Lampen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "bulb");
                Json.put(object, "target", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Kamera")
                    || ifContainsRemove("Camera"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "camera");
                Json.put(object, "target", lastwrd);
                suchwas = true;
            }

            if (ifContainsRemove("Kameras")
                    || ifContainsRemove("Cameras"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "camera");
                Json.put(object, "target", lastwrd);
                suchwas = true;
            }

            if (ifContainsRemove("Spracheingabe")
                    || ifContainsRemove("Spracherkennung")
                    || ifContainsRemove("Erkennung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "speechlistener");
                Json.put(object, "target", lastwrd);
                suchwas = true;
            }

            if (ifContainsRemove("Spracheingaben")
                    || ifContainsRemove("Spracherkennungen")
                    || ifContainsRemove("Erkennungen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "plural", true);
                Json.put(object, "object", "speechlistener");
                Json.put(object, "target", lastwrd);
                suchwas = true;
            }
        }

        return objects;
    }

    private void getMatchingDevices(JSONObject object)
    {
        String action = Json.getString(object, "action");
        String objname = Json.getString(object, "object");
        boolean plural = Json.getBoolean(object, "plural");

        if ((action == null) || action.isEmpty()
                || (objname == null) || objname.isEmpty()
                || ! plural)
        {
            //
            // For device capability search we
            // like an explicit plural.
            //

            return;
        }

        String newaction = action;
        String capability = null;

        if (objname.equals("led") && ((action.equals("switchon") || action.equals("switchoff"))))
        {
            newaction = action.equals("switchon") ? "switchonled" : "switchoffled";
            capability = "ledonoff";
        }

        if (objname.equals("plug") && ((action.equals("switchon") || action.equals("switchoff"))))
        {
            newaction = action.equals("switchon") ? "switchonplug" : "switchoffplug";
            capability = "plugonoff";
        }

        if (objname.equals("bulb") && ((action.equals("switchon") || action.equals("switchoff"))))
        {
            newaction = action.equals("switchon") ? "switchonbulb" : "switchoffbulb";
            capability = "bulbonoff";
        }

        if (objname.equals("bulb") && ((action.equals("adjustpos") || action.equals("adjustneg"))))
        {
            capability = "dimmable";
        }

        if (objname.equals("bulb") && (action.startsWith("color.")))
        {
            capability = "colorhsb";
        }

        if ((capability == null) || capability.isEmpty()) return;

        Json.put(object, "action", newaction);

        //
        // Scan for matching devices.
        //

        JSONArray devices = new JSONArray();

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

        if (devices.length() > 0)
        {
            Json.put(object, "devices", devices);
        }
    }

    private JSONArray evaluateDevices(String action)
    {
        JSONObject names = new JSONObject();

        JSONArray list = IOTDevices.instance.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevices.getEntry(uuid);

            if (ifContains(device.name))
            {
                JSONArray dlist = Json.getArray(names, device.name);
                if (dlist == null)
                {
                    dlist = new JSONArray();
                    Json.put(names, device.name, dlist);
                }

                Json.put(dlist, uuid);
            }
        }

        //
        // Iterate over names found.
        //

        JSONArray devices = new JSONArray();

        Iterator<String> keys = names.keys();

        while (keys.hasNext())
        {
            String name = keys.next();
            JSONArray dlist = Json.getArray(names, name);
            if ((dlist == null) || (dlist.length() == 0)) continue;

            JSONObject device = new JSONObject();
            Json.put(devices, device);

            Json.put(device, "action", action);
            Json.put(device, "target", name);
            Json.put(device, "devices", dlist);

            ifContainsRemove(name);
        }

        return devices;
    }

    private String evaluateAction()
    {
        if (ifContains("Puff Beleuchtung")
                || ifContains("Bordell Beleuchtung"))
        {
            return "color." + Integer.toHexString(Color.RED).substring(2);
        }

        if (ifContains("normale Beleuchtung"))
        {
            return "color." + Integer.toHexString(Color.WHITE).substring(2);
        }

        if (ifContainsRemove("dunkler machen")
                || ifContainsRemove("dimmen"))
        {
            return "adjustneg";
        }

        if (ifContainsRemove("heller machen"))
        {
            return "adjustpos";
        }

        if (ifContainsRemove("anschalten")
                || ifContainsRemove("einschalten"))
        {
            return "switchon";
        }

        if (ifContainsRemove("abschalten")
                || ifContainsRemove("ausschalten") )
        {
            return "switchoff";
        }

        if (ifContainsRemove("schließen")
                || ifContainsRemove("beenden")
                || ifContainsRemove("ausblenden"))
        {
            return "close";
        }

        if (ifContainsRemove("auf das display")
                || ifContainsRemove("auf den schirm")
                || ifContainsRemove("auf dem schirm")
                || ifContainsRemove("öffnen")
                || ifContainsRemove("anzeigen")
                || ifContainsRemove("einblenden"))
        {
            return "open";
        }

        if (ifContainsRemove("anwählen")
                || ifContainsRemove("auswählen")
                || ifContainsRemove("aktivieren"))
        {
            return "select";
        }

        if (ifContainsRemove("bewegen")
                || ifContainsRemove("verschieben"))
        {
            return "move";
        }

        if (ifContainsRemove("löschen"))
        {
            return "delete";
        }

        return null;
    }

    private boolean ifContains(String target)
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
                lastwrd = target;

                return true;
            }

            pos = locmess.toLowerCase().indexOf(loctarg.toLowerCase());

            if (pos >= 0)
            {
                lastwrd = target;

                return true;
            }
        }

        return false;
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
                message = trim(locmess.substring(0, pos) + " " + locmess.substring(pos + len));
                lastwrd = target;

                return true;
            }

            pos = locmess.toLowerCase().indexOf(loctarg.toLowerCase());

            if (pos >= 0)
            {
                message = trim(locmess.substring(0, pos) + locmess.substring(pos + len));
                lastwrd = target;

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