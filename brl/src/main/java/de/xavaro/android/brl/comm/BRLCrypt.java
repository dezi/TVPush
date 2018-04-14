package de.xavaro.android.brl.comm;

import android.support.annotation.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class BRLCrypt
{
    private static final String CIPHER_ALGO = "AES/CBC/NoPadding";

    private static final String KEY_ALGO = "AES";

    public static final byte[] INITIAL_KEY =
            {
                    0x09, 0x76, 0x28, 0x34,
                    0x3f, (byte) 0xe9, (byte) 0x9e, 0x23,
                    0x76, 0x5c, 0x15, 0x13,
                    (byte) 0xac, (byte) 0xcf, (byte) 0x8b, 0x02
            };

    public static final byte[] INITIAL_IV =
            {
                    0x56, 0x2e, 0x17, (byte) 0x99,
                    0x6d, 0x09, 0x3d, 0x28,
                    (byte) 0xdd, (byte) 0xb3, (byte) 0xba,
                    0x69, 0x5a, 0x2e, 0x6f, 0x58
            };

    private final byte[] key;

    private final byte[] iv;

    public BRLCrypt()
    {
        this.key = INITIAL_KEY;
        this.iv = INITIAL_IV;
    }

    @Nullable
    public byte[] encrypt(byte[] data)
    {
        try
        {
            Cipher c = Cipher.getInstance(CIPHER_ALGO);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGO), new IvParameterSpec(iv));
            return c.doFinal(data);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public byte[] decrypt(byte[] data)
    {
        try
        {
            Cipher c = Cipher.getInstance(CIPHER_ALGO);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGO), new IvParameterSpec(iv));
            return c.doFinal(data);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }
}
