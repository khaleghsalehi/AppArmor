#include <jni.h>


JNIEXPORT jstring JNICALL
Java_apparmor_sdk_signverification_IronFoxSDK_getEncKey1(JNIEnv *env,
                                                        jobject this) {



        jstring key = "abcdefghijklmnop";
        jstring result = (*env)->NewStringUTF(env, key);
        return result;


}
