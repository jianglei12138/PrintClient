//
// Created by 姜雷 on 16/5/23.
//

#include <jni.h>
#include <cups/cups.h>
#include <cups/ppd.h>
#include <cups/cgi.h>
#include "cups_util.h"
#include "android/log.h"

static jobject options_instance;
//static jmethodID options_add;

JNIEXPORT jobject JNICALL Java_com_android_printclient_fragment_OptionFragment_getOptionGroups
        (JNIEnv *env, jobject jthis, jstring name) {

    jclass options = (*env)->FindClass(env, "java/util/ArrayList");
    if (options == NULL)
        return NULL;
    jmethodID options_init = (*env)->GetMethodID(env, options, "<init>",
                                                 "()V");
    if (options_init == NULL)
        return NULL;
    options_instance = (*env)->NewGlobalRef(env,(*env)->NewObject(env, options, options_init, ""));
    jmethodID options_add = (*env)->GetMethodID(env, options, "add",
                                                "(Ljava/lang/Object;)Z");


    ipp_t *request;          /* IPP request */
    ipp_t *response;         /* IPP response */
    char uri[1024];
    const char *printer = (*env)->GetStringUTFChars(env, name, NULL);
    http_t *http = gethttp_t();

    request = ippNewRequest(IPP_GET_PRINTER_ATTRIBUTES);

    httpAssembleURIf(HTTP_URI_CODING_ALL, uri, sizeof(uri), "ipp", NULL,
                     "localhost", ippPort(), "/printers/%s", printer);
    ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_URI, "printer-uri",
                 NULL, uri);

    response = cupsDoRequest(http, request, "/");

    struct ppd_file_s *ppd = NULL;
    ppd_group_t *group;            /* Option group */

    int i;
    const char *filename = cupsGetPPD2(http, printer);
    if (filename) {
        if ((ppd = ppdOpenFile(filename)) == NULL) {
            return NULL;
        }
    }

    i = 0;
    if (ppd) {
        for (group = ppd->groups; i < ppd->num_groups; i++, group++) {
                //cgiSetArray("GROUP", i, group->text);
                printf("group = %s\n", group->text);
                (*env)->CallBooleanMethod(env, options_instance, options_add,
                                          (*env)->NewStringUTF(env, group->text));
        }
    }


    if (ippFindAttribute(response, "job-sheets-supported", IPP_TAG_ZERO)) {
        //cgiSetArray("GROUP_ID", i, "CUPS_BANNERS");
        printf("group = %s\n", cgiText(("Banners")));
        (*env)->CallBooleanMethod(env, options_instance, options_add,
                                  (*env)->NewStringUTF(env, cgiText("Banners")));
        //cgiSetArray("GROUP", i++, cgiText(("Banners")));
    }

    if (ippFindAttribute(response, "printer-error-policy-supported",
                         IPP_TAG_ZERO) ||
        ippFindAttribute(response, "printer-op-policy-supported",
                         IPP_TAG_ZERO)) {
        //cgiSetArray("GROUP_ID", i, "CUPS_POLICIES");
        printf("group = %s\n", cgiText(("Policies")));
        (*env)->CallBooleanMethod(env, options_instance, options_add,
                                  (*env)->NewStringUTF(env, cgiText("Policies")));
        //cgiSetArray("GROUP", i++, cgiText(("Policies")));
    }

    if (ippFindAttribute(response, "port-monitor-supported",
                         IPP_TAG_NAME) != NULL) {
        //cgiSetArray("GROUP_ID", i, "CUPS_PORT_MONITOR");
        (*env)->CallBooleanMethod(env, options_instance, options_add,
                                  (*env)->NewStringUTF(env, cgiText("Port Monitor")));
        printf("group = %s\n", cgiText(("Port Monitor")));
        //cgiSetArray("GROUP", i, cgiText(("Port Monitor")));
    }
    return options_instance;
}

JNIEXPORT void JNICALL Java_com_android_printclient_fragment_OptionFragment_realease
        (JNIEnv *env, jobject jthis) {
    (*env)->DeleteGlobalRef(env,options_instance);
    //(*env)->DeleteGlobalRef(env,options_add);
}

