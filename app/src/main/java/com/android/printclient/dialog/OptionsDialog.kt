package com.android.printclient.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display
import com.android.printclient.R
import java.util.*

/**
 * Created by jianglei on 16/5/22.
 */
class OptionsDialog : Dialog {

    init {
        System.loadLibrary("options")
    }

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var printer: String? = null

    external fun getOptionGroups(name: String): List<String>

    constructor(context: Context, printer: String) : super(context) {
        this.printer = printer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_options)

        //set window size
        val display: Display = window.windowManager.defaultDisplay
        val param = window.attributes
        param.width = (display.width * SCREEN_DIALOG_RATE).toInt()
        this.window.attributes = param;

        var list = getOptionGroups(printer!!)

    }
}