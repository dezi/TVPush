package zz.top.sny.base;

import android.support.annotation.Nullable;

import android.os.Environment;
import android.util.SparseArray;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;

import zz.top.utl.Json;

public class SNYPrograms
{
    private final static String LOGTAG = SNYPrograms.class.getSimpleName();

    public static void importSDB()
    {
        String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File sdbXML = new File(extPath, "sdb.xml");
        File sdbJSON = new File(extPath, "sdb.json");

        CharSequence sdbxml = readTextFile(sdbXML);

        if (sdbxml == null) return;

        JSONObject sdbjson = decodeSDB(sdbxml);

        if (sdbjson == null) return;

        writeTextFile(sdbJSON, Json.toPretty(sdbjson));

        Log.d(LOGTAG, "importSDB: getan...");

        registerChannels(sdbjson);
    }

    public static void registerChannels(JSONObject sdbjson)
    {
        JSONObject SdbRoot = Json.getObject(sdbjson, "SdbRoot");
        JSONObject SdbXml = Json.getObject(SdbRoot, "SdbXml");
        JSONObject sdbC = Json.getObject(SdbXml, "sdbC");
        JSONObject Service = Json.getObject(sdbC, "Service");

        JSONArray nos = Json.getArray(Service, "No");
        JSONArray names = Json.getArray(Service, "Name");
        JSONArray types = Json.getArray(Service, "ServiceFilter");

        //
        // How stupid can a single inder be?
        //

        JSONArray actives = Json.getArray(Service, "b_deleted_by_user");

        if (names == null)
        {
            Log.d(LOGTAG, "registerChannels: nix...");

            return;
        }

        for (int inx = 0; inx < nos.length(); inx++)
        {
            String name = Json.getString(names, inx);
            int active = Json.getInt(actives, inx);
            int no = Json.getInt(nos, inx) >> 18;
            int type = Json.getInt(types, inx);

            if (active != 1) continue;

            String typestr = (type == 1) ? "tv" : (type == 2) ? "radio" : "data";
            String nostr = Integer.toString(no);

            while (nostr.length() < 3) nostr = "0" + nostr;

            Log.d(LOGTAG, "registerChannels:"
                    + " no=" + nostr
                    + " type=" + typestr
                    + " name=" + name
            );

            JSONObject programm = new JSONObject();

            Json.put(programm, "name", name);
            Json.put(programm, "no", nostr);
            Json.put(programm, "type", typestr);

        }
    }

    @Nullable
    public static JSONObject decodeSDB(CharSequence xml)
    {
        return decodeSDB(xml, false);
    }

