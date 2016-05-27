/*
 * "$Id: lpadmin.c 12603 2015-05-06 01:42:51Z msweet $"
 *
 * "lpadmin" command for CUPS.
 *
 * Copyright 2007-2015 by Apple Inc.
 * Copyright 1997-2006 by Easy Software Products.
 *
 * These coded instructions, statements, and computer programs are the
 * property of Apple Inc. and are protected by Federal copyright
 * law.  Distribution and use rights are outlined in the file "LICENSE.txt"
 * which should have been included with this file.  If this file is
 * file is missing or damaged, see the license at "http://www.cups.org/".
 */

/*
 * Include necessary headers...
 */

#include <cups/cups.h>
#include <errno.h>
#include <stdio.h>
#include <cups/ppd.h>

static int                /* O - 0 on success, 1 on fail */
set_printer_options_add(
        http_t *http,        /* I - Server connection */
        char *printer,        /* I - Printer */
        char *file)        /* I - PPD file/interface script */
{
    ipp_t *request;        /* IPP Request */
    const char *ppdfile = NULL;        /* PPD filename */
    ppd_file_t *ppd;            /* PPD file */
    char uri[HTTP_MAX_URI],    /* URI for printer/class */
            tempfile[1024];        /* Temporary filename */
    cups_file_t *in,            /* PPD file */
            *out;            /* Temporary file */
    /*
     * Build a CUPS-Add-Modify-Printer or CUPS-Add-Modify-Class request,
     * which requires the following attributes:
     *
     *    attributes-charset
     *    attributes-natural-language
     *    printer-uri
     *    requesting-user-name
     *    other options
     */
    request = ippNewRequest(IPP_OP_CUPS_ADD_MODIFY_PRINTER);
    ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_URI, "device-uri", NULL, "pdfwriter:/");
    httpAssembleURIf(HTTP_URI_CODING_ALL, uri, sizeof(uri), "ipp", NULL, "localhost", ippPort(), "/printers/%s",
                     printer);
    ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_URI, "printer-uri", NULL, uri);
    ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_NAME, "requesting-user-name", NULL, cupsUser());
    ippAddString(request, IPP_TAG_OPERATION, IPP_TAG_NAME,
                 "requesting-user-name", NULL, cupsUser());
    ippAddInteger(request, IPP_TAG_PRINTER, IPP_TAG_ENUM, "printer-state",
                  IPP_PSTATE_IDLE);
    ippAddBoolean(request, IPP_TAG_PRINTER, "printer-is-accepting-jobs", 1);
    /*
     * Add the options...
     */


    if (file)
        ppdfile = file;

    //cupsEncodeOptions2(request, num_options, options, IPP_TAG_OPERATION);
    //cupsEncodeOptions2(request, num_options, options, IPP_TAG_PRINTER);

    if (ppdfile) {
        /*
         * Set default options in the PPD file...
         */

        if ((ppd = ppdOpenFile(ppdfile)) == NULL) {
            int linenum;    /* Line number of error */
            ppd_status_t status = ppdLastError(&linenum);
            /* Status code */

            printf(("lpadmin: Unable to open PPD \"%s\": %s on line %d."), ppdfile, ppdErrorString(status),
                   linenum);
        }

        //ppdMarkDefaults(ppd);
        //cupsMarkOptions(ppd, num_options, options);

        if ((out = cupsTempFile2(tempfile, sizeof(tempfile))) == NULL) {
            printf(NULL, ("lpadmin: Unable to create temporary file"));
            ippDelete(request);
            if (ppdfile != file)
                unlink(ppdfile);
            return (1);
        }

        if ((in = cupsFileOpen(ppdfile, "r")) == NULL) {
            printf(
                    ("lpadmin: Unable to open PPD file \"%s\" - %s"),
                    ppdfile, strerror(errno));
            ippDelete(request);
            if (ppdfile != file)
                unlink(ppdfile);
            cupsFileClose(out);
            unlink(tempfile);
            return (1);
        }
        cupsFileClose(in);
        cupsFileClose(out);
        ppdClose(ppd);

        /*
         * Do the request...
         */

        ippDelete(cupsDoFileRequest(http, request, "/admin/", file));

        /*
         * Clean up temp files... (TODO: catch signals in case we CTRL-C during
         * lpadmin)
         */

        if (ppdfile != file)
            unlink(ppdfile);
        unlink(tempfile);
    }
    else {
        /*
         * No PPD file - just set the options...
         */

        ippDelete(cupsDoRequest(http, request, "/admin/"));
    }

    /*
     * Check the response...
     */

    if (cupsLastError() > IPP_STATUS_OK_CONFLICTING) {
        printf(("%s: %s"), "lpadmin", cupsLastErrorString());

        return (1);
    }
    else
        return (0);
}


/*
 * 'main()' - Parse options and configure the scheduler.
 */

int
main(int argc,            /* I - Number of command-line arguments */
     char *argv[])        /* I - Command-line arguments */
{
    http_t *http = NULL;        /* Connection to server */
    char *printer = "Helloxxxx";    /* Destination printer */
//    int num_options = 0;    /* Number of options */
//    cups_option_t *options;    /* Options */
    char *file = "/sdcard/PDFwriter.ppd";        /* New PPD file/interface script */


//    num_options = cupsAddOption("device-uri", "pdfwriter:/",
//                                num_options, &options);


    http = httpConnect2(cupsServer(), ippPort(), NULL, AF_UNSPEC, cupsEncryption(), 1, 30000, NULL);
    if (http == NULL) {
        return (1);
    }

    if (printer == NULL) {
        printf("error");
        return (1);
    }

    if (set_printer_options_add(http, printer, file))
        return (1);


    httpClose(http);

    return (0);
}
