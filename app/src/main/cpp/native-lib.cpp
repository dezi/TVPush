#include <jni.h>
#include <string>

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1GetAPIVersion
        (JNIEnv *env, jclass);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Initialize
        (JNIEnv *env, jclass self, jbyteArray, jint);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1DeInitialize
        (JNIEnv *env, jclass);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Check
        (JNIEnv *env, jclass self, jint, jobject);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1CheckDevOnline
        (JNIEnv *env, jclass self, jstring, jstring, jint, jintArray);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1ConnectByServer
        (JNIEnv *env, jclass self, jstring, jbyte, jint, jstring, jstring);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Close
        (JNIEnv *env, jclass self, jint);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1ForceClose
        (JNIEnv *env, jclass self, jint);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Write
        (JNIEnv *env, jclass self, jint, jbyte, jbyteArray, jint);

extern "C" JNIEXPORT jint JNICALL Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1Read
        (JNIEnv *env, jclass self, jint, jbyte, jbyteArray, jintArray, jint);


















extern "C" JNIEXPORT jstring JNICALL Java_de_xavaro_android_tvpush_MainActivity_stringFromJNI(
        JNIEnv *env, jobject self)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_GetAPIVersion
        (JNIEnv *env, jclass self)
{
    return Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1GetAPIVersion(env, self);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Initialize
        (JNIEnv *env, jclass self, jbyteArray, jint)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_DeInitialize
        (JNIEnv *env, jclass)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Check
        (JNIEnv *env, jclass self, jint, jobject)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_CheckDevOnline
        (JNIEnv *env, jclass self, jstring, jstring, jint, jintArray)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_ConnectByServer
        (JNIEnv *env, jclass self, jstring, jbyte, jint, jstring, jstring)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Close
        (JNIEnv *env, jclass self, jint)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_ForceClose
        (JNIEnv *env, jclass self, jint)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Write
        (JNIEnv *env, jclass self, jint, jbyte, jbyteArray, jint)
{

}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Read
        (JNIEnv *env, jclass self, jint, jbyte, jbyteArray, jintArray, jint)
{

}
