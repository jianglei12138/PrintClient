//
// Created by 姜雷 on 16/5/22.
//

#include <cups/cups.h>
#include <stdio.h>

int main() {
    http_t *http = httpConnect2(cupsServer(), ippPort(), NULL, AF_UNSPEC, cupsEncryption(), 1, 30000, NULL);

    char *ppdfile = cupsGetServerPPD(http, "drv:///sample.drv/epson9.ppd");
    printf("%s", ppdfile);
}