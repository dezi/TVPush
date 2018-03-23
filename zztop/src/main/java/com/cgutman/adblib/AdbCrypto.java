package com.cgutman.adblib;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class AdbCrypto
{
    private static final String LOGTAG = AdbCrypto.class.getSimpleName();

    private KeyPair keyPair;

    public static final int KEY_LENGTH_BITS = 2048;
    public static final int KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8;
    public static final int KEY_LENGTH_WORDS = KEY_LENGTH_BYTES / 4;

    public static final int[] SIGNATURE_PADDING_AS_INT = new int[]
            {
                    0x00, 0x01, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00,
                    0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e, 0x03, 0x02, 0x1a, 0x05, 0x00,
                    0x04, 0x14
            };

    public static byte[] SIGNATURE_PADDING;

    static
    {
        SIGNATURE_PADDING = new byte[SIGNATURE_PADDING_AS_INT.length];

        for (int i = 0; i < SIGNATURE_PADDING.length; i++)
            SIGNATURE_PADDING[i] = (byte) SIGNATURE_PADDING_AS_INT[i];
    }

    private static byte[] convertRsaPublicKeyToAdbFormat(RSAPublicKey pubkey)
    {
        /*
		 * ADB literally just saves the RSAPublicKey struct to a file.
		 * 
		 * typedef struct RSAPublicKey {
         * int len; // Length of n[] in number of uint32_t
         * uint32_t n0inv;  // -1 / n[0] mod 2^32
         * uint32_t n[RSANUMWORDS]; // modulus as little endian array
         * uint32_t rr[RSANUMWORDS]; // R^2 as little endian array
         * int exponent; // 3 or 65537
         * } RSAPublicKey;
		 */

		/* ------ This part is a Java-ified version of RSA_to_RSAPublicKey from adb_host_auth.c ------ */
        BigInteger r32, r, rr, rem, n, n0inv;

        r32 = BigInteger.ZERO.setBit(32);
        n = pubkey.getModulus();
        r = BigInteger.ZERO.setBit(KEY_LENGTH_WORDS * 32);
        rr = r.modPow(BigInteger.valueOf(2), n);
        rem = n.remainder(r32);
        n0inv = rem.modInverse(r32);

        int myN[] = new int[KEY_LENGTH_WORDS];
        int myRr[] = new int[KEY_LENGTH_WORDS];
        BigInteger res[];
        for (int i = 0; i < KEY_LENGTH_WORDS; i++)
        {
            res = rr.divideAndRemainder(r32);
            rr = res[0];
            rem = res[1];
            myRr[i] = rem.intValue();

            res = n.divideAndRemainder(r32);
            n = res[0];
            rem = res[1];
            myN[i] = rem.intValue();
        }

		/* ------------------------------------------------------------------------------------------- */

        ByteBuffer bbuf = ByteBuffer.allocate(524).order(ByteOrder.LITTLE_ENDIAN);


        bbuf.putInt(KEY_LENGTH_WORDS);
        bbuf.putInt(n0inv.negate().intValue());
        for (int i : myN)
            bbuf.putInt(i);
        for (int i : myRr)
            bbuf.putInt(i);

        bbuf.putInt(pubkey.getPublicExponent().intValue());
        return bbuf.array();
    }


    public static AdbCrypto loadAdbKeyPair(File privateKey, File publicKey)
    {
        try
        {
            AdbCrypto crypto = new AdbCrypto();

            int privKeyLength = (int) privateKey.length();
            int pubKeyLength = (int) publicKey.length();

            byte[] privKeyBytes = new byte[privKeyLength];
            byte[] pubKeyBytes = new byte[pubKeyLength];

            FileInputStream privIn = new FileInputStream(privateKey);
            FileInputStream pubIn = new FileInputStream(publicKey);

            privIn.read(privKeyBytes);
            pubIn.read(pubKeyBytes);

            privIn.close();
            pubIn.close();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubKeyBytes);

            crypto.keyPair = new KeyPair(
                    keyFactory.generatePublic(publicKeySpec),
                    keyFactory.generatePrivate(privateKeySpec));

            Log.d(LOGTAG, "############### read key");

            return crypto;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static AdbCrypto generateAdbKeyPair()
    {
        try
        {
            AdbCrypto crypto = new AdbCrypto();

            KeyPairGenerator rsaKeyPg = KeyPairGenerator.getInstance("RSA");
            rsaKeyPg.initialize(KEY_LENGTH_BITS);

            crypto.keyPair = rsaKeyPg.genKeyPair();

            return crypto;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Signs the ADB SHA1 payload with the private key of this object.
     *
     * @param payload SHA1 payload to sign
     * @return Signed SHA1 payload
     * @throws GeneralSecurityException If signing fails
     */

    public byte[] signAdbTokenPayload(byte[] payload) throws GeneralSecurityException
    {
        Cipher c = Cipher.getInstance("RSA/ECB/NoPadding");

        c.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());

        c.update(SIGNATURE_PADDING);

        return c.doFinal(payload);
    }


    public byte[] getAdbPublicKeyPayload() throws IOException
    {
        byte[] convertedKey = convertRsaPublicKeyToAdbFormat((RSAPublicKey) keyPair.getPublic());

        StringBuilder keyString = new StringBuilder(720);

        //
        // The key is base64 encoded with a user@host suffix
        // and terminated with a NUL.
        //

        keyString.append(Base64.encodeToString(convertedKey,0));
        keyString.append(" unknown@unknown");
        keyString.append('\0');

        return keyString.toString().getBytes("UTF-8");
    }


    public void saveAdbKeyPair(File privateKey, File publicKey)
    {
        try
        {
            FileOutputStream privOut = new FileOutputStream(privateKey);
            FileOutputStream pubOut = new FileOutputStream(publicKey);

            privOut.write(keyPair.getPrivate().getEncoded());
            pubOut.write(keyPair.getPublic().getEncoded());

            privOut.close();
            pubOut.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
