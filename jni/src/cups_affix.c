//
// Created by 姜雷 on 16/5/14.
//

#include "cups/cgi.h"

#include "jni.h"
#include "cups_util.h"
#include "android/log.h"

jobject callback;
jclass callback_class;
jmethodID callback_onfound;
jobject device;
jfieldID device_java_class, device_java_id, device_java_info, device_java_makemodel, device_java_uri, device_java_location;


static void found_printer_callback(
        const char *device_cls,             /* I - Class */
        const char *device_id,              /* I - 1284 device ID */
        const char *device_info,            /* I - Description */
        const char *device_make_and_model,  /* I - Make and model */
        const char *device_uri,             /* I - Device URI */
        const char *device_location,        /* I - Location */
        const void *title)                        /* I - callback */
{
    JNIEnv *env = title;

    __android_log_print(ANDROID_LOG_ERROR,"JNIEnv","id=%s class=%s info=%s makemodel=%s uri=%s location=%s\n", device_id, device_cls, device_info,
           device_make_and_model, device_uri, device_location);
    if (callback == NULL) return;
    //callback interface

    (*env)->SetObjectField(env, device, device_java_class, (*env)->NewStringUTF(env, device_cls));
    (*env)->SetObjectField(env, device, device_java_id, (*env)->NewStringUTF(env, device_id));
    (*env)->SetObjectField(env, device, device_java_info, (*env)->NewStringUTF(env, device_info));
    (*env)->SetObjectField(env, device, device_java_makemodel,
                           (*env)->NewStringUTF(env, device_make_and_model));
    (*env)->SetObjectField(env, device, device_java_uri, (*env)->NewStringUTF(env, device_uri));
    (*env)->SetObjectField(env, device, device_java_location,
                           (*env)->NewStringUTF(env, device_location));

    (*env)->CallVoidMethod(env, callback, callback_onfound,device);

};