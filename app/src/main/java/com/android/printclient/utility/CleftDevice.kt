package com.android.printclient.utility

import android.content.Context
import com.android.printclient.R
import com.android.printclient.objects.Device

/**
 * Created by jianglei on 16/5/15.
 */
object CleftDevice {

    fun cleftDevice(device: Device, context: Context): String {

        if (!device.deviceClass.equals("network"))
            return context.getString(R.string.local_printer)
        else {
            if (device.deviceUri!!.contains(":"))
                return context.getString(R.string.network_printer)
            else
                return context.getString(R.string.other_printer)
        }
    }
}