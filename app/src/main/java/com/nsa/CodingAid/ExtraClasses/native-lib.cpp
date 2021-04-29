#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_nsa_CodingAid_SignInActivity_baseUrlFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string baseURL = "1018363210699-vcgp8ophisr9hjfn1qmv8meqqv9q478o.apps.googleusercontent.com";
    return env->NewStringUTF(baseURL.c_str());
}
