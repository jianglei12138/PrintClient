#include <stdio.h>
#include <cups/cups.h>

int main() {
    int i;   /*for loop*/
    cups_dest_t *dests, *dest;
    int destnums;

    destnums = cupsGetDests(&dests);
    printf("printer number = %d\n", destnums);

    for (i = destnums, dest = dests; i > 0; i--, dest++) {
        if (dest->instance) {
            printf("printers: %s %s\n", dest->name, dest->instance);
        } else {
            printf("printers: %s", dest->name);
            cups_option_t *temp = dest->options;
            while (temp++) {
                printf("  %s = %s  ", temp->name, temp->value);
            }
            printf("\n");
        }
    }
    return 0;
}