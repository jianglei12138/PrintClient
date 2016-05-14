#include <stdio.h>
#include <jni.h>
#include <cups/cups.h>
#include <android/log.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,"JNIEnv",__VA_ARGS__)

JNIEXPORT jobject JNICALL Java_com_android_printclient_MainActivity_getPrinters(
        JNIEnv *env, jobject jthis) {

    /*for java object Entry*/
    jclass entry = (*env)->FindClass(env,
                                     "com/android/printclient/objects/Entry");
    if (entry == NULL)
        return NULL;
    jmethodID init_entry = (*env)->GetMethodID(env, entry, "<init>", "()V");
    if (init_entry == NULL)
        return NULL;
    jfieldID name_entry = (*env)->GetFieldID(env, entry, "name",
                                             "Ljava/lang/String;");
    jfieldID value_entry = (*env)->GetFieldID(env, entry, "value",
                                              "Ljava/lang/String;");

    /*for java object Printer*/
    jclass printer = (*env)->FindClass(env,
                                       "com/android/printclient/objects/Printer");
    if (printer == NULL)
        return NULL;
    jmethodID init_printer = (*env)->GetMethodID(env, printer, "<init>", "()V");
    if (init_printer == NULL)
        return NULL;
    jfieldID name = (*env)->GetFieldID(env, printer, "name",
                                       "Ljava/lang/String;");
    jfieldID instance = (*env)->GetFieldID(env, printer, "instance",
                                           "Ljava/lang/String;");
    jfieldID isdefault = (*env)->GetFieldID(env, printer, "isdefault", "Z");
    jfieldID options = (*env)->GetFieldID(env, printer, "options",
                                          "Ljava/util/ArrayList;");

    /*for return printer list*/
    jclass printer_list = (*env)->FindClass(env, "java/util/ArrayList");
    if (printer_list == NULL)
        return NULL;
    jmethodID init_list = (*env)->GetMethodID(env, printer_list, "<init>",
                                              "()V");
    if (init_list == NULL)
        return NULL;
    jobject printerlist = (*env)->NewObject(env, printer_list, init_list, "");
    jmethodID printer_add = (*env)->GetMethodID(env, printer_list, "add",
                                                "(Ljava/lang/Object;)Z");

    /*for return options*/
    jclass options_list = (*env)->FindClass(env, "java/util/ArrayList");
    if (options_list == NULL)
        return NULL;
    jmethodID init_options = (*env)->GetMethodID(env, options_list, "<init>",
                                                 "()V");
    if (init_options == NULL)
        return NULL;
    jmethodID option_add = (*env)->GetMethodID(env, options_list, "add",
                                               "(Ljava/lang/Object;)Z");

    int i; /*for loop*/
    cups_dest_t *dests, *dest;
    int destnums;

    destnums = cupsGetDests(&dests);

    for (i = destnums, dest = dests; i > 0; i--, dest++) {
        //new a printer object
        jobject destprint = (*env)->NewObject(env, printer, init_printer, "");

        jstring destname = (*env)->NewStringUTF(env,
                                                dest->name ? dest->name : "");

        jstring destinstance = (*env)->NewStringUTF(env,
                                                    dest->instance ? dest->instance : "");
        jboolean destisdefault = dest->is_default;

        (*env)->SetObjectField(env, destprint, name, destname);

        (*env)->SetObjectField(env, destprint, instance, destinstance);

        (*env)->SetBooleanField(env, destprint, isdefault, destisdefault);

        jobject option_list = (*env)->NewObject(env, options_list, init_options,
                                                "");
        cups_option_t *temp = dest->options;
        int count = dest->num_options;
        int j = 0;
        while (j < count) {
            LOGD("current option: %d=%s", i, temp->name,temp->value);
            jobject item = (*env)->NewObject(env, entry, init_entry, "");
            jstring itemname = (*env)->NewStringUTF(env,
                                                    temp->name ? temp->name : "");

            jstring itemvalue = (*env)->NewStringUTF(env,
                                                     temp->value ? temp->value : "");
            (*env)->SetObjectField(env, item, name_entry, itemname);
            (*env)->SetObjectField(env, item, value_entry, itemvalue);
            (*env)->CallBooleanMethod(env, option_list, option_add, item);
            temp++;
            j++;
        }
        (*env)->SetObjectField(env, destprint, options, option_list);
        (*env)->CallBooleanMethod(env, printerlist, printer_add, destprint);
    }
    return printerlist;
}
