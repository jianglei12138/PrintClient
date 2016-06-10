package com.shockwave.pdfium;

import java.io.IOException;

public class PdfOpenException extends IOException {

    public PdfOpenException(String detailMessage) {
        super(detailMessage);
    }

}
