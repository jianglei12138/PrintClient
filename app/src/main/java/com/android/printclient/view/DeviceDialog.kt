package com.android.printclient.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Display
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import com.android.printclient.R
import com.android.printclient.utility.Translate
import java.util.*

/**
 * Created by jianglei on 16/5/14.
 */
class DeviceDialog : Dialog {

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var mcontext: Context? = null
    var uri: String? = null

    constructor(context: Context, uri: String) : super(context) {
        this.mcontext = context
        this.uri = uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog_device)

        val display: Display = window.windowManager.defaultDisplay
        val param = window.attributes
        param.width = (display.width * SCREEN_DIALOG_RATE).toInt()
        this.window.attributes = param;

        var titleTextView: TextView = findViewById(R.id.title_textView) as TextView
        var certainTextView: TextView = findViewById(R.id.certain_textView) as TextView
        var sampleTextView: TextView = findViewById(R.id.sample_textView) as TextView
        var uriEditText = findViewById(R.id.uri_editText) as android.support.v7.widget.AppCompatEditText

        uriEditText.hint = uri

        certainTextView.setOnClickListener({ view -> dismiss() })
    }
}