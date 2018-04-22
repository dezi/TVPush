#include <jni.h>

extern "C" jbyteArray JNICALL Java_com_telink_crypto_AES_decryptCmd
        (JNIEnv *env, jclass self, jbyteArray crypt, jbyteArray nonce, jbyteArray key);

extern "C" jbyteArray JNICALL Java_com_telink_crypto_AES_encryptCmd
        (JNIEnv *env, jclass self, jbyteArray plain, jbyteArray nonce, jbyteArray key);

extern "C" JNIEXPORT jbyteArray JNICALL Java_de_xavaro_android_awx_comm_AWXAES_decrypt
        (JNIEnv *env, jclass self, jbyteArray key, jbyteArray nonce, jbyteArray crypt)
{
    return Java_com_telink_crypto_AES_decryptCmd(env, self, crypt, nonce, key);
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_de_xavaro_android_awx_comm_AWXAES_encrypt
        (JNIEnv *env, jclass self, jbyteArray key, jbyteArray nonce, jbyteArray plain)
{
    return Java_com_telink_crypto_AES_encryptCmd(env, self, plain, nonce, key);
}


