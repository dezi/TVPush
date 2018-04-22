package com.telink.crypto;

import android.util.Log;

public class AES
{
    private static final String LOGTAG = "AWX" + AES.class.getSimpleName();

    private static native byte[] decryptCmd(byte[] bArr, byte[] bArr2, byte[] bArr3);
    private static native byte[] encryptCmd(byte[] bArr, byte[] bArr2, byte[] bArr3);

    static
    {
        Log.e(LOGTAG, "static: load TelinkCrypto");
        System.loadLibrary("TelinkCrypto");
        Log.e(LOGTAG, "static: loaded TelinkCrypto");
    }

    public static void initialize()
    {
    }

    public static byte[] encrypt(byte[] key, byte[] nonce, byte[] plaintext)
    {
        return encryptCmd(plaintext, nonce, key);
    }

    public static byte[] decrypt(byte[] key, byte[] nonce, byte[] plaintext)
    {
        return decryptCmd(plaintext, nonce, key);
    }
}
