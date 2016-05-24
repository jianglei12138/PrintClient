package com.android.printclient.objects

import java.util.*

/**
 * Created by jianglei on 16/5/24.
 */
class Option {
    var key: String? = null
    var text: String? = null
    var ui: String? = null
    var section: String? = null
    var order: Int = 0
    var choice: String? = null
    var items: List<Item> = ArrayList()
}