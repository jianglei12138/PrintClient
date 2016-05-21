package com.android.printclient.dialog

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatEditText
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.data.PpdDB
import com.android.printclient.utility.FileUtil

/**
 * Created by jianglei on 16/5/14.
 */
class DeviceDialog : Dialog {

    companion object {
        var ppdTextView: TextView? = null
        var ppd: String? = null
    }

    init {
        System.loadLibrary("printer")
    }

    external fun addPrinter(type: Int, ppd: String, printer: String, uri: String, isShared: Boolean, location: String, info: String):Boolean


    var tabTitle = arrayOf("add_tab", "detail_tab", "ppd_tab")
    var startIndex = 0

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var uri: String? = null

    var tabhost: TabHost? = null

    var nextTextView: TextView? = null
    var beforeTextView: TextView? = null

    var uriEditText: AppCompatEditText? = null

    var uriTextView: TextView? = null
    var nameEditText: AppCompatEditText? = null
    var descriptionEditText: AppCompatEditText? = null
    var locationEditText: AppCompatEditText? = null
    var shareCheckBox: CheckBox? = null

    var db = PpdDB(context)

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
        nextTextView = findViewById(R.id.next_textView) as TextView
        beforeTextView = findViewById(R.id.before_textView) as TextView

        //init
        beforeTextView!!.visibility = View.GONE
        tabhost!!.currentTab = startIndex
        beforeTextView!!.text = context.getString(R.string.before_step)

        initAddTab()
        initDetailTab()
        initPpdTab()

        nextTextView!!.setOnClickListener({
            view ->
            if (tabhost!!.currentTab == 0) {
                if (uriEditText!!.text.toString().contains(":/")) {
                    uriTextView!!.text = uriEditText!!.text
                } else {
                    Snackbar.make(view, context.getString(R.string.uri_format_error), Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            if (tabhost!!.currentTab == 1) {
                var namePrinter = nameEditText!!.text.toString()
                if (TextUtils.isEmpty(namePrinter) || namePrinter.length > 127 || namePrinter.contains(" ") || namePrinter.contains("/") || namePrinter.contains("#")) {
                    Snackbar.make(view, context.getString(R.string.ex_printer_name_attention), Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            if (tabhost!!.currentTab == tabTitle.size - 1) {
                //add printer,first check system provide ppd

                if (!TextUtils.isEmpty(uri)) {
                    var isShared = shareCheckBox!!.isChecked
                    var name = nameEditText!!.text.toString()
                    var uri = uriEditText!!.text.toString()
                    var ppd = "/data/data/com.android.printclient/files/PDFwriter.ppd"
                    var location = locationEditText!!.text.toString()
                    var info = descriptionEditText!!.text.toString()
                    var result = addPrinter(0,ppd,name,uri,isShared,location,info);
                    if(!result)
                        Snackbar.make(view, context.getString(R.string.add_printer_failed), Snackbar.LENGTH_LONG).show()
                }
                return@setOnClickListener
            }
            if (tabhost!!.currentTab == startIndex) {
                nextTextView!!.text = context.getString(R.string.next_step)
                beforeTextView!!.visibility = View.VISIBLE
            }
            if (tabhost!!.currentTab == tabTitle.size - 2) {
                nextTextView!!.text = context.getString(R.string.ok)
            }
            tabhost!!.currentTab = tabhost!!.currentTab + 1
        })
        beforeTextView!!.setOnClickListener({ view ->
            if (tabhost!!.currentTab == startIndex) {
                return@setOnClickListener
            } else if (tabhost!!.currentTab == tabTitle.size - 1) {
                nextTextView!!.text = context.getString(R.string.next_step)
            }
            if (tabhost!!.currentTab == startIndex + 1) {
                beforeTextView!!.visibility = View.INVISIBLE
            }
            tabhost!!.currentTab = tabhost!!.currentTab - 1
        })

        if (uri!!.contains(":/"))
            uriTextView!!.text = uri

    }

    private fun initPpdTab() {
        //select ppds from database
        var makeSpinner = findViewById(R.id.make_spinner) as Spinner
        var modelSpinner = findViewById(R.id.model_spinner) as Spinner

        var itemMake = db.getAllMake()
        itemMake.add(0, context.getString(R.string.printer_make))
        var adapterMake = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemMake.toTypedArray())
        makeSpinner.adapter = adapterMake

        var itemModel = arrayOf(context.getString(R.string.printer_model))
        val adapterModel = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemModel)
        modelSpinner.adapter = adapterModel

        makeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                modelSpinner.adapter = adapterModel
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    modelSpinner.adapter = adapterModel
                    return
                }
                val itemModelByMake = db.getModelByMake(itemMake[position])
                itemModelByMake.add(0, context.getString(R.string.printer_model))
                val adapterModelByMake = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemModelByMake.toTypedArray())
                modelSpinner.adapter = adapterModelByMake
            }
        }
        var chooseTextView = findViewById(R.id.choose_textView)
        chooseTextView.setOnClickListener { choosePpd() }
        ppdTextView = findViewById(R.id.ppd_TextView) as TextView?

    }

    private fun choosePpd() {
        var intent = Intent(Intent.ACTION_GET_CONTENT);
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        //set ownerActivity first
        ownerActivity.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.choose_ppd)), MainActivity.FILE_SELECT_CODE)
    }

    private fun initDetailTab() {
        nameEditText = findViewById(R.id.name_editText) as AppCompatEditText
        descriptionEditText = findViewById(R.id.description_editText) as AppCompatEditText
        locationEditText = findViewById(R.id.location_editText) as AppCompatEditText
        uriTextView = findViewById(R.id.uri_textView) as TextView
        shareCheckBox = findViewById(R.id.share_checkBox) as CheckBox
    }

    private fun initAddTab() {
        uriEditText = findViewById(R.id.uri_editText) as AppCompatEditText
        if (uri!!.contains(":/")) {
            uriEditText!!.hint = uri
            uriEditText!!.setText(uri)
        } else {
            uriEditText!!.hint = uri + "://"
            uriEditText!!.setText(uri + "://")
        }

    }

    fun setFirstTab(position: Int) {
        startIndex = position
        tabhost!!.currentTab = position
    }

    class ResultPPDFile : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            ppd = intent!!.getStringExtra("ppd")
            ppdTextView!!.text = FileUtil.getNameByPathWithSuffix(ppd!!)
        }

    }
}