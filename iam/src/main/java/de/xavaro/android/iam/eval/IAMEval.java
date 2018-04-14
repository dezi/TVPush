package de.xavaro.android.iam.eval;

import android.support.annotation.Nullable;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iam.simple.Json;

public class IAMEval
{
    private final static String LOGTAG = IAMEval.class.getSimpleName();

    private JSONArray lastResult;

    private String message;
    private String lastwrd;

    @Nullable
    public static JSONArray evaluateSpeech(JSONObject speech)
    {
        boolean partial = Json.getBoolean(speech, "partial");
        if (partial) return null;

        IAMEval eval = new IAMEval(speech);
        return eval.evaluateSpeech();
    }

    private IAMEval(JSONObject speech)
    {
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
            //
            // Small but very nice.
            //

            if (message.equals("noch mal"))
            {
                return lastResult;
            }

            String action = evaluateAction();

            if (action == null)
            {
                action = evaluateInstrinsic();
            }

            if ((action != null) && ! action.isEmpty())
            {
                String actionWords = lastwrd;

                JSONArray devices = evaluateDevices(action, actionWords);
                JSONArray objects = evaluateObjects(action, actionWords);

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

                lastResult = (results.length() > 0) ? results : null;

                return lastResult;
            }
            else
            {
                //
                // Dezi's channel hack.
                //

                JSONObject result = IAMEvalChannels.isChannel(message);

                if (result != null)
                {
                    String dial = Json.getString(result, "dial");

                    JSONObject object = new JSONObject();

                    Json.put(object, "action", "select");
                    Json.put(object, "actionData", dial);
                    Json.put(object, "actionWords", message);
                    Json.put(object, "plural", true);
                    Json.put(object, "object", "tvremote");
                    Json.put(object, "objectWords", message);

                    getMatchingDevices(object);

                    JSONArray results = new JSONArray();
                    Json.put(results, object);

                    return results;
                }

                //
                // Dezi's color hack.
                //

                if (IAMEvalColors.isColor(message))
                {
                    int colorrgb = IAMEvalColors.getColor(message);

                    JSONObject object = new JSONObject();

                    Json.put(object, "action", "color");
                    Json.put(object, "actionData", Integer.toHexString(colorrgb));
                    Json.put(object, "actionWords", message);
                    Json.put(object, "plural", true);
                    Json.put(object, "object", "bulb");
                    Json.put(object, "objectWords", message);

                    getMatchingDevices(object);

                    JSONArray results = new JSONArray();
                    Json.put(results, object);

                    return results;
                }
            }
        }

