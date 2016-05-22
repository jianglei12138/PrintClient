package com.android.printclient.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.printclient.R


/**
 * Created by jianglei on 16/5/22.
 */
class PrograssDialog : Dialog {

    companion object {
        val SCREEN_DIALOG_RATE = 0.8
    }

    var text: String? = null

    constructor(text: String, context: Context) : super(context) {
        this.text = text
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)

        val display: Display = window.windowManager.defaultDisplay
        val param = window.attributes
        param.width = (display.width * SCREEN_DIALOG_RATE).toInt()
        this.window.attributes = param;

        var progressbarTextView = findViewById(R.id.progress_textView) as TextView
        progressbarTextView.text = text
    }
}