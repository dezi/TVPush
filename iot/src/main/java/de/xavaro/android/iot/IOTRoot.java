package de.xavaro.android.iot;

import android.util.Log;

@SuppressWarnings("WeakerAccess")
public class IOTRoot extends IOTBase
{
    private final static String LOGTAG = IOTRoot.class.getSimpleName();

    public static IOTRoot root;
    public static IOTMeme meme;

    static
    {
        root = new IOTRoot();

        if (! root.loadFromStorage())
        {
            root.saveToStorage();
        }

        meme = new IOTMeme(root.uuid);

        if (! meme.loadFromStorage())
        {
            meme.saveToStorage();
        }

        Log.d(LOGTAG, "meme=" + meme.uuid);
    }

    @Override
    public String getKey()
    {
        //
        // This class is a singleton.
        //

        return "iot." + IOTRoot.class.getSimpleName();
    }
}
