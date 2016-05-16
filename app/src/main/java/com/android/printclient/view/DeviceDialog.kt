package com.android.printclient.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.widget.*
import com.android.printclient.R
import com.android.printclient.utility.Translate
import java.util.*

/**
 * Created by jianglei on 16/5/14.
 */
class DeviceDialog : Dialog {

    var tabTitle = arrayOf("add_tab", "detail_tab", "ppd_tab")
    var currentTab = 0
    var startIndex = 0

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var uri: String? = null

    var tabhost: TabHost? = null

    constructor(context: Context, uri: String) : super(context) {
        this.uri = uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog_device)

        val display: Display = window.windowManager.defaultDisplay
        val param = window.attributes
        param.width = (display.width * SCREEN_DIALOG_RATE).toInt()
        this.window.attributes = param;


        //for tabhost
        tabhost = findViewById(R.id.dialog_tabHost) as TabHost
        tabhost!!.setup()

        LayoutInflater.from(context).inflate(R.layout.tab_addprinter, tabhost!!.tabContentView)
        LayoutInflater.from(context).inflate(R.layout.tab_adddetail, tabhost!!.tabContentView)
        LayoutInflater.from(context).inflate(R.layout.tab_addppd, tabhost!!.tabContentView)


        tabhost!!.addTab(tabhost!!.newTabSpec(tabTitle[0]).setIndicator("add_tab").setContent(R.id.add_tab))
        tabhost!!.addTab(tabhost!!.newTabSpec(tabTitle[1]).setIndicator("detail_tab").setContent(R.id.detail_tab))
        tabhost!!.addTab(tabhost!!.newTabSpec(tabTitle[2]).setIndicator("ppd_tab").setContent(R.id.ppd_tab))

        //var titleTextView: TextView = findViewById(R.id.title_textView) as TextView
        var nextTextView: TextView = findViewById(R.id.next_textView) as TextView
        var beforeTextView: TextView = findViewById(R.id.before_textView) as TextView

        initAddTab()
        initDetailTab()
        initPpdTab()

        nextTextView.setOnClickListener({ view -> tabhost!!.setCurrentTabByTag(tabTitle[(++currentTab) % 3]) })
        beforeTextView.setOnClickListener({ view -> tabhost!!.setCurrentTabByTag(tabTitle[(--currentTab) % 3]) })

    }

    private fun initPpdTab() {

    }

    private fun initDetailTab() {

    }

    private fun initAddTab() {
        var uriEditText = findViewById(R.id.uri_editText) as android.support.v7.widget.AppCompatEditText
        uriEditText.hint = uri
    }



    fun setFirstTab(position: Int) {
        tabhost!!.currentTab = position
        startIndex = position

    }
}