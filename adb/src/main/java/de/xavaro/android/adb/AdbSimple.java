package de.xavaro.android.adb;

public class AdbSimple
{
    private static final String LOGTAG = AdbSimple.class.getSimpleName();

    public static void wait(Object object)
    {
        try
        {
            object.wait();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
