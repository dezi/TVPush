package de.xavaro.android.brl.comm;

public class BRLUtil
{
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
