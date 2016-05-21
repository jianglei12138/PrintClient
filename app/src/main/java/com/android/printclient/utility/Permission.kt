package com.android.printclient.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

/**
 * Created by jianglei on 16/5/21.
 */
object Permission {
    fun requestPermission(permission: Array<String>, context: Context, activity: Activity, requestNumber: Int) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestNumber)
        }
    }

    fun checkRequestPermission(grant: Int): Boolean {
        return grant == PackageManager.PERMISSION_GRANTED
    }
}