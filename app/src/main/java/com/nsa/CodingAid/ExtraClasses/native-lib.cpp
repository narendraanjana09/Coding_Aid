#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_nsa_CodingAid_SignInActivity_baseUrlFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string baseURL = "Your_server_api_key";
    return env->NewStringUTF(baseURL.c_str());
}
