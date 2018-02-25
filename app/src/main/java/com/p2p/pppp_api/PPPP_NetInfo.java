package com.p2p.pppp_api;

public class PPPP_NetInfo
{
    byte[] MyLanIP = new byte[16];
    byte[] MyWanIP = new byte[16];

    byte NAT_Type = (byte) 0;
    byte bFlagHostResolved = (byte) 0;
    byte bFlagInternet = (byte) 0;
    byte bFlagServerHello = (byte) 0;

    public String getMyLanIP()
    {
        return PPPP_Session.bytes2Str(this.MyLanIP);
    }

    public String getMyWanIP()
    {
        return PPPP_Session.bytes2Str(this.MyWanIP);
    }
}
