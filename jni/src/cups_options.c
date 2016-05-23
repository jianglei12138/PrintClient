//
// Created by 姜雷 on 16/5/23.
//

#include <jni.h>
#include <cups/cups.h>
#include <cups/ppd.h>
#include <cups/cgi.h>
#include "cups_util.h"

JNIEXPORT jobject JNICALL Java_com_android_printclient_dialog_OptionsDialog_getOptionGroups
        (JNIEnv *env, jobject jthis, jstring name) {

    jclass options = (*env)->FindClass(env, "java/util/ArrayList");
    if (options == NULL)
        return NULL;
    jmethodID options_init = (*env)->GetMethodID(env, options, "<init>",
                                                 "()V");
    if (options_init == NULL)
        return NULL;
    jobject options_instance = (*env)->NewObject(env, options, options_init, "");
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
            //cgiSetArray("GROUP_ID", i, group->name);
            //printf("group_id = %s", group->name);
            if (!strcmp(group->name, "InstallableOptions")) {
                //cgiSetArray("GROUP", i, cgiText("Options Installed"));
                printf("group = %s\n", cgiText("Options Installed"));
                (*env)->CallBooleanMethod(env, options_instance, options_add,
                                         cgiText("Options Installed"));
            }
            else {
                //cgiSetArray("GROUP", i, group->text);
                printf("group = %s\n", group->text);
                (*env)->CallBooleanMethod(env, options_instance, options_add,
                                         (*env)->NewStringUTF(env, group->text));
            }
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
