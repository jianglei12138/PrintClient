#include <stdio.h>
#include <jni.h>
#include <cups/cups.h>
#include <android/log.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,"JNIEnv",__VA_ARGS__)

JNIEXPORT jobject JNICALL Java_com_android_printclient_fragment_fragment_SubMainFragment_getPrinters(
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

JNIEXPORT jobject JNICALL Java_com_android_printclient_fragment_fragment_SubMainFragment_getAttributePrinter(
        JNIEnv *env, jobject jthis, jstring name, jstring instance) {

    int i; /*for loop       */
    cups_dest_t *dests; /*printers       */
    cups_dest_t *dest; /*result printer */
    int dest_num; /*printer nums   */
    jclass map; /*return map     */
    jmethodID init_map; /*constructor map*/

    dest_num = cupsGetDests(&dests);
    const char *dest_name = (*env)->GetStringUTFChars(env, name, 0);

    char *dest_instance;
    if (instance == NULL)
        dest_instance = NULL;
    else
        dest_instance = (char *) (*env)->GetStringUTFChars(env, instance, 0);

    dest = cupsGetDest(dest_name, dest_instance, dest_num, dests);
    if (dest == NULL)
        return NULL ;
    cups_option_t *temp = dest->options;
    int count = dest->num_options;

    //map
    map = (*env)->FindClass(env, "java/util/HashMap");
    if (map == NULL)
        return NULL ;
    init_map = (*env)->GetMethodID(env, map, "<init>", "()V");
    if (init_map == NULL)
        return NULL ;
    jobject client_map_instance = (*env)->NewObject(env, map, init_map, "");
    jmethodID client_map_put = (*env)->GetMethodID(env, map, "put",
                                                   "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    for (i = 0; i < count; i++) {
        jstring itemname = (*env)->NewStringUTF(env,
                                                temp->name ? temp->name : "");
        jstring itemvalue = (*env)->NewStringUTF(env,
                                                 temp->value ? temp->value : "");
        (*env)->CallObjectMethod(env, client_map_instance, client_map_put,
                                 itemname, itemvalue);
        temp++;
    }
    return client_map_instance;
}
