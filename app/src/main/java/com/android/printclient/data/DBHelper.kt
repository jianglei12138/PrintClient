package com.android.printclient.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.android.printclient.objects.Ppd
import org.jetbrains.anko.db.*

/**
 * Created by jianglei on 16/5/17.
 */
class DBHelper(context: Context) : ManagedSQLiteOpenHelper(context, "_cups_", null, 1) {

    object instance {
        private var instance: DBHelper? = null

        fun getInstance(ctx: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(ctx)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.createTable(PpdTable.NAME, true,
                PpdTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                PpdTable.PPD_MAKE to TEXT,
                PpdTable.PPD_DEVICE_ID to TEXT,
                PpdTable.PPD_LANGUAGE to TEXT,
                PpdTable.PPD_MODEL_NUMBER to TEXT,
                PpdTable.PPD_PRODUCT to TEXT,
                PpdTable.PPD_MAKE_AND_MODEL to TEXT,
                PpdTable.PPD_PSVERSION to TEXT,
                PpdTable.PPD_TYPE to TEXT,
                PpdTable.PPD_NAME to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.dropTable(PpdTable.NAME)
        onCreate(db)
    }
}