#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "def.h"

/**
 *
 * @param env
 * @param this
 * @return AES passphrase
 */
int verifyme() {

    //todo check if this is a valid signed app(armor.c), then return true or false

    // this hash will be hard code in source
    char *hard_code_hash_val = "bc86863f0001f86187a0fddbf133afc6";

    // get get_hash form armor.c
    char *get_hash = "bc86863f0001f86187a0fddbf133afc6";
    if (strcmp(hard_code_hash_val, get_hash) == 0) {
        NSV_LOGI("ironfox app hash is valid");
        return 0;
    } else {
        NSV_LOGI("ironfox app hash is invalid");
        return 1;
    }
}

JNIEXPORT jstring JNICALL
Java_apparmor_sdk_signverification_IronFoxSDK_getEncKey(JNIEnv *env,
                                                        jobject this) {



    int verify = verifyme();

    if (verify == 0) {
        jstring key = "abcdefghijklmnop";
        jstring result = (*env)->NewStringUTF(env, key);
        return result;
    } else {
        NSV_LOGI("ironfox fake app detected, killing app");
        exit(1);
    }


}
