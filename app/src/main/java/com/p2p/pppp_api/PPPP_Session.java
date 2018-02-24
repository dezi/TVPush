package com.p2p.pppp_api;

public class PPPP_Session
{
    int ConnectTime = 0;
    int ConnectTimeP2P = 0;
    int ConnectTimeRelay = 0;
    byte[] DID = new byte[24];
    byte[] MyLocalIP = new byte[16];
    int MyLocalPort = 0;
    byte[] MyWanIP = new byte[16];
    int MyWanPort = 0;
    byte[] RemoteIP = new byte[16];
    int RemotePort = 0;
    int Skt = -1;
    byte bCorD = (byte) 0;
    byte bMode = (byte) 0;

    public static String bytes2Str(byte[] bArr)
    {
        String str = "";
        int i = 0;
        while (i < bArr.length && bArr[i] != (byte) 0)
        {
            i++;
        }
        return i == 0 ? "" : new String(bArr, 0, i);
    }

    public int getConnectTime()
    {
        return this.ConnectTime;
    }

    public int getConnectTimeP2P()
    {
        return this.ConnectTimeP2P;
    }

    public int getConnectTimeRelay()
    {
        return this.ConnectTimeRelay;
    }

    public int getCorD()
    {
        return this.bCorD & 255;
    }

    public String getDID()
    {
        return bytes2Str(this.DID);
    }

    public int getMode()
    {
        return this.bMode & 255;
    }

    public String getMyLocalIP()
    {
        return bytes2Str(this.MyLocalIP);
    }

    public int getMyLocalPort()
    {
        return this.MyLocalPort;
    }

    public String getMyWanIP()
    {
        return bytes2Str(this.MyWanIP);
    }

    public int getMyWanPort()
    {
        return this.MyWanPort;
    }

    public String getRemoteIP()
    {
        return bytes2Str(this.RemoteIP);
    }

    public int getRemotePort()
    {
        return this.RemotePort;
    }

    public int getSkt()
    {
        return this.Skt;
    }
}
