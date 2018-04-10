package de.xavaro.android.edx.comm;

public class EDXUtil
{
    private static final String LOGTAG = EDXUtil.class.getSimpleName();

    public static String getCapabilities(String model)
    {
        String caps = "smartplug|fixed|tcp|wifi|stupid|timer|plugonoff|ledonoff";

        if ("SP2101W".equals(model) || "SP2101W_V2".equals(model))
        {
            caps += "|energy";
        }

        return caps;
    }
}
