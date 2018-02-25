package com.p2p.pppp_api;

@SuppressWarnings({"JniMissingFunction", "unused"})
public class PPPP_APIs
{
    public static native int PPPP_Initialize(byte[] parameter, int keyLenght);
    public static native int PPPP_DeInitialize();

    public static native int PPPP_CheckDevOnline(String TargetID, String str2, int i, int[] iArr);

    public static native int PPPP_Check(int SessionHandle, PPPP_Session session);
    public static native int PPPP_Check_Buffer(int SessionHandle, byte Channel, int[] WriteSize, int[] ReadSize);
    public static native int PPPP_Close(int SessionHandle);
    public static native int PPPP_ForceClose(int SessionHandle);

    public static native int PPPP_Listen_With_Key(String str, int i, int i2, byte b, String str2);

    public static native int PPPP_ConnectOnlyLanSearch(String TargetID);
    public static native int PPPP_ConnectByServer(String TargetID, byte bEnableLanSearch, int UDP_Port, String serverString, String licenseKey);
    public static native int PPPP_ConnectByServerDefault(String TargetID, String serverString, String licenseKey);
    public static native int PPPP_ConnectForDoolBell(String TargetID, byte bEnableLanSearch, int UDP_Port, String serverString, String licenseKey);

    public static native int PPPP_Connect(String TargetID, byte bEnableLanSearch, int UDP_Port, String unknown1);
    public static native int PPPP_Connect_To_With_MasterServer(String TargetID, byte bEnableLanSearch, int UDP_Port, String unknown1, String unknown2);

    public static native int PPPP_Connect_Break(String str);

    public static native int PPPP_GetAPIVersion();
    public static native int PPPP_IsConnecting();
    public static native int PPPP_Listen_Break();
    public static native int PPPP_Config_Debug(byte b, int i);

    public static native int PPPP_Listen_With_Key2(String str, int i, int i2, byte b, String str2, byte b2);

    public static native int PPPP_LoginStatus_Check(byte[] bLoginStatus);

    public static native int PPPP_NetworkDetect(PPPP_NetInfo netInfo, int UDP_Port);

    public static native int PPPP_NetworkDetectByServer(PPPP_NetInfo netInfo, int UDP_Port, String ServerString);

    public static native int PPPP_Probe(String str, int i, byte[] bArr, int i2);

    public static native int PPPP_SendLogin();

    public static native int PPPP_Set_Log_Filename(String str);

    public static native int PPPP_Share_Bandwidth(byte bOnOff);

    public static native int PPPP_Write(int SessionHandle, byte Channel, byte[] DataBuf, int DataSizeToWrite);

    public static native int PPPP_Read(int SessionHandle, byte Channel, byte[] DataBuf, int[] DataSize, int TimeOut_ms);
}