char *getServerPpd(const char *name) {
    setenv("TMPDIR", "/data/data/com.android.printclient/files", 1);
    http_t *http_t = gethttp_t();
    char *ppdfile = (char *) cupsGetPPD2(http_t, name);
    return ppdfile;
}


JNIEXPORT jobject JNICALL Java_com_android_printclient_fragment_fragment_SubFragment_getGroup
        (JNIEnv *env, jobject jthis, jstring groupString, jstring printerString) {

    //set env
    //setenv("TMPDIR", "/data/data/com.android.printclient/files", 1);
    static char *uis[] = {"BOOLEAN", "PICKONE", "PICKMANY"};
    static char *sections[] = {"ANY", "DOCUMENT", "EXIT",
                               "JCL", "PAGE", "PROLOG"};

    char *filename;         /*ppd file path   */
    http_t *http;           /*http connection */
    const char *groupname;  /*group mame      */
    const char *printer;    /*printer name    */
    int i, j, m;            /*index of loop   */
    ppd_file_t *ppd;        /*ppd file        */
    ppd_group_t *group;     /*groups          */
    ppd_option_t *option;   /*option          */
    ppd_choice_t *choice;   /*option choice   */
    ipp_attribute_t *attr;  /* response attr  */
    ipp_t *request;         /* IPP request    */
    ipp_t *response;        /* IPP response   */
    char uri[1024];

    //init
    groupname = (*env)->GetStringUTFChars(env, groupString, 0);
    if (groupname == NULL) return NULL;

    __android_log_print(ANDROID_LOG_ERROR, "JNIEnv", "groupname = %s", groupname);

    printer = (*env)->GetStringUTFChars(env, printerString, 0);
    if (printer == NULL) return NULL;

    __android_log_print(ANDROID_LOG_ERROR, "JNIEnv", "printer = %s", printer);


    http = gethttp_t();
    if (http == NULL) return NULL;

    filename = getServerPpd(printer);
    if (filename == NULL) return NULL;

    ppd = ppdOpenFile(filename);
    //if (ppd == NULL) return NULL;


    request = ippNewRequest(IPP_GET_PRINTER_ATTRIBUTES);

    httpAssembleURIf(HTTP_URI_CODING_ALL, uri, sizeof(uri), "ipp", NULL,
                     "localhost", ippPort(), "/printers/%s", printer);
    ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_URI, "printer-uri",
                 NULL, uri);

    response = cupsDoRequest(http, request, "/");

    //List<Option> ----> Result
    jclass optionListClass = (*env)->FindClass(env, "java/util/ArrayList");
    if (optionListClass == NULL) return NULL;

    jmethodID optionListConstrution = (*env)->GetMethodID(env, optionListClass, "<init>", "()V");
    if (optionListConstrution == NULL) return NULL;

    jobject optionListInstance = (*env)->NewObject(env, optionListClass, optionListConstrution, "");
    jmethodID optionListAddFun = (*env)->GetMethodID(env, optionListClass, "add",
                                                     "(Ljava/lang/Object;)Z");



    //Option
    jclass optionClass = (*env)->FindClass(env, "com/android/printclient/objects/Option");
    if (optionClass == NULL) return NULL;
    jmethodID optionConstruction = (*env)->GetMethodID(env, optionClass, "<init>", "()V");
    if (optionConstruction == NULL)return NULL;
    jfieldID optionKey = (*env)->GetFieldID(env, optionClass, "key",
                                            "Ljava/lang/String;");
    jfieldID optionText = (*env)->GetFieldID(env, optionClass, "text",
                                             "Ljava/lang/String;");
    jfieldID optionUI = (*env)->GetFieldID(env, optionClass, "ui",
                                           "Ljava/lang/String;");
    jfieldID optionSection = (*env)->GetFieldID(env, optionClass, "section",
                                                "Ljava/lang/String;");
    jfieldID optionOrder = (*env)->GetFieldID(env, optionClass, "order", "I");
    jfieldID optionChoice = (*env)->GetFieldID(env, optionClass, "choice",
                                               "Ljava/lang/String;");
    jfieldID optionItems = (*env)->GetFieldID(env, optionClass, "items",
                                              "Ljava/util/List;");


    //for item list
    jclass itemListClass = (*env)->FindClass(env, "java/util/ArrayList");
    if (itemListClass == NULL) return NULL;

    jmethodID itemListConstruction = (*env)->GetMethodID(env, itemListClass, "<init>", "()V");
    if (itemListConstruction == NULL) return NULL;

    jmethodID itemListAddFun = (*env)->GetMethodID(env, itemListClass, "add",
                                                   "(Ljava/lang/Object;)Z");


    //for item object
    jclass itemClass = (*env)->FindClass(env, "com/android/printclient/objects/Item");
    if (itemClass == NULL) return NULL;
    jmethodID itemConstruction = (*env)->GetMethodID(env, itemClass, "<init>", "()V");
    if (itemConstruction == NULL)return NULL;
    jfieldID itemChoice = (*env)->GetFieldID(env, itemClass, "choice", "Ljava/lang/String;");
    jfieldID itemText = (*env)->GetFieldID(env, itemClass, "text", "Ljava/lang/String;");

    /*   banners   */
    if (strcasecmp(groupname, "Banners") == 0) if (
            (attr = ippFindAttribute(response, "job-sheets-supported",
                                     IPP_TAG_ZERO)) != NULL) {

        jobject optionInstance = (*env)->NewObject(env, optionClass, optionConstruction, "");
        //set
        (*env)->SetObjectField(env, optionInstance, optionKey,
                               (*env)->NewStringUTF(env, "job_sheets_start"));
        (*env)->SetObjectField(env, optionInstance, optionText,
                               (*env)->NewStringUTF(env, cgiText("Starting Banner")));
        jobject itemListInstance = (*env)->NewObject(env, itemListClass, itemListConstruction,
                                                     "");
        int k;
        for (k = 0; k < ippGetCount(attr); k++) {
            jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");
            (*env)->SetObjectField(env, itemInstance, itemChoice,
                                   (*env)->NewStringUTF(env, ippGetString(attr, k, NULL)));
            (*env)->SetObjectField(env, itemInstance, itemText,
                                   (*env)->NewStringUTF(env, ippGetString(attr, k, NULL)));
            (*env)->CallBooleanMethod(env, itemListInstance, itemListAddFun,
                                      itemInstance);

        }
        attr = ippFindAttribute(response, "job-sheets-supported", IPP_TAG_ZERO);
        (*env)->SetObjectField(env, optionInstance, optionChoice,
                               (*env)->NewStringUTF(env, attr != NULL ? ippGetString(attr, 0, NULL)
                                                                      : ""));
        (*env)->SetObjectField(env, optionInstance, optionItems, itemListInstance);
        (*env)->CallBooleanMethod(env, optionListInstance, optionListAddFun, optionInstance);


        attr = ippFindAttribute(response, "job-sheets-supported", IPP_TAG_ZERO);

        jobject optionInstance2 = (*env)->NewObject(env, optionClass, optionConstruction, "");
        //set
        (*env)->SetObjectField(env, optionInstance2, optionKey,
                               (*env)->NewStringUTF(env, "job_sheets_end"));
        (*env)->SetObjectField(env, optionInstance2, optionText,
                               (*env)->NewStringUTF(env, cgiText("Ending Banner")));
        jobject itemListInstance2 = (*env)->NewObject(env, itemListClass, itemListConstruction,
                                                      "");
        int j;
        for (j = 0; j < ippGetCount(attr); j++) {
            jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");
            (*env)->SetObjectField(env, itemInstance, itemChoice,
                                   (*env)->NewStringUTF(env, ippGetString(attr, j, NULL)));
            (*env)->SetObjectField(env, itemInstance, itemText,
                                   (*env)->NewStringUTF(env, ippGetString(attr, j, NULL)));
            (*env)->CallBooleanMethod(env, itemListInstance2, itemListAddFun,
                                      itemInstance);

        }
        attr = ippFindAttribute(response, "job-sheets-default", IPP_TAG_ZERO);
        (*env)->SetObjectField(env, optionInstance2, optionChoice,
                               (*env)->NewStringUTF(env, attr != NULL && ippGetCount(attr) > 1
                                                         ? ippGetString(attr, 1, NULL) : ""));
        (*env)->SetObjectField(env, optionInstance2, optionItems, itemListInstance2);
        (*env)->CallBooleanMethod(env, optionListInstance, optionListAddFun, optionInstance2);
        return optionListInstance;
    }
    /*   policy   */
    if (strcasecmp(groupname, "Policies") == 0) if (
            ippFindAttribute(response, "printer-error-policy-supported",
                             IPP_TAG_ZERO) ||
            ippFindAttribute(response, "printer-op-policy-supported",
                             IPP_TAG_ZERO)) {


        attr = ippFindAttribute(response, "printer-error-policy-supported",
                                IPP_TAG_ZERO);
        if (attr) {
            jobject optionInstance = (*env)->NewObject(env, optionClass, optionConstruction, "");
            //set
            (*env)->SetObjectField(env, optionInstance, optionKey,
                                   (*env)->NewStringUTF(env, "printer_error_policy"));
            (*env)->SetObjectField(env, optionInstance, optionText,
                                   (*env)->NewStringUTF(env, cgiText("Error Policy")));
            jobject itemListInstance = (*env)->NewObject(env, itemListClass, itemListConstruction,
                                                         "");
            int k;
            for (k = 0; k < ippGetCount(attr); k++) {
                jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");
                (*env)->SetObjectField(env, itemInstance, itemChoice,
                                       (*env)->NewStringUTF(env, ippGetString(attr, k, NULL)));
                (*env)->SetObjectField(env, itemInstance, itemText,
                                       (*env)->NewStringUTF(env, ippGetString(attr, k, NULL)));
                (*env)->CallBooleanMethod(env, itemListInstance, itemListAddFun,
                                          itemInstance);

            }
            attr = ippFindAttribute(response, "printer-error-policy", IPP_TAG_ZERO);
            (*env)->SetObjectField(env, optionInstance, optionChoice,
                                   (*env)->NewStringUTF(env,
                                                        attr == NULL ? "" : ippGetString(attr, 0,
                                                                                         NULL)));
            (*env)->SetObjectField(env, optionInstance, optionItems, itemListInstance);
            (*env)->CallBooleanMethod(env, optionListInstance, optionListAddFun, optionInstance);
        }

        attr = ippFindAttribute(response, "printer-op-policy-supported",
                                IPP_TAG_ZERO);
        if (attr) {
            jobject optionInstance2 = (*env)->NewObject(env, optionClass, optionConstruction, "");
            //set
            (*env)->SetObjectField(env, optionInstance2, optionKey,
                                   (*env)->NewStringUTF(env, "printer_op_policy"));
            (*env)->SetObjectField(env, optionInstance2, optionText,
                                   (*env)->NewStringUTF(env, cgiText("Operation Policy")));
            jobject itemListInstance2 = (*env)->NewObject(env, itemListClass, itemListConstruction,
                                                          "");
            int j;
            for (j = 0; j < ippGetCount(attr); j++) {
                jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");
                (*env)->SetObjectField(env, itemInstance, itemChoice,
                                       (*env)->NewStringUTF(env, ippGetString(attr, j, NULL)));
                (*env)->SetObjectField(env, itemInstance, itemText,
                                       (*env)->NewStringUTF(env, ippGetString(attr, j, NULL)));
                (*env)->CallBooleanMethod(env, itemListInstance2, itemListAddFun,
                                          itemInstance);
            }
            attr = ippFindAttribute(response, "printer-op-policy", IPP_TAG_ZERO);
            (*env)->SetObjectField(env, optionInstance2, optionChoice,
                                   (*env)->NewStringUTF(env, attr == NULL ?
                                                             "" : ippGetString(attr, 0, NULL)));
            (*env)->SetObjectField(env, optionInstance2, optionItems, itemListInstance2);
            (*env)->CallBooleanMethod(env, optionListInstance, optionListAddFun, optionInstance2);
        }
        return optionListInstance;
    }

    /*   monitor   */

    if (strcasecmp(groupname, "Port Monitor") == 0) if (
            (attr = ippFindAttribute(response, "port-monitor-supported",
                                     IPP_TAG_NAME)) != NULL && ippGetCount(attr) > 1) {
        jobject optionInstance = (*env)->NewObject(env, optionClass, optionConstruction, "");
        //set
        (*env)->SetObjectField(env, optionInstance, optionKey,
                               (*env)->NewStringUTF(env, "port_monitor"));
        (*env)->SetObjectField(env, optionInstance, optionText,
                               (*env)->NewStringUTF(env, cgiText("Port Monitor")));
        jobject itemListInstance = (*env)->NewObject(env, itemListClass, itemListConstruction,
                                                     "");

        int i;
        for (i = 0; i < ippGetCount(attr); i++) {
            jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");
            (*env)->SetObjectField(env, itemInstance, itemChoice,
                                   (*env)->NewStringUTF(env, ippGetString(attr, i, NULL)));
            (*env)->SetObjectField(env, itemInstance, itemText,
                                   (*env)->NewStringUTF(env, ippGetString(attr, i, NULL)));
            (*env)->CallBooleanMethod(env, itemListInstance, itemListAddFun,
                                      itemInstance);
        }

        attr = ippFindAttribute(response, "port-monitor", IPP_TAG_NAME);
        (*env)->SetObjectField(env, optionInstance, optionChoice,
                               (*env)->NewStringUTF(env,
                                                    attr ? ippGetString(attr, 0, NULL) : "none"));
        (*env)->SetObjectField(env, optionInstance, optionItems, itemListInstance);
        (*env)->CallBooleanMethod(env, optionListInstance, optionListAddFun, optionInstance);
        return optionListInstance;
    }

    /*   for group in ppd files  */
    for (i = 0, group = ppd->groups; i < ppd->num_groups; ++i, group++) {
        if (strcasecmp(group->text, groupname)) {
            continue;
        }
        for (j = 0, option = group->options; j < group->num_options; ++j, option++) {

            jobject optionInstance = (*env)->NewObject(env, optionClass, optionConstruction, "");

            //set
            (*env)->SetObjectField(env, optionInstance, optionKey,
                                   (*env)->NewStringUTF(env, option->keyword));
            (*env)->SetObjectField(env, optionInstance, optionText,
                                   (*env)->NewStringUTF(env, option->text));
            (*env)->SetObjectField(env, optionInstance, optionUI,
                                   (*env)->NewStringUTF(env, uis[option->ui]));
            (*env)->SetObjectField(env, optionInstance, optionSection,
                                   (*env)->NewStringUTF(env, sections[option->section]));
            (*env)->SetIntField(env, optionInstance, optionOrder, (jint) option->order);


            jobject itemListInstance = (*env)->NewObject(env, itemListClass, itemListConstruction,
                                                         "");

            if (strcmp(option->keyword, "PageSize") == 0 ||
                strcmp(option->keyword, "PageRegion") == 0) {
                for (m = option->num_choices, choice = option->choices; m > 0; m--, choice++) {
                    if (strcmp(option->defchoice, choice->choice) == 0)
                        (*env)->SetObjectField(env, optionInstance, optionChoice,
                                               (*env)->NewStringUTF(env, choice->text));
                    jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");

                    (*env)->SetObjectField(env, itemInstance, itemChoice,
                                           (*env)->NewStringUTF(env, choice->choice));
                    (*env)->SetObjectField(env, itemInstance, itemText,
                                           (*env)->NewStringUTF(env, choice->text));
                    (*env)->CallBooleanMethod(env, itemListInstance, itemListAddFun,
                                              itemInstance);
                }
            }
            else {
                for (m = option->num_choices, choice = option->choices; m > 0; m--, choice++) {
                    if (strcmp(option->defchoice, choice->choice) == 0)
                        (*env)->SetObjectField(env, optionInstance, optionChoice,
                                               (*env)->NewStringUTF(env, choice->text));
                    jobject itemInstance = (*env)->NewObject(env, itemClass, itemConstruction, "");

                    (*env)->SetObjectField(env, itemInstance, itemChoice,
                                           (*env)->NewStringUTF(env, choice->choice));
                    (*env)->SetObjectField(env, itemInstance, itemText,
                                           (*env)->NewStringUTF(env, choice->text));
                    (*env)->CallBooleanMethod(env, itemListInstance, itemListAddFun,
                                              itemInstance);
                }
            }

            (*env)->SetObjectField(env, optionInstance, optionItems, itemListInstance);
            (*env)->CallBooleanMethod(env, optionListInstance, optionListAddFun, optionInstance);
        }
    }
    return optionListInstance;
}






