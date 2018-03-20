package de.xavaro.android.iot.base;

public class IOTDefs
{
    public static final int IOT_SAVE_FAILED = -1;
    public static final int IOT_SAVE_UNCHANGED = 0x00;
    public static final int IOT_SAVE_SYSCHANGED = 0x01;
    public static final int IOT_SAVE_USRCHANGED = 0x02;
    public static final int IOT_SAVE_ALLCHANGED = IOT_SAVE_SYSCHANGED | IOT_SAVE_USRCHANGED;
}
