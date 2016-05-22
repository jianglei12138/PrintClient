package com.android.printclient.utility

import java.io.InputStream
import java.io.OutputStream

/**
 * Created by jianglei on 16/5/21.
 */

object FileUtil {

    fun copyFile(inputStream: InputStream, outputStream: OutputStream) {
        inputStream.use {
            input ->
            outputStream.use {
                output ->
                input.copyTo(output)
            }

        }
    }
}
