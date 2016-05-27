#include <stdio.h>
#include <cups/cgi.h>


static void
choose_device_cb(
        const char *device_class,        /* I - Class */
        const char *device_id,        /* I - 1284 device ID */
        const char *device_info,        /* I - Description */
        const char *device_make_and_model,    /* I - Make and model */
        const char *device_uri,        /* I - Device URI */
        const char *device_location,    /* I - Location */
        void *title)            /* I - Page title */
{
    printf("id=%s class=%s info=%s makemodel=%s uri=%s location=%s\n", "", device_class, device_info,
           device_make_and_model, device_uri, device_location);
    printf("%s\n",title);
}



int main() {

    http_t *http;

    http = httpConnectEncrypt(cupsServer(), ippPort(), cupsEncryption());

    if (cupsGetDevices(http, 5, CUPS_INCLUDE_ALL, CUPS_EXCLUDE_NONE,
                       (cups_device_cb_t)choose_device_cb,
                       (void *) "Hello World") == IPP_OK) {
        fputs("DEBUG: Got device list!\n", stderr);
    }
}


