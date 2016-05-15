//
// Created by 姜雷 on 16/5/15.
//

#include <cups/cups.h>
#include "cups_util.h"

http_t *gethttp_t(void) {
    return httpConnectEncrypt(cupsServer(), ippPort(), cupsEncryption());
}
