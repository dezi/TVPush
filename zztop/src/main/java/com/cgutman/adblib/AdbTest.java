package com.cgutman.adblib;

import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.net.Socket;
import java.util.Scanner;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbCrypto;
import com.cgutman.adblib.AdbStream;

public class AdbTest
{
    private static AdbCrypto setupCrypto(String pubKeyFile, String privKeyFile)
    {
        File ext = Environment.getExternalStorageDirectory();
        File pub = new File(ext, pubKeyFile);
        File priv = new File(ext, privKeyFile);

        AdbCrypto crypto = null;

        if (pub.exists() && priv.exists())
        {
            crypto = AdbCrypto.loadAdbKeyPair(priv, pub);
        }

        if (crypto == null)
        {
            crypto = AdbCrypto.generateAdbKeyPair();

            crypto.saveAdbKeyPair(priv, pub);

            System.out.println("Generated new keypair");
        }
        else
        {
            System.out.println("Loaded existing keypair");
        }

        return crypto;
    }

    public static AdbStream stream;

    public static void main()
    {
        Scanner in = new Scanner(System.in);
        AdbConnection adb;
        Socket sock;
        AdbCrypto crypto;

        crypto = setupCrypto("pub.key", "priv.key");

        try
        {
            sock = new Socket("192.168.0.11", 5555);
            sock.setTcpNoDelay(true);

            adb = AdbConnection.create(sock, crypto);
            adb.connect();

            stream = adb.open("shell:");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return;
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!stream.isClosed())
                {
                    try
                    {
                        System.out.print(new String(stream.read(), "US-ASCII"));
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                }
            }
        }).start();

        try
        {
            stream.write("ls\n");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
