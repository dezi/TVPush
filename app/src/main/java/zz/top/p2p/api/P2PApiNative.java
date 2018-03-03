package zz.top.p2p.api;

public class P2PApiNative
{
    public static native int GetAPIVersion();

    public static native int Initialize(byte[] parameter, int keyLenght);
    public static native int DeInitialize();

    public static native int Check(int SessionHandle, P2PApiSession session);
    public static native int CheckDevOnline(String TargetID, String serverString, int i, int[] iArr);
    public static native int ConnectByServer(String TargetID, byte bEnableLanSearch, int UDP_Port, String serverString, String licenseKey);

    public static native int Close(int SessionHandle);
    public static native int ForceClose(int SessionHandle);

    public static native int Write(int SessionHandle, byte Channel, byte[] DataBuf, int DataSizeToWrite);
    public static native int Read(int SessionHandle, byte Channel, byte[] DataBuf, int[] DataSize, int TimeOut_ms);

    /*
    public static native int Check_Buffer(int SessionHandle, byte Channel, int[] WriteSize, int[] ReadSize);

    public static native int Listen_With_Key(String str, int i, int i2, byte b, String str2);

    public static native int ConnectOnlyLanSearch(String TargetID);
    public static native int ConnectByServerDefault(String TargetID, String serverString, String licenseKey);
    public static native int ConnectForDoolBell(String TargetID, byte bEnableLanSearch, int UDP_Port, String serverString, String licenseKey);

    public static native int Connect(String TargetID, byte bEnableLanSearch, int UDP_Port, String unknown1);
    public static native int Connect_To_With_MasterServer(String TargetID, byte bEnableLanSearch, int UDP_Port, String unknown1, String unknown2);

    public static native int Connect_Break(String str);

    public static native int IsConnecting();
    public static native int Listen_Break();
    public static native int Config_Debug(byte b, int i);

    public static native int Listen_With_Key2(String str, int i, int i2, byte b, String str2, byte b2);

    public static native int LoginStatus_Check(byte[] bLoginStatus);

    public static native int NetworkDetect(P2PApiNetInfo netInfo, int UDP_Port);

    public static native int NetworkDetectByServer(P2PApiNetInfo netInfo, int UDP_Port, String ServerString);

    public static native int Probe(String str, int i, byte[] bArr, int i2);

    public static native int SendLogin();

    public static native int Set_Log_Filename(String str);

    public static native int Share_Bandwidth(byte bOnOff);
    */
}
