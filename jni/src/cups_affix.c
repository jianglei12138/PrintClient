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

JNIEXPORT void JNICALL Java_com_android_printclient_fragment_AddFragment_getDevices
        (JNIEnv *env, jobject jthis, jobject callback_listener) {

    http_t *http;
    callback = callback_listener;

    http = gethttp_t();
    if (!http)
        return;

    //callback
    callback_class = (*env)->FindClass(env,"com/android/printclient/fragment/AddFragment$OnFoundDeviceListener");
    callback_onfound = (*env)->GetMethodID(env, callback_class, "onFound",
                                           "(Lcom/android/printclient/objects/Device;)V");
    //devices object
    jclass device_class = (*env)->FindClass(env, "com/android/printclient/objects/Device");
    jmethodID device_init = (*env)->GetMethodID(env, device_class, "<init>", "()V");
    device = (*env)->NewObject(env, device_class, device_init, "");
    device_java_class = (*env)->GetFieldID(env, device_class, "deviceClass",
                                           "Ljava/lang/String;");
    device_java_id = (*env)->GetFieldID(env, device_class, "deviceId",
                                        "Ljava/lang/String;");
    device_java_info = (*env)->GetFieldID(env, device_class, "deviceInfo",
                                          "Ljava/lang/String;");
    device_java_makemodel = (*env)->GetFieldID(env, device_class, "deviceMakeModel",
                                               "Ljava/lang/String;");
    device_java_uri = (*env)->GetFieldID(env, device_class, "deviceUri",
                                         "Ljava/lang/String;");
    device_java_location = (*env)->GetFieldID(env, device_class, "deviceLocaton",
                                              "Ljava/lang/String;");


    ipp_status_t state = cupsGetDevices(http, 5, CUPS_INCLUDE_ALL, CUPS_EXCLUDE_NONE,
                   (cups_device_cb_t) found_printer_callback, (void *) env);


    if (state != IPP_OK){
        __android_log_print(ANDROID_LOG_ERROR,"JNIEnv","no permission!");
        return;
    }

    jmethodID callback_onfinish = (*env)->GetMethodID(env, callback_class, "onFinish", "()V");
    (*env)->CallVoidMethod(env, callback, callback_onfinish,"");
}
