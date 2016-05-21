//
// Created by 姜雷 on 16/5/17.
//
#include "jni.h"
#include "cups/cgi.h"
#include "cups_util.h"

ipp_t *response;

JNIEXPORT jobject JNICALL Java_com_android_printclient_data_PpdDB_getPpds
        (JNIEnv *env, jobject jthis) {

    char *ppdString[1024];
    int count = 0;
    jclass ppd_list = (*env)->FindClass(env, "java/util/ArrayList");
    if (ppd_list == NULL)
        return NULL;
    jmethodID ppd_init = (*env)->GetMethodID(env, ppd_list, "<init>",
                                             "()V");
    if (ppd_init == NULL)
        return NULL;
    jobject ppd_list_instance = (*env)->NewObject(env, ppd_list, ppd_init, "");
    jmethodID ppd_add = (*env)->GetMethodID(env, ppd_list, "add", "(Ljava/lang/Object;)Z");


    ipp_t *request = ippNewRequest(CUPS_GET_PPDS);
    response = cupsDoRequest(gethttp_t(), request, "/");


    ipp_attribute_t *attr = ippFirstAttribute(response);//attributes-charset
    attr = ippNextAttribute(response);  //attributes-natural-language

    jclass ppd = (*env)->FindClass(env, "com/android/printclient/objects/Ppd");
    jmethodID init_ppd = (*env)->GetMethodID(env, ppd, "<init>", "()V");
    jfieldID ppd_name = (*env)->GetFieldID(env, ppd, "ppd_name", "Ljava/lang/String;");
    jfieldID ppd_natural_language = (*env)->GetFieldID(env, ppd, "ppd_natural_language",
                                                       "Ljava/lang/String;");
    jfieldID ppd_make = (*env)->GetFieldID(env, ppd, "ppd_make", "Ljava/lang/String;");
    jfieldID ppd_make_and_model = (*env)->GetFieldID(env, ppd, "ppd_make_and_model",
                                                     "Ljava/lang/String;");
    jfieldID ppd_device_id = (*env)->GetFieldID(env, ppd, "ppd_device_id", "Ljava/lang/String;");
    jfieldID ppd_product = (*env)->GetFieldID(env, ppd, "ppd_product", "Ljava/lang/String;");
    jfieldID ppd_psversion = (*env)->GetFieldID(env, ppd, "ppd_psversion", "Ljava/lang/String;");
    jfieldID ppd_type = (*env)->GetFieldID(env, ppd, "ppd_type", "Ljava/lang/String;");
    jfieldID ppd_model_number = (*env)->GetFieldID(env, ppd, "ppd_model_number",
                                                   "Ljava/lang/String;");

    while (attr) {
        attr = ippNextAttribute(response);
        if (ippGetName(attr) != NULL) {
            const char *value = (char *) ippGetString(attr, 0, NULL);;
            ppdString[count++] = (char *) value;
        }
    }
    //ppd-name ppd-natural-language ppd-make ppd-make-and-model ppd-device-id ppd-product ppd-psversion ppd-type ppd-model-number

    int i;
    for (i = 0; i < count / 9; ++i) {
        jobject ppd_instance = (*env)->NewObject(env, ppd, init_ppd, "");
        (*env)->SetObjectField(env,ppd_instance,ppd_name,(*env)->NewStringUTF(env,ppdString[i*9+0]));
        (*env)->SetObjectField(env,ppd_instance,ppd_natural_language,(*env)->NewStringUTF(env,ppdString[i*9+1]));
        (*env)->SetObjectField(env,ppd_instance,ppd_make,(*env)->NewStringUTF(env,ppdString[i*9+2]));
        (*env)->SetObjectField(env,ppd_instance,ppd_make_and_model,(*env)->NewStringUTF(env,ppdString[i*9+3]));
        (*env)->SetObjectField(env,ppd_instance,ppd_device_id,(*env)->NewStringUTF(env,ppdString[i*9+4]));
        (*env)->SetObjectField(env,ppd_instance,ppd_product,(*env)->NewStringUTF(env,ppdString[i*9+5]));
        (*env)->SetObjectField(env,ppd_instance,ppd_psversion,(*env)->NewStringUTF(env,ppdString[i*9+6]));
        (*env)->SetObjectField(env,ppd_instance,ppd_type,(*env)->NewStringUTF(env,ppdString[i*9+7]));
        (*env)->SetObjectField(env,ppd_instance,ppd_model_number,(*env)->NewStringUTF(env,ppdString[i*9+8]));
        (*env)->CallBooleanMethod(env, ppd_list_instance, ppd_add, ppd_instance);
    }
    return ppd_list_instance;
}

JNIEXPORT void JNICALL Java_com_android_printclient_data_PpdDB_release
        (JNIEnv *env, jobject jthis) {
    ippDelete(response);
}
