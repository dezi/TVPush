package de.xavaro.android.sny.base;

import android.support.annotation.Nullable;

import android.os.Environment;

import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;

import de.xavaro.android.sny.simple.Json;
import de.xavaro.android.sny.simple.Log;

public class SNYPrograms
{
    private final static String LOGTAG = SNYPrograms.class.getSimpleName();

    private final static String ChecksumRegex = "<CheckSum>([^<]*)<\\/CheckSum>";

    public static void importSDB(String uuid)
    {
        File external = Environment.getExternalStorageDirectory();

        File sdbXML = new File(external, "sdb.xml");
        File sdbJSON = new File(external, "sdb.json");

        if (sdbXML.exists())
        {
            importSDB(uuid, sdbXML, sdbJSON);
        }
        else
        {
            File storage = new File("/storage");
            File[] dir = storage.listFiles();

            for (File sub : dir)
            {
                Log.d(LOGTAG, "importSDB: sub=" + sub.getAbsolutePath());

                sdbXML = new File(sub, "sdb.xml");

                if (sdbXML.exists())
                {
                    importSDB(uuid, sdbXML, sdbJSON);

                    break;
                }
            }
        }
    }

    public static void importSDB(String uuid, File sdbXML, File sdbJSON)
    {
        Log.d(LOGTAG, "importSDB:"
                + " sdbXML=" + sdbXML.getAbsolutePath()
                + " exists=" + sdbXML.exists()
        );

        Log.d(LOGTAG, "importSDB:"
                + " sdbJSON=" + sdbJSON.getAbsolutePath()
                + " exists=" + sdbJSON.exists()
        );

        if (sdbXML.exists() && sdbJSON.exists())
        {
            if (sdbXML.lastModified() < sdbJSON.lastModified())
            {
                Log.d(LOGTAG, "importSDB: sdbXML older than sdbJSON, skip import.");

                return;
            }
        }

        String sdbxml = readTextFile(sdbXML);
        if (sdbxml == null) return;

        String startTag = "<SdbXml>";
        String endTag = "</SdbXml>\n";

        int startPos = sdbxml.indexOf(startTag);
        int endPos = sdbxml.indexOf(endTag) + endTag.length();

        String checkString = sdbxml.substring(startPos, endPos);
        byte[] checkBytes = checkString.getBytes();

        String checksumSelf = Integer.toHexString(SNYCRC32.crc32(checkBytes));
        String checksumSony = SNYUtil.matchStuff(sdbxml, ChecksumRegex);

        Log.d(LOGTAG, "importSDB: checksumSony=" + checksumSony);
        Log.d(LOGTAG, "importSDB: checksumSelf=" + checksumSelf);

        JSONObject sdbjson = decodeSDB(sdbxml);

        if (sdbjson == null) return;

        writeTextFile(sdbJSON, Json.toPretty(sdbjson));

        Log.d(LOGTAG, "importSDB: done.");

        registerChannels(uuid, sdbjson);
    }

    public static void registerChannels(String uuid, JSONObject sdbjson)
    {
        JSONObject SdbRoot = Json.getObject(sdbjson, "SdbRoot");
        JSONObject SdbXml = Json.getObject(SdbRoot, "SdbXml");
        JSONObject sdbC = Json.getObject(SdbXml, "sdbC");
        JSONObject Service = Json.getObject(sdbC, "Service");

        JSONArray nos = Json.getArray(Service, "No");
        JSONArray names = Json.getArray(Service, "Name");
        JSONArray types = Json.getArray(Service, "ServiceFilter");
        JSONArray actives = Json.getArray(Service, "b_deleted_by_user");

        if ((names == null) || (nos == null))
        {
            Log.e(LOGTAG, "registerChannels: nix...");

            return;
        }

        JSONObject metadata = new JSONObject();
        JSONArray channels = new JSONArray();

        Json.put(metadata, "uuid", uuid);
        Json.put(metadata, "PUBChannels", channels);

        for (int inx = 0; inx < names.length(); inx++)
        {
            String name = Json.getString(names, inx);
            int active = Json.getInt(actives, inx);
            int dial = Json.getInt(nos, inx) >> 18;
            int type = Json.getInt(types, inx);

            if (active != 1) continue;

            String typestr = (type == 1) ? "tv" : (type == 2) ? "radio" : "data";
            String dialstr = Integer.toString(dial);

            while (dialstr.length() < 3) dialstr = "0" + dialstr;

            Log.d(LOGTAG, "registerChannels:"
                    + " dial=" + dialstr
                    + " type=" + typestr
                    + " name=" + name
            );

            JSONObject channel = new JSONObject();

            Json.put(channel, "name", name);
            Json.put(channel, "dial", dialstr);
            Json.put(channel, "type", typestr);

            Json.put(channels, channel);
        }

        SNY.instance.onDeviceMetadata(metadata);
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

    private static boolean writeTextFile(File filename, String content)
    {
        return writeBinaryFile(filename, content.getBytes());
    }

    private static boolean writeBinaryFile(File filename, byte[] bytes)
    {
        try
        {
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(bytes);
            outputStream.close();

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
}
