//
// Created by 姜雷 on 16/5/15.
//

#include <jni.h>
#include "cups/cups.h"
#include "cups_util.h"

JNIEXPORT jboolean JNICALL Java_com_android_printclient_fragment_MainFragment_checkCupsd
        (JNIEnv *env, jobject jthis) {
    http_t *http = gethttp_t();
    if (!http){
        return (jboolean)0;
    }
    return (jboolean)1;
}