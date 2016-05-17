package com.android.printclient.utility

/**
 * Created by jianglei on 16/5/17.
 */
object ListUtil {
    fun containsIgnoreCase(array: List<Any>, o: Any): Boolean {
        val a = array
        val s = array.size

        for (i in 0..s - 1) {
            if (o.equals(a[i])) {
                return true
            }
        }
        return false
    }
}