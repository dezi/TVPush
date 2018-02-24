#include <jni.h>
#include <string>
#include <stdio.h>

extern "C" jstring Java_de_xavaro_android_yihome_Camera_stringFromJNI(
        JNIEnv *env,
        jclass type)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

int PPPP_GetAPIVersion();
int PPPP_ConnectOnlyLanSearch(char *);

extern "C" jint Java_de_xavaro_android_yihome_Camera_PPPP_1GetAPIVersion(
        JNIEnv *env,
        jclass type)
{
    //return PPPP_GetAPIVersion();

    return 0;
}

extern "C" jint Java_de_xavaro_android_yihome_Camera_PPPP_1ConnectOnlyLanSearch(
        JNIEnv *env,
        jclass type,
        jstring str)
{
    //return PPPP_ConnectOnlyLanSearch("TNPUSAC-663761-TLWPW");
    return 0;
}
