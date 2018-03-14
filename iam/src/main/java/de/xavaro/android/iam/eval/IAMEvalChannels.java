package de.xavaro.android.iam.eval;

public class IAMEvalChannels
{
    public static boolean isChannel(String word)
    {
        return (word.equals("ProSieben")
            || word.equals("Super RTL")
            || word.equals("RTL2")
            || word.equals("ZDF")
            || word.equals("ARD")
            || word.equals("RTL")
            || word.equals("Sat1")
        );
    }
}
