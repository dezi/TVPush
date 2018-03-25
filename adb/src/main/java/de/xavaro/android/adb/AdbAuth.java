package de.xavaro.android.adb;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.EncodedKeySpec;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.KeyPair;

import javax.crypto.Cipher;

@SuppressWarnings("WeakerAccess")
public class AdbAuth
{
    private final static String LOGTAG = AdbAuth.class.getSimpleName();

    private final static boolean saveToPrefs = true;

    private final static String pubKeyName = "adb.auth.rsa.public";
    private final static String privKeyName = "adb.auth.rsa.private";

    private KeyPair keyPair;

    private static final int KEY_LENGTH_BITS = 2048;
    private static final int KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8;
    private static final int KEY_LENGTH_WORDS = KEY_LENGTH_BYTES / 4;

    private static byte[] SIGNATURE_PADDING_AS_BYTE;

    private static final int[] SIGNATURE_PADDING_AS_INT = new int[]
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

    static
    {
        SIGNATURE_PADDING_AS_BYTE = new byte[SIGNATURE_PADDING_AS_INT.length];

        for (int inx = 0; inx < SIGNATURE_PADDING_AS_BYTE.length; inx++)
        {
            SIGNATURE_PADDING_AS_BYTE[inx] = (byte) SIGNATURE_PADDING_AS_INT[inx];
        }
    }

    public static AdbAuth createAdbAuth(Context context)
    {
        AdbAuth adbAuth = new AdbAuth();

        if (! adbAuth.loadKeyPair(context))
        {
            if (adbAuth.generateRSAKeyPair())
            {
                adbAuth.saveKeyPair(context);
            }
            else
            {
                Log.e(LOGTAG, "createAdbAuth: RSA crypto not available!");

                adbAuth = null;
            }
        }
        else
        {
            Log.d(LOGTAG, "createAdbAuth: RSA crypto loaded.");
        }

        return adbAuth;
    }

    private boolean loadKeyPair(Context context)
    {
        try
        {
            boolean ok;

            byte[] pubKeyBytes;
            byte[] privKeyBytes;

            if (saveToPrefs)
            {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

                pubKeyBytes = Base64.decode(prefs.getString(pubKeyName, null), Base64.DEFAULT);
                privKeyBytes = Base64.decode(prefs.getString(privKeyName, null), Base64.DEFAULT);

                ok = (pubKeyBytes != null) && (privKeyBytes != null);
            }
            else
            {
                File ext = Environment.getExternalStorageDirectory();

                File publicKey = new File(ext, pubKeyName);
                int pubKeyLength = (int) publicKey.length();
                pubKeyBytes = new byte[pubKeyLength];

                FileInputStream pubIn = new FileInputStream(publicKey);
                int pubRead = pubIn.read(pubKeyBytes);
                pubIn.close();

                Log.d(LOGTAG, "loadKeyPair: public len=" + pubRead + " ok=" + (pubRead == pubKeyLength));

                File privateKey = new File(ext, privKeyName);
                int privKeyLength = (int) privateKey.length();
                privKeyBytes = new byte[privKeyLength];

                FileInputStream privIn = new FileInputStream(privateKey);
                int privRead = privIn.read(privKeyBytes);
                privIn.close();

                Log.d(LOGTAG, "loadKeyPair: private len=" + privRead + " ok=" + (privRead == privKeyLength));

                ok = (pubRead == pubKeyLength) && (privRead == privKeyLength);
            }

            if (ok)
            {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubKeyBytes);
                EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);

                keyPair = new KeyPair(keyFactory.generatePublic(publicKeySpec),
                        keyFactory.generatePrivate(privateKeySpec));
            }

            Log.d(LOGTAG, "loadKeyPair: loaded key pairs ok=" + ok);

            return ok;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    private boolean saveKeyPair(Context context)
    {
        try
        {
            if (saveToPrefs)
            {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

                String pubBase64 = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);
                boolean publicOk = prefs.edit().putString(pubKeyName, pubBase64).commit();

                Log.d(LOGTAG, "saveKeyPair: public=" + pubKeyName + " ok=" + publicOk);

                String privBase64 = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.NO_WRAP);
                boolean privateOk = prefs.edit().putString(privKeyName, privBase64).commit();

                Log.d(LOGTAG, "saveKeyPair: public=" + privKeyName + " ok=" + privateOk);

                return publicOk && privateOk;
            }
            else
            {
                File ext = Environment.getExternalStorageDirectory();

                File publicKey = new File(ext, pubKeyName);
                FileOutputStream pubOut = new FileOutputStream(publicKey);
                pubOut.write(keyPair.getPublic().getEncoded());
                pubOut.close();

                Log.d(LOGTAG, "saveKeyPair: public=" + publicKey.toString());

                File privateKey = new File(ext, privKeyName);
                FileOutputStream privOut = new FileOutputStream(privateKey);
                privOut.write(keyPair.getPrivate().getEncoded());
                privOut.close();

                Log.d(LOGTAG, "saveKeyPair: private=" + privateKey.toString());

                return true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    private boolean generateRSAKeyPair()
    {
        try
        {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_LENGTH_BITS);
            keyPair = keyPairGenerator.genKeyPair();

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    public byte[] signAdbTokenPayload(byte[] payload) throws GeneralSecurityException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");

        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
        cipher.update(SIGNATURE_PADDING_AS_BYTE);

        return cipher.doFinal(payload);
    }

    public byte[] getAdbPublicKeyPayload()
    {
        byte[] adbKey = convertRSAPublicKeyToAdbFormat((RSAPublicKey) keyPair.getPublic());
        String b64Key = Base64.encodeToString(adbKey, Base64.NO_WRAP).trim();

        String keyString = b64Key + " unknown@unknown" + '\0';

        return keyString.getBytes();
    }

    private byte[] convertRSAPublicKeyToAdbFormat(RSAPublicKey pubkey)
    {
        //
        // ADB literally just saves the RSAPublicKey struct to a file.
        //
        // typedef struct RSAPublicKey
        // {
        //      int len;                    // Length of n[] in number of uint32_t
        //      uint32_t n0inv;             // -1 / n[0] mod 2^32
        //      uint32_t n[RSANUMWORDS];    // modulus as little endian array
        //      uint32_t rr[RSANUMWORDS];   // R^2 as little endian array
        //      int exponent;               // 3 or 65537
        //
        // } RSAPublicKey;
        //

		// --- This part is a Java-ified version of RSA_to_RSAPublicKey from adb_host_auth.c ---

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

        for (int inx = 0; inx < KEY_LENGTH_WORDS; inx++)
        {
            res = rr.divideAndRemainder(r32);
            rr = res[0];
            rem = res[1];
            myRr[inx] = rem.intValue();

            res = n.divideAndRemainder(r32);
            n = res[0];
            rem = res[1];
            myN[inx] = rem.intValue();
        }

		// --------------------------------------------------------------------------------------

        ByteBuffer rsabuf = ByteBuffer.allocate(524).order(ByteOrder.LITTLE_ENDIAN);

        rsabuf.putInt(KEY_LENGTH_WORDS);
        rsabuf.putInt(n0inv.negate().intValue());

        for (int inx : myN) rsabuf.putInt(inx);
        for (int inx : myRr) rsabuf.putInt(inx);

        rsabuf.putInt(pubkey.getPublicExponent().intValue());

        return rsabuf.array();
    }
}
