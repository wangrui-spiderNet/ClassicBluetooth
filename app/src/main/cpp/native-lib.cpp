#include <jni.h>
#include <string>
#include "LSFR.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_juplus_app_verification_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_juplus_app_utils_Utils_encryptData(JNIEnv *env, jclass thiz, jint init_key,
                                             jbyteArray l_data, jint length) {
    // TODO: implement stringFromJNI()

    uint8_t *olddata = (uint8_t *) env->GetByteArrayElements(l_data, 0);
    SmartLinkCore::CLFSR clfsr{};
    clfsr.Encrypt_Data(init_key, olddata, length);

    jbyteArray jarray = env->NewByteArray(length);

    //将byte数组转换为java String,并输出
    env->SetByteArrayRegion(jarray, 0, length, reinterpret_cast<const jbyte *>(olddata));
    return jarray;
}
