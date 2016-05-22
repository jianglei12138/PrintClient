package com.android.printclient.data

import android.content.ContentValues
import android.content.Context
import com.android.printclient.objects.Ppd
import com.android.printclient.utility.ListUtil
import org.jetbrains.anko.db.select
import java.util.*

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
        //dropTable(PpdTable.NAME, true)
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

    fun getAllMake(): ArrayList<String> = dbHelper.use {
        select(PpdTable.NAME, "ppd_make").exec {
            var make = ArrayList<String>()
            while (moveToNext())
                make.add(getString(getColumnIndex("ppd_make")))
            //do distinct
            var result = ArrayList<String>()
            make.forEach {
                if (!ListUtil.containsIgnoreCase(result, it)) {
                    result.add(it)
                }
            }
            let { result }
        }
    }

    fun getModelByMake(make: String): ArrayList<String> = dbHelper.use {
        select(PpdTable.NAME, "ppd_product").where("ppd_make = {ppd_make}", "ppd_make" to make).exec {
            var model = ArrayList<String>()
            while (moveToNext())
                model.add(getString(getColumnIndex("ppd_product")))
            let { model }
        }
    }

    fun getNameByMakeAndModel(make: String, model: String) = dbHelper.use {
        select(PpdTable.NAME, "ppd_name").where("ppd_make = {ppd_make} and ppd_product={ppd_product}", "ppd_make" to make, "ppd_product" to model).exec {
            var name: String? = null
            while (moveToNext())
                name = (getString(getColumnIndex("ppd_name")))
            let { name!! }
        }
    }
}