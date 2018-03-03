#include <jni.h>
#include <string>
#include <android/log.h>


//
//__android_log_print(ANDROID_LOG_INFO, "Hubuabddhdsds####%s####", "++++");
//


//
// C native external functions.
//

extern "C" int Java_com_aac_utils_DecodeAAC_nOpen(JNIEnv *env, jclass self);
extern "C" int Java_com_aac_utils_DecodeAAC_nDecode(JNIEnv *env, jclass self, jbyteArray bArr1, jint int1, jbyteArray bArr2, jint int2);
extern "C" int Java_com_aac_utils_DecodeAAC_nClose(JNIEnv *env, jclass self);

//
// JNI bridge methods.
//


extern "C" JNIEXPORT jint JNICALL Java_zz_top_aac_AACDecode_open
        (JNIEnv *env, jclass self)
{
    return Java_com_aac_utils_DecodeAAC_nOpen(env, self);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_aac_AACDecode_decode
        (JNIEnv *env, jclass self, jbyteArray bArr1, jint int1, jbyteArray bArr2, jint int2)
{
    return Java_com_aac_utils_DecodeAAC_nDecode(env, self, bArr1, int1, bArr2, int2);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_aac_AACDecode_close
        (JNIEnv *env, jclass self)
{
    return Java_com_aac_utils_DecodeAAC_nClose(env, self);
}

//
// C native external functions.
//

extern "C" int PPPP_GetAPIVersion();

extern "C" int PPPP_Initialize(const char *parameter, int keyLenght);
extern "C" int PPPP_DeInitialize();

extern "C" int PPPP_CheckDevOnline(const char *targetID, const char *serverString, int size, int *lastLoginTime);

extern "C" int PPPP_ConnectByServer(const char *targetID, char bEnableLanSearch, int udpPort, const char *serverString, const char *licenseKey);

extern "C" int PPPP_Close(int sessionHandle);
extern "C" int PPPP_ForceClose(int sessionHandle);

extern "C" int PPPP_Write(int sessionHandle, char channel, const char *dataBuff, int dataSizeToWrite);
extern "C" int PPPP_Read(int sessionHandle, char channel, char *dataBuff, int *dataSize, int timeOutMS);

//
// JNI bridge methods.
//

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_GetAPIVersion
        (JNIEnv *env, jclass self)
{
    return PPPP_GetAPIVersion();
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Initialize
        (JNIEnv *env, jclass self, jbyteArray parameter, jint keyLenght)
{
    jbyte *parameterNative = env->GetByteArrayElements(parameter, NULL);

    jint res = PPPP_Initialize((const char *) parameterNative, keyLenght);

    env->ReleaseByteArrayElements(parameter, parameterNative, JNI_COMMIT);

    return res;
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_DeInitialize
        (JNIEnv *env, jclass self)
{
    return PPPP_DeInitialize();
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_CheckDevOnline
        (JNIEnv *env, jclass self, jstring targetID, jstring serverString, jint size, jintArray lastLoginTime)
{
    const char *targetIDNative = env->GetStringUTFChars(targetID, NULL);
    const char *serverStringNative = env->GetStringUTFChars(serverString, NULL);

    jint *iArr1native = env->GetIntArrayElements(lastLoginTime, NULL);

    jint res = PPPP_CheckDevOnline(targetIDNative, serverStringNative, size, iArr1native);

    env->ReleaseIntArrayElements(lastLoginTime, iArr1native, JNI_COMMIT);

    return res;
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_ConnectByServer
        (JNIEnv *env, jclass self, jstring targetID, jbyte bEnableLanSearch, jint udpPort, jstring serverString, jstring licenseKey)
{
    const char *targetIDNative = env->GetStringUTFChars(targetID, NULL);
    const char *serverStringNative = env->GetStringUTFChars(serverString, NULL);
    const char *licenseKeyNative = env->GetStringUTFChars(licenseKey, NULL);

    return PPPP_ConnectByServer(targetIDNative, bEnableLanSearch, udpPort, serverStringNative, licenseKeyNative);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Close
        (JNIEnv *env, jclass self, jint sessionHandle)
{
    return PPPP_Close(sessionHandle);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_ForceClose
        (JNIEnv *env, jclass self, jint sessionHandle)
{
    return PPPP_ForceClose(sessionHandle);
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Write
        (JNIEnv *env, jclass self, jint sessionHandle, jbyte channel, jbyteArray dataBuff, jint dataSizeToWrite)
{
    jbyte *dataBuffNative = env->GetByteArrayElements(dataBuff, NULL);

    jint res = PPPP_Write(sessionHandle, channel, (const char *) dataBuffNative, dataSizeToWrite);

    env->ReleaseByteArrayElements(dataBuff, dataBuffNative, JNI_COMMIT);

    return res;
}

extern "C" JNIEXPORT jint JNICALL Java_zz_top_p2p_api_P2PApiNative_Read
        (JNIEnv *env, jclass self, jint sessionHandle, jbyte channel, jbyteArray dataBuff, jintArray dataSize, jint timeOutMS)
{
    jbyte *dataBuffNative = env->GetByteArrayElements(dataBuff, NULL);
    jint *dataSizeNative = env->GetIntArrayElements(dataSize, NULL);

    jint res = PPPP_Read(sessionHandle, channel, (char *) dataBuffNative, dataSizeNative, timeOutMS);

    env->ReleaseIntArrayElements(dataSize, dataSizeNative, JNI_COMMIT);
    env->ReleaseByteArrayElements(dataBuff, dataBuffNative, JNI_COMMIT);

    return res;
}
