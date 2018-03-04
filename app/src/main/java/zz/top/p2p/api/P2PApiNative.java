package zz.top.p2p.api;

public class P2PApiNative
{
    public static native int GetAPIVersion();

    public static native int Initialize(byte[] parameter, int keyLenght);
    public static native int DeInitialize();

    public static native int CheckDevOnline(String targetID, String serverString, int size, int[] lastLoginTime);
    public static native int ConnectByServer(String targetID, byte bEnableLanSearch, int udpPort, String serverString, String licenseKey);

    public static native int Close(int sessionHandle);
    public static native int ForceClose(int sessionHandle);

    public static native int Write(int sessionHandle, byte channel, byte[] dataBuff, int dataSizeToWrite);
    public static native int Read(int sessionHandle, byte channel, byte[] dataBuff, int[] dataSize, int timeOutMS);

    public static native int ShareBandwidth(int bOnOff);

    /*
    public static native int Check(int sessionHandle, P2PApiSession session);
    public static native int Check_Buffer(int sessionHandle, byte channel, int[] writeSize, int[] readSize);

    public static native int Listen_With_Key(String str1, int int1, int int2, byte byte1, String str2);

    public static native int ConnectOnlyLanSearch(String targetID);
    public static native int ConnectByServerDefault(String targetID, String serverString, String licenseKey);
    public static native int ConnectForDoolBell(String targetID, byte bEnableLanSearch, int udpPort, String serverString, String licenseKey);

    public static native int Connect(String targetID, byte bEnableLanSearch, int udpPort, String unknown1);
    public static native int Connect_To_With_MasterServer(String targetID, byte bEnableLanSearch, int udpPort, String unknown1, String unknown2);

    public static native int Connect_Break(String str1);

    public static native int IsConnecting();
    public static native int Listen_Break();
    public static native int Config_Debug(byte byte1, int int1);

    public static native int Listen_With_Key2(String str1, int int1, int int2, byte byte1, String str2, byte byte2);

    public static native int LoginStatus_Check(byte[] bLoginStatus);

    public static native int NetworkDetect(P2PApiNetInfo netInfo, int udpPort);

    public static native int NetworkDetectByServer(P2PApiNetInfo netInfo, int udpPort, String serverString);

    public static native int Probe(String str1, int int1, byte[] bArr1, int int2);

    public static native int SendLogin();

    public static native int Set_Log_Filename(String str1);

    */
}
