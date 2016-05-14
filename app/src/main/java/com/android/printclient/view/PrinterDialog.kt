package com.android.printclient.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import com.android.printclient.R
import java.util.*

/**
 * Created by jianglei on 16/5/14.
 */
class PrinterDialog : Dialog {

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var mcontext: Context? = null
    var attributes: Map<String, String>? = null

    constructor(context: Context, attributes: Map<String, String>) : super(context) {
        this.mcontext = context
        this.attributes = attributes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog_printer)

        val display: Display = window.windowManager.defaultDisplay
        val param = window.attributes
        param.width = (display.width * SCREEN_DIALOG_RATE).toInt()
        this.window.attributes = param;

        var titleTextView: TextView = findViewById(R.id.title_textView) as TextView
        var attributeListView: ListView = findViewById(R.id.attribute_listView) as ListView
        var certainTextView: TextView = findViewById(R.id.certain_textView) as TextView

        var data = ArrayList<Map<String, String>>()

        for (item in attributes!!) {
            var map: HashMap<String, String> = HashMap()
            map.put("key", item.key)
            map.put("value", item.value)
            data.add(map)
        }

        var adapter: SimpleAdapter = SimpleAdapter(
                mcontext,
                data,
                R.layout.listview_attributes,
                arrayOf("key", "value"),
                intArrayOf(R.id.key_textView, R.id.value_textView)
        )
        attributeListView.adapter = adapter
    }
}