    @Nullable
    private static JSONObject decodeSDB(CharSequence xml, boolean debug)
    {
        SparseArray<Object> levels = new SparseArray<>();

        char ccc;
        int level = 0;
        int len = xml.length();
        boolean intag = false;
        boolean fucked = false;

        JSONObject root = new JSONObject();
        levels.put(level, root);

        Object currentJson = levels.get(0);
        String currentTag = "";

        StringBuilder currentText = new StringBuilder();

        for (int inx = 0; inx < len; inx++)
        {
            //if (level >= 4) break;

            ccc = xml.charAt(inx);

            if (ccc == '<')
            {
                intag = true;

                currentTag = "";

                continue;
            }

            if (ccc == '>')
            {
                intag = false;

                if (currentTag.startsWith("?"))
                {
                    //
                    // XML header. Skip.
                    //

                    if (debug) Log.d(LOGTAG, "importSDB: skip" + " level=" + level + " tag=" + currentTag);

                    continue;
                }

                if (currentTag.endsWith("/"))
                {
                    //
                    // Dummy tag does not change current level.
                    //

                    currentTag = currentTag.substring(0, currentTag.length() - 1);

                    String[] parts = currentTag.split(" ");
                    currentTag = parts[ 0 ];

                    if (currentJson instanceof JSONObject)
                    {
                        Object dummyJson = (parts.length > 1) ? new JSONArray() : new JSONObject();

                        Json.put((JSONObject) currentJson, currentTag, dummyJson);

                        String type = (dummyJson instanceof JSONObject) ? "JSONObject" : "JSONArray";
                        if (debug) Log.d(LOGTAG, "importSDB: dumm" + " level=" + level + " tag=" + currentTag + " type=" + type);

                        continue;
                    }

                    Log.e(LOGTAG, "importSDB: XML fucked (4)...");
                    fucked = true;
                    break;
                }

                if (currentTag.startsWith("/"))
                {
                    //
                    // Closing tag.
                    //

                    currentTag = currentTag.substring(1);
                    String innertext = currentText.toString().trim();

                    if (! innertext.isEmpty())
                    {
                        if (currentJson instanceof JSONArray)
                        {
                            if (debug)
                            {
                                Json.put((JSONArray) currentJson, innertext);
                            }
                            else
                            {
                                String[] lines = innertext.split("\n");

                                for (String line : lines)
                                {
                                    line = line.trim();

                                    Json.put((JSONArray) currentJson, isInteger(line) ? Long.valueOf(line) : SNYUtil.HTMLdefuck(line));
                                }

                                if (debug) Log.d(LOGTAG, "importSDB: done" + " level=" + level + " tag=" + currentTag + " lines=" + lines.length);
                            }
                        }
                        else
                        {
                            if (debug) Log.d(LOGTAG, "importSDB: done" + " level=" + level + " tag=" + currentTag + " text=" + innertext);

                            currentJson = isInteger(innertext) ? Long.valueOf(innertext) : SNYUtil.HTMLdefuck(innertext);
                        }

                        currentText = new StringBuilder();
                    }
                    else
                    {
                        String type = (currentJson instanceof JSONObject) ? "JSONObject" : "JSONArray";
                        if (debug) Log.d(LOGTAG, "importSDB: done" + " level=" + level + " tag=" + currentTag + " type=" + type);
                    }

                    if (level <= 0)
                    {
                        Log.e(LOGTAG, "importSDB: XML fucked (1)...");
                        fucked = true;
                        break;
                    }

                    Object lastJson = levels.get(--level);

                    if (lastJson instanceof JSONObject)
                    {
                        Json.put((JSONObject) lastJson, currentTag, currentJson);
                        currentJson = lastJson;

                        continue;
                    }

                    String type = lastJson.getClass().getSimpleName();
                    Log.e(LOGTAG, "importSDB: XML fucked (3)... type=" + type);
                    fucked = true;
                    break;
                }

                //
                // Open tag. Anticipate either JSONObject or JSONArray.
                //

                String[] parts = currentTag.split(" ");
                currentTag = parts[ 0 ];

                if (debug) Log.d(LOGTAG, "importSDB: open" + " level=" + level + " tag=" + currentTag);

                currentJson = (parts.length > 1) ? new JSONArray() : new JSONObject();
                levels.put(++level, currentJson);

                currentText = new StringBuilder();

                continue;
            }

            if (intag)
            {
                currentTag += ccc;

                continue;
            }

            currentText.append(ccc);
        }

        if (debug) Log.d(LOGTAG, "importSDB: json=" + Json.toPretty(root));

        return fucked ? null : root;
    }

    private static  boolean isInteger(CharSequence text)
    {
        for (int inx = 0; inx < text.length(); inx++)
        {
            if ((text.charAt(inx) < '0') || (text.charAt(inx) > '9'))
            {
                return false;
            }
        }

        return (text.length() > 0);
    }

    @Nullable
    private static String readTextFile(File filename)
    {
        byte[] bytes = readBinaryFile(filename);

        if (bytes != null)
        {
            return new String(bytes);
        }

        return null;
    }

    @Nullable
    private static byte[] readBinaryFile(File filename)
    {
        try
        {
            FileInputStream inputStream = new FileInputStream(filename);
            int size = (int) inputStream.getChannel().size();
            byte[] content = new byte[ size ];
            int xfer = inputStream.read(content);
            inputStream.close();

            return (xfer == size) ? content : null;
        }
        catch (FileNotFoundException ignore)
        {
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    private static boolean writeTextFile(File filename, String content)
    {
        return writeBinaryFile(filename, content.getBytes());
    }

    @Nullable
    private static boolean writeBinaryFile(File filename, byte[] bytes)
    {
        try
        {
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(bytes);
            outputStream.close();

            return true;
        }
        catch (FileNotFoundException ignore)
        {
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
}
