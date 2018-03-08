package de.xavaro.android.iot;

import org.json.JSONArray;

@SuppressWarnings("WeakerAccess")
public class IOTDevice extends IOTBase
{
    public final String TYPE_PC = "pc";
    public final String TYPE_TV = "tv";
    public final String TYPE_PHONE = "phone";
    public final String TYPE_TABLET = "tablet";
    public final String TYPE_LAPTOP = "laptop";
    public final String TYPE_CAMERA= "camera";

    public String nick;
    public String name;
    public String type;
    public String model;
    public String brand;
    public String version;
    public String location;

    public JSONArray capabilities = new JSONArray();

    public IOTDevice()
    {
        super();
    }

    public IOTDevice(String uuid)
    {
        super(uuid);
    }
}
