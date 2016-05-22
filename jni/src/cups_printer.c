//
// Created by 姜雷 on 16/5/20.
//

#include <jni.h>
#include <cups/http.h>
#include <cups/ipp.h>
#include <cups/cups.h>
#include <cups/ppd.h>
#include "cups_util.h"
#include "android/log.h"

JNIEXPORT jboolean JNICALL Java_com_android_printclient_dialog_DeviceDialog_addPrinter(
		JNIEnv *env, jobject jthis, jint type, jstring ppd,
		jstring printerString, jstring uriString) {

	http_t *http; /* http connection */
	char *printer; /* printer name    */
	ipp_t *request; /* request         */
	char *ppdfile; /* ppd file name   */
	char *printeruri; /* printer uri     */
	ppd_file_t *ppd_t; /* ppd_t           */
	char tempfile[1024]; /* tempfile for ppd*/
	cups_file_t *in, /* PPD file */
	*out; /* Temporary file */
	char uri[HTTP_MAX_URI];

	http = gethttp_t();

	if (http == NULL) {
		__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
				"cupsd has not started!");
		return 0;
	}

	printer = (char *) (*env)->GetStringUTFChars(env, printerString, 0);
	if (printer == NULL) {
		__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
				"printer name is null");
		return 0;
	}

	ppdfile = (char *) (*env)->GetStringUTFChars(env, ppd, 0);

	printeruri = (char *) (*env)->GetStringUTFChars(env, uriString, 0);

	request = ippNewRequest(CUPS_ADD_PRINTER);
	ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_URI, "device-uri", NULL,
			printeruri);

	__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
					"device-uri = %s", printeruri);

	httpAssembleURIf(HTTP_URI_CODING_ALL, uri, sizeof(uri), "ipp",
	NULL, "localhost", ippPort(), "/printers/%s", printer);


	__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
					"device-uri = %s", uri);
	ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_URI, "printer-uri", NULL,
			printeruri);
	ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_NAME,
			"requesting-user-name", NULL, cupsUser());
	ippAddInteger(request, IPP_TAG_PRINTER, IPP_TAG_ENUM, "printer-state",
			IPP_PSTATE_IDLE);
	ippAddBoolean(request, IPP_TAG_PRINTER, "printer-is-accepting-jobs", 1);

	if (type == 0) {
		if ((ppd_t = ppdOpenFile(ppdfile)) == NULL) {
			int linenum;
			ppd_status_t status = ppdLastError(&linenum);
			__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
					"Unable to open PPD \"%s\": %s on line %d.", ppdfile,
					ppdErrorString(status), linenum);
			return 0;
		}
//
//		if ((out = cupsTempFile2(tempfile, sizeof(tempfile))) == NULL) {
//			__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
//					"unable to create temporary file!");
//			ippDelete(request);
//			cupsFileClose(out);
//			unlink(ppdfile);
//			return 0;
//		}
//		if ((in = cupsFileOpen(ppdfile, "r")) == NULL) {
//			__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
//					"unable to open ppd file!");
//			ippDelete(request);
//			cupsFileClose(in);
//			unlink(ppdfile);
//			unlink(tempfile);
//			return 0;
//		}
//		cupsFileClose(in);
//		cupsFileClose(out);
//		ppdClose(ppd);

		ippDelete(cupsDoFileRequest(http, request, "/admin/", ppdfile));

		//clean
		unlink(ppdfile);
	} else {

		__android_log_print(ANDROID_LOG_ERROR, "JNIEnv", "init3");

		ippDelete(cupsDoRequest(http, request, "/admin/"));
	}

	if (cupsLastError() > IPP_STATUS_OK_CONFLICTING) {
		__android_log_print(ANDROID_LOG_ERROR, "JNIEnv",
				"cups add printer error. %s", cupsLastErrorString());
		return 0;
	}
	return 1;
}

JNIEXPORT jstring JNICALL Java_com_android_printclient_dialog_DeviceDialog_getServerPpd(
        JNIEnv *env, jobject jthis, jstring name) {
    setenv("TMPDIR", "/data/data/com.android.printclient/files", 1);
    http_t *http_t = gethttp_t();
    char *ppdfile = cupsGetServerPPD(http_t, (*env)->GetStringUTFChars(env, name, 0));
    return (*env)->NewStringUTF(env, ppdfile);
}