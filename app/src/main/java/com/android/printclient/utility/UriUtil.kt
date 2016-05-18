package com.android.printclient.utility

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns

/**
 * Created by jianglei on 16/5/18.
 */
object UriUtil {
    fun uri2Name(uri: Uri, context: Context): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun uri2Path(uri: Uri, context: Context): String? {
        var path: String? = null
        val scheme = uri.scheme
        if (scheme == "file") {
            path = uri.lastPathSegment
        } else if (scheme == "content") {
            val proj = arrayOf(MediaStore.Images.Media.TITLE)
            val cursor = context.contentResolver.query(uri, proj, null, null, null)
            if (cursor != null && cursor.count != 0) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
                cursor.moveToFirst()
                path = cursor.getString(columnIndex)
            }
            cursor?.close()
        }
        return path
    }
}
