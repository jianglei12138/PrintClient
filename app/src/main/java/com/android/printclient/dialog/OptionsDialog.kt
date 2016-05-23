package com.android.printclient.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.widget.TabHost
import com.android.printclient.R

/**
 * Created by jianglei on 16/5/22.
 */
class OptionsDialog : Dialog {

    init {
        System.loadLibrary("options")
    }

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var printer: String? = null

    var tabHost: TabHost? = null

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


        tabHost = findViewById(R.id.dialog_tabHost) as TabHost?
        tabHost!!.setup()

        var list = getOptionGroups(printer!!)

        LayoutInflater.from(context).inflate(R.layout.tab_addprinter, tabHost!!.tabContentView)
        tabHost!!.addTab(tabHost!!.newTabSpec(list[0]).setIndicator(list[0]).setContent(R.id.add_tab))

        //LayoutInflater.from(context).inflate(R.layout.tab_addppd, tabHost!!.tabContentView)
        //tabHost!!.addTab(tabHost!!.newTabSpec(list[1]).setIndicator(list[1]).setContent(R.id.add_tab))



    }
}