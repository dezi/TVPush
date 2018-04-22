package de.xavaro.android.awx.comm;

import android.util.Log;

public class AWXAES
{
    private static final String LOGTAG = AWXAES.class.getSimpleName();

    public static native byte[] decrypt(byte[] key, byte[] nonce, byte[] crypt);
    public static native byte[] encrypt(byte[] key, byte[] nonce, byte[] plain);

    static
    {
        Log.e(LOGTAG, "static: load awxaes");
        System.loadLibrary("awxaes");
        Log.e(LOGTAG, "static: loaded awxaes");
    }
}
