package zz.top.p2p.api;

public class P2PApiNetInfo
{
    byte[] MyLanIP = new byte[16];
    byte[] MyWanIP = new byte[16];

    byte NAT_Type = (byte) 0;
    byte bFlagHostResolved = (byte) 0;
    byte bFlagInternet = (byte) 0;
    byte bFlagServerHello = (byte) 0;

    public String getMyLanIP()
    {
        return P2PApiSession.bytes2Str(this.MyLanIP);
    }

    public String getMyWanIP()
    {
        return P2PApiSession.bytes2Str(this.MyWanIP);
    }
}