        return null;
    }

    private JSONArray evaluateObjects(String action, String actionWords)
    {
        JSONArray objects = new JSONArray();

        boolean plural = ifContainsRemove("alle");

        boolean suchwas = true;

        while (suchwas)
        {
            suchwas = false;

            if (ifContainsRemove("Streetview")
                    || ifContainsRemove("Street View"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "streetview");
                Json.put(object, "objectWords", message);
                suchwas = false;
            }

            if (ifContainsRemove("Wizard")
                    || ifContainsRemove("Wizzard"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "wizzard");
                Json.put(object, "objectWords", message);
                suchwas = false;
            }

            if (ifIsPincodeRemove()
                    || ifContainsRemove("PIN Code")
                    || ifContainsRemove("PIN-Code"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "plural", true);
                Json.put(object, "action", action);
                Json.put(object, "actionData", actionWords);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "object", "pincode");
                Json.put(object, "objectWords", message);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("normale Beleuchtung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionData", "fffffe");
                Json.put(object, "actionWords", actionWords);

                Json.put(object, "plural", true);
                Json.put(object, "object", "bulb");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Puff Beleuchtung")
                    || ifContainsRemove("Bordell Beleuchtung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionData", "ff0000");
                Json.put(object, "actionWords", actionWords);

                Json.put(object, "plural", true);
                Json.put(object, "object", "bulb");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("App")
                    || ifContainsRemove("Anwendung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "application");
                Json.put(object, "objectWords", lastwrd);

                suchwas = true;
            }

            if (ifContainsRemove("Apps")
                    || ifContainsRemove("Anwendungen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", true);
                Json.put(object, "object", "application");
                Json.put(object, "objectWords", lastwrd);

                suchwas = true;
            }

            if (ifContainsRemove("LED"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "led");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("LEDs"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", true);
                Json.put(object, "object", "led");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Steckdose"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "plug");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Steckdosen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", true);
                Json.put(object, "object", "plug");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Lampe"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "bulb");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Licht")
                    || ifContainsRemove("Lichter")
                    || ifContainsRemove("Lampen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", true);
                Json.put(object, "object", "bulb");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Kamera")
                    || ifContainsRemove("Camera"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "camera");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Kameras")
                    || ifContainsRemove("Cameras"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", true);
                Json.put(object, "object", "camera");
                Json.put(object, "objectWords", lastwrd);

                getMatchingDevices(object);

                suchwas = true;
            }

            if (ifContainsRemove("Spracheingabe")
                    || ifContainsRemove("Spracherkennung")
                    || ifContainsRemove("Erkennung"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", plural);
                Json.put(object, "object", "speechlistener");
                Json.put(object, "objectWords", lastwrd);
                suchwas = true;
            }

            if (ifContainsRemove("Spracheingaben")
                    || ifContainsRemove("Spracherkennungen")
                    || ifContainsRemove("Erkennungen"))
            {
                JSONObject object = new JSONObject();
                Json.put(objects, object);

                Json.put(object, "action", action);
                Json.put(object, "actionWords", actionWords);
                Json.put(object, "plural", true);
                Json.put(object, "object", "speechlistener");
                Json.put(object, "objectWords", lastwrd);
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

        if ((action == null) || action.isEmpty())
        {
            //
            // No action, no game.
            //

            return;
        }

        if ((objname != null) && (! objname.isEmpty()) && ! plural)
        {
            //
            // For device capability search we
            // like an explicit plural.
            //

            return;
        }

        String newaction = action;
        String capability = null;

        if ((objname != null) && ! objname.isEmpty())
        {
            if (objname.equals("camera") && ((action.equals("activate") || action.equals("deactivate"))))
            {
                capability = "closeopen";
            }

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

            if (objname.equals("bulb") && (action.equals("color")))
            {
                capability = "colorhsb";
            }

            if (objname.equals("tvremote") && (action.equals("select")))
            {
                capability = "tvremote";
            }
        }

        if (action.equals("pincode"))
        {
            capability = "pincode";
        }

        if ((capability == null) || capability.isEmpty()) return;

        Json.put(object, "action", newaction);

        //
        // Scan for matching devices.
        //

        JSONArray devices = new JSONArray();

        JSONArray list = IOTDevice.list.getUUIDList();

        for (int dinx = 0; dinx < list.length(); dinx++)
        {
            String uuid = Json.getString(list, dinx);
            IOTDevice device = IOTDevice.list.getEntry(uuid);
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

    private JSONArray evaluateDevices(String action, String actionWords)
    {
        JSONObject names = new JSONObject();

        JSONArray list = IOTDevice.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevice.list.getEntry(uuid);

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
            Json.put(device, "actionWords", actionWords);
            Json.put(device, "objectWords", name);
            Json.put(device, "devices", dlist);

            ifContainsRemove(name);
        }

        return devices;
    }

    private String evaluateInstrinsic()
    {
        if (ifIsPincode()
                || ifContains("PIN Code")
                || ifContains("PIN-Code"))
        {
            return "pincode";
        }

        if (ifContains("Kamera"))
        {
            return "open";
        }

        if (ifContains("Streetview")
                || ifContains("Street View"))
        {
            return "open";
        }

        if (ifContains("Wizard")
                || ifContains("Wizzard"))
        {
            return "open";
        }

        if (ifContains("Puff Beleuchtung")
                || ifContains("Bordell Beleuchtung")
                || ifContains("normale Beleuchtung"))
        {
            return "color";
        }

        return null;
    }

    private String evaluateAction()
    {
        if (ifContainsRemove("dunkler machen")
                || ifContainsRemove("dimmen"))
        {
            return "adjustneg";
        }

        if (ifContainsRemove("heller machen"))
        {
            return "adjustpos";
        }

        if (ifContainsRemove("aktivieren"))
        {
            return "activate";
        }

        if (ifContainsRemove("deaktivieren"))
        {
            return "deactivate";
        }

        if (ifContainsRemove("an")
                || ifContainsRemove("anschalten")
                || ifContainsRemove("einschalten"))
        {
            return "switchon";
        }

        if (ifContainsRemove("aus")
                || ifContainsRemove("abschalten")
                || ifContainsRemove("ausschalten"))
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
                || ifContainsRemove("einstellen"))
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

    private boolean ifIsPincode()
    {
        boolean pincode = false;

        if (message.length() == 4)
        {
            pincode = ('0' <= message.charAt(0)) && (message.charAt(0) <= '9')
                    && ('0' <= message.charAt(1)) && (message.charAt(1) <= '9')
                    && ('0' <= message.charAt(2)) && (message.charAt(2) <= '9')
                    && ('0' <= message.charAt(3)) && (message.charAt(3) <= '9');
        }

        if (pincode) lastwrd = message;

        return pincode;
    }

    private boolean ifIsPincodeRemove()
    {
        boolean pincode = ifIsPincode();

        if (pincode)
        {
            lastwrd = message;
            message = "";
        }

        return pincode;
    }

    private boolean ifContains(String target)
    {
        if ((target != null) && ! target.isEmpty())
        {
            String locmess = " " + message + " ";
            String loctarg = " " + target + " ";

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
                message = trim(locmess.substring(0, pos) + " " + locmess.substring(pos + len + 1));
                lastwrd = target;

                return true;
            }

            pos = locmess.toLowerCase().indexOf(loctarg.toLowerCase());

            if (pos >= 0)
            {
                message = trim(locmess.substring(0, pos) + locmess.substring(pos + len + 1));
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