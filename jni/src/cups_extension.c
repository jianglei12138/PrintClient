#include <stdio.h>
#include <jni.h>
#include <cups/cups.h>
#include <android/log.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,"JNIEnv",__VA_ARGS__)

JNIEXPORT jobject JNICALL Java_com_android_printclient_fragment_MainFragment_getPrinters(
        JNIEnv *env, jobject jthis) {

    jclass printer;             /*a printer object class   */
    jclass client_printer_list; /*result printer list      */
    jmethodID init_printer;     /*printer class constructor*/
    jmethodID init_printer_list;/*printer list constructor */

    /*for java object Printer*/
    printer = (*env)->FindClass(env, "com/android/printclient/objects/Printer");
    if (printer == NULL)
        return NULL;
    init_printer = (*env)->GetMethodID(env, printer, "<init>", "()V");
    if (init_printer == NULL)
        return NULL;
    jfieldID printer_name = (*env)->GetFieldID(env, printer, "name", "Ljava/lang/String;");
    jfieldID printer_instance = (*env)->GetFieldID(env, printer, "instance", "Ljava/lang/String;");
    jfieldID printer_default = (*env)->GetFieldID(env, printer, "isdefault", "Z");
    jfieldID printer_uri = (*env)->GetFieldID(env, printer, "deviceuri", "Ljava/lang/String;");

    /*for return printer list*/
    client_printer_list = (*env)->FindClass(env, "java/util/ArrayList");
    if (client_printer_list == NULL)
        return NULL;
     init_printer_list = (*env)->GetMethodID(env, client_printer_list, "<init>",
                                                      "()V");
    if (init_printer_list == NULL)
        return NULL;
    jobject client_printer_list_instance = (*env)->NewObject(env, client_printer_list,
                                                             init_printer_list, "");
    jmethodID client_printer_list_add = (*env)->GetMethodID(env, client_printer_list, "add",
                                                            "(Ljava/lang/Object;)Z");

    int i;                      /*for loop     */
    cups_dest_t *dests, *dest;  /*printers     */
    int dest_nums;              /*printer nums */

    dest_nums = cupsGetDests(&dests);

    for (i = dest_nums, dest = dests; i > 0; i--, dest++) {
        //new a printer object
        jobject dest_print = (*env)->NewObject(env, printer, init_printer, "");

        //convert char* to jstring as parameter
        jstring dest_name = (*env)->NewStringUTF(env, dest->name ? dest->name : "");
        jstring dest_instance = (*env)->NewStringUTF(env, dest->instance ? dest->instance : "");
        jboolean dest_default = (jboolean) dest->is_default;
        cups_option_t *temp = dest->options;
        const char *dest_device_uri = cupsGetOption("device-uri", dest->num_options, dest->options);
        jstring dest_uri = (*env)->NewStringUTF(env, dest_device_uri);

        //set printer var
        (*env)->SetObjectField(env, dest_print, printer_name, dest_name);
        (*env)->SetObjectField(env, dest_print, printer_instance, dest_instance);
        (*env)->SetBooleanField(env, dest_print, printer_default, dest_default);
        (*env)->SetObjectField(env, dest_print, printer_uri, dest_uri);

        //add printer to list
        (*env)->CallBooleanMethod(env, client_printer_list_instance, client_printer_list_add, dest_print);
    }
    return client_printer_list_instance;
}
