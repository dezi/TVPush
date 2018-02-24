package com.p2p.pppp_api;

@SuppressWarnings({"JniMissingFunction", "unused"})
public class PPPP_APIs
{
    public static native int PPPP_Check(int i, PPPP_Session session);

    public static native int PPPP_CheckDevOnline(String str, String str2, int i, int[] iArr);

    public static native int PPPP_Check_Buffer(int i, byte b, int[] iArr, int[] iArr2);

    public static native int PPPP_Close(int i);

    public static native int PPPP_Config_Debug(byte b, int i);

    public static native int PPPP_Connect(String str, byte b, int i, String str2);

    public static native int PPPP_ConnectByServer(String str, byte b, int i, String str2, String str3);

    public static native int PPPP_ConnectByServerDefault(String str, String str2, String str3);

    public static native int PPPP_ConnectForDoolBell(String str, byte b, int i, String str2, String str3);

    public static native int PPPP_ConnectOnlyLanSearch(String str);

    public static native int PPPP_Connect_Break(String str);

    public static native int PPPP_Connect_To_With_MasterServer(String str, byte b, int i, String str2, String str3);

    public static native int PPPP_DeInitialize();

    public static native int PPPP_ForceClose(int i);

    public static native int PPPP_GetAPIVersion();

    public static native int PPPP_Initialize(byte[] bArr, int i);

    public static native int PPPP_IsConnecting();

    public static native int PPPP_Listen_Break();

    public static native int PPPP_Listen_With_Key(String str, int i, int i2, byte b, String str2);

    public static native int PPPP_Listen_With_Key2(String str, int i, int i2, byte b, String str2, byte b2);

    public static native int PPPP_LoginStatus_Check(byte[] bArr);

    public static native int PPPP_NetworkDetect(PPPP_NetInfo netInfo, int i);

    public static native int PPPP_NetworkDetectByServer(PPPP_NetInfo netInfo, int i, String str);

    public static native int PPPP_Probe(String str, int i, byte[] bArr, int i2);

    public static native int PPPP_Read(int i, byte b, byte[] bArr, int[] iArr, int i2);

    public static native int PPPP_SendLogin();

    public static native int PPPP_Set_Log_Filename(String str);

    public static native int PPPP_Share_Bandwidth(byte b);

    public static native int PPPP_Write(int i, byte b, byte[] bArr, int i2);
}
