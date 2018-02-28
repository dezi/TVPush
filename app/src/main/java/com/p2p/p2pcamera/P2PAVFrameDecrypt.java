package com.p2p.p2pcamera;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class P2PAVFrameDecrypt
{
    private static final String LOGTAG = P2PAVFrame.class.getSimpleName();

    private Cipher cipher;

    public P2PAVFrameDecrypt(String password)
    {
        cipher = getCipher(password);
    }

    public void decryptIframe(P2PAVFrame aVFrame)
    {
        if (aVFrame.frmData.length >= 36)
        {
            byte[] data = new byte[16];

            System.arraycopy(aVFrame.frmData, 4, data, 0, 16);
            System.arraycopy(decrypt(data), 0, aVFrame.frmData, 4, 16);
            System.arraycopy(aVFrame.frmData, 20, data, 0, 16);
            System.arraycopy(decrypt(data), 0, aVFrame.frmData, 20, 16);
        }
    }

    private byte[] decrypt(byte[] data)
    {
        try
        {
            return cipher.doFinal(data);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return data;
        }
    }

    private Cipher getCipher(String password)
    {
        try
        {
            Key secretKeySpec = new SecretKeySpec(password.getBytes("ASCII"), "AES");

            Cipher instance = Cipher.getInstance("AES/ECB/NoPadding");
            instance.init(2, secretKeySpec);

            return instance;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
