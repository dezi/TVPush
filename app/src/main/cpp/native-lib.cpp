#include <jni.h>
#include <string>

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1GetAPIVersion
        (JNIEnv *env, jclass self);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Initialize
        (JNIEnv *env, jclass self, jbyteArray bArr1, jint int1);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1DeInitialize
        (JNIEnv *env, jclass self);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Check
        (JNIEnv *env, jclass self, jint int1, jobject obj1);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1CheckDevOnline
        (JNIEnv *env, jclass self, jstring str1, jstring str2, jint int1, jintArray iArr1);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1ConnectByServer
        (JNIEnv *env, jclass self, jstring str1, jbyte byte1, jint int1, jstring str2, jstring str3);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Close
        (JNIEnv *env, jclass self, jint int1);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1ForceClose
        (JNIEnv *env, jclass self, jint int1);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Write
        (JNIEnv *env, jclass self, jint int1, jbyte byte1, jbyteArray bArr1, jint int2);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Read
        (JNIEnv *env, jclass self, jint int1, jbyte byte1, jbyteArray bArr1, jintArray, jint int2);


















extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_GetAPIVersion
        (JNIEnv *env, jclass self)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1GetAPIVersion(env, self);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Initialize
        (JNIEnv *env, jclass self, jbyteArray bArr1, jint int1)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Initialize(env, self, bArr1, int1);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_DeInitialize
        (JNIEnv *env, jclass self)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1DeInitialize(env, self);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Check
        (JNIEnv *env, jclass self, jint int1, jobject obj1)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Check(env, self, int1, obj1);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_CheckDevOnline
        (JNIEnv *env, jclass self, jstring str1, jstring str2, jint int1, jintArray iArr1)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1CheckDevOnline(env, self, str1, str2, int1, iArr1);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_ConnectByServer
        (JNIEnv *env, jclass self, jstring str1, jbyte byte1, jint int1, jstring str2, jstring str3)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1ConnectByServer(env, self, str1, byte1, int1, str2, str3);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Close
        (JNIEnv *env, jclass self, jint int1)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Close(env, self, int1);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_ForceClose
        (JNIEnv *env, jclass self, jint int1)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1ForceClose(env, self, int1);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Write
        (JNIEnv *env, jclass self, jint int1, jbyte byte1, jbyteArray bArr1, jint int2)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Write(env, self, int1, byte1, bArr1, int2);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Read
        (JNIEnv *env, jclass self, jint int1, jbyte byte1, jbyteArray bArr1, jintArray iArr1, jint int2)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Read(env, self, int1, byte1, bArr1, iArr1, int2);
}

























extern "C" JNIEXPORT jstring JNICALL Java_de_xavaro_android_tvpush_MainActivity_stringFromJNI(
        JNIEnv *env, jobject self)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
