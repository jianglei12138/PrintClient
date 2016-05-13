package com.android.printclient.objects

import java.util.*

/**
 * Created by jianglei on 16/5/13.
 */
class Printer {
    var name: String = ""
    var instance: String = ""
    var isdefault: Boolean = false
    var options: ArrayList<Entry> = ArrayList<Entry>()
}
