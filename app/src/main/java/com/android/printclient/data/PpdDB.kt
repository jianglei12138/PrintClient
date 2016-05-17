package com.android.printclient.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.android.printclient.objects.Ppd

/**
 * Created by jianglei on 16/5/17.
 */
class PpdDB(context: Context) {

    init {
        System.loadLibrary("persistent")
    }

    external fun getPpds(): List<Ppd>
    external fun release()

    val dbHelper: DBHelper = DBHelper.instance.getInstance(context)

    fun insertPpds(ppds: List<Ppd>) = dbHelper.use {
        ppds.forEach {
            with(it) {
                val cv = ContentValues()
                cv.put("ppd_name", ppd_name);
                cv.put("ppd_natural_language", ppd_natural_language);
                cv.put("ppd_make", ppd_make);
                cv.put("ppd_make_and_model", ppd_make_and_model);
                cv.put("ppd_device_id", ppd_device_id);
                cv.put("ppd_product", ppd_product);
                cv.put("ppd_psversion", ppd_psversion);
                cv.put("ppd_type", ppd_type);
                cv.put("ppd_model_number", ppd_model_number);
                insert(PpdTable.NAME, null, cv)
            }
        }
    }

    fun initPpds() {
        insertPpds(getPpds())
        release()
    }
}