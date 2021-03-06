package com.android.printclient.dialog

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.data.PpdDB
import com.android.printclient.fragment.AddFragment
import com.android.printclient.utility.FileUtil
import com.android.printclient.utility.ThreadUtil
import java.io.File
import java.util.*

/**
 * Created by jianglei on 16/5/14.
 */
class DeviceDialog : Dialog {

    companion object {
        var ppdTextView: TextView? = null
        var ppd: Uri? = null
    }

    init {
        System.loadLibrary("printer")
    }

    external fun addPrinter(type: Int, ppd: String, printer: String, uri: String, isShared: Boolean, location: String, info: String): Boolean
    external fun getServerPpd(name: String): String


    var tabTitle = arrayOf("add_tab", "detail_tab", "ppd_tab")
    var startIndex = 0

    val SCREEN_DIALOG_RATE: Float = 0.96.toFloat()
    var uri: String? = null

    var tabhost: TabHost? = null

    var nextTextView: TextView? = null
    var beforeTextView: TextView? = null

    var uriEditText: EditText? = null

    var uriTextView: TextView? = null
    var nameEditText: EditText? = null
    var descriptionEditText: EditText? = null
    var locationEditText: EditText? = null
    var shareCheckBox: CheckBox? = null

    var db = PpdDB(context)
    var modelSpinner: Spinner? = null
    var makeSpinner: Spinner? = null

    var progressDialog: PrograssDialog? = null
    var listener: AddFragment.OnResultListener? = null


    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg!!.what) {
                0 -> {
                    val printer = msg.obj as String
                    progressDialog!!.dismiss()
                    val result = msg.arg1
                    when (result) {
                        0 -> Toast.makeText(context, "Add successfully.", Toast.LENGTH_LONG).show()
                        1 -> {
                            Toast.makeText(context, "Add successfully.", Toast.LENGTH_LONG).show()
                            AlertDialog.Builder(context).setMessage(R.string.show_options)
                                    .setNegativeButton(R.string.cancel, null)
                                    .setPositiveButton(R.string.ok) {
                                        dialog, which ->
                                        dismiss()
                                        listener!!.onListener(printer)
                                    }
                                    .show()
                        }
                    }

                }
            }
            super.handleMessage(msg)
        }
    }

    constructor(context: Context, uri: String, listener: AddFragment.OnResultListener) : super(context) {
        this.uri = uri
        this.listener = listener
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
                if (makeSpinner!!.selectedItemPosition != 0 && modelSpinner!!.selectedItemPosition != 0) {

                    var make = makeSpinner!!.selectedItem as String
                    var model = modelSpinner!!.selectedItem as String
                    var ppdname: String = ""
                    var type: Int = 0
                    if (model.toLowerCase().equals("raw")) {
                        type = 1;
                        ppdname = "raw" //no use, just as a flag
                    } else
                        ppdname = getServerPpd(db.getNameByMakeAndModel(make, model))
                    if (!!TextUtils.isEmpty(ppdname)) {
                        Snackbar.make(view, context.getString(R.string.ppd_error), Snackbar.LENGTH_LONG).show()
                    } else {
                        var isShared = shareCheckBox!!.isChecked
                        var name = nameEditText!!.text.toString()
                        var uri = uriEditText!!.text.toString()
                        var location = locationEditText!!.text.toString()
                        var info = descriptionEditText!!.text.toString()
                        dismiss()
                        progressDialog = PrograssDialog("添加打印机中...", context)
                        progressDialog!!.show()

                        //do add printer
                        ThreadUtil.execute(Runnable {
                            var result = addPrinter(type, ppdname, name, uri, isShared, location, info);
                            var msg = Message.obtain()
                            with(msg) {
                                what = 0
                                obj = name
                                if (result)
                                    arg1 = 1
                                else
                                    arg1 = 0
                            }
                            handler.sendMessage(msg)
                        })
                    }
                    return@setOnClickListener
                }

                //add printer,first check system provide ppd
                if (!TextUtils.isEmpty(uriEditText!!.text.toString())) {
                    var isShared = shareCheckBox!!.isChecked
                    var name = nameEditText!!.text.toString()
                    var uri = uriEditText!!.text.toString()
                    var location = locationEditText!!.text.toString()
                    var info = descriptionEditText!!.text.toString()

                    dismiss()
                    progressDialog = PrograssDialog("添加打印机中...", context)
                    progressDialog!!.show()

                    //do add printer
                    ThreadUtil.execute(Runnable {
                        //copy the ppd
                        var uuid = UUID.randomUUID().toString()
                        var ppdpath = context.filesDir.absolutePath
                        var outputstream = context.openFileOutput(uuid, Context.MODE_PRIVATE)
                        var inputstream = context.contentResolver.openInputStream(ppd)

                        FileUtil.copyFile(inputstream, outputstream)

                        var result = addPrinter(0, ppdpath + File.separator + uuid, name, uri, isShared, location, info);
                        var msg = Message.obtain()
                        with(msg) {
                            what = 0
                            obj = name
                            if (result)
                                arg1 = 1
                            else
                                arg1 = 0
                        }
                        handler.sendMessage(msg)
                    })

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
        makeSpinner = findViewById(R.id.make_spinner) as Spinner
        modelSpinner = findViewById(R.id.model_spinner) as Spinner

        var itemMake = db.getAllMake()
        itemMake.add(0, context.getString(R.string.printer_make))
        var adapterMake = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemMake.toTypedArray())
        makeSpinner!!.adapter = adapterMake

        var itemModel = arrayOf(context.getString(R.string.printer_model))
        val adapterModel = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemModel)
        modelSpinner!!.adapter = adapterModel

        makeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                modelSpinner!!.adapter = adapterModel
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    modelSpinner!!.adapter = adapterModel
                    return
                }

                var itemModelByMake = ArrayList<String>()
                if (itemMake[position].toLowerCase().equals("raw")) {
                    itemModelByMake.add("raw")
                } else {
                    itemModelByMake = db.getModelByMake(itemMake[position])
                }
                itemModelByMake.add(0, context.getString(R.string.printer_model))
                val adapterModelByMake = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemModelByMake.toTypedArray())
                modelSpinner!!.adapter = adapterModelByMake
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
        nameEditText = (findViewById(R.id.name_editText) as TextInputLayout).editText
        descriptionEditText = (findViewById(R.id.description_editText) as TextInputLayout).editText
        locationEditText = (findViewById(R.id.location_editText) as TextInputLayout).editText
        uriTextView = (findViewById(R.id.uri_textView) as TextInputLayout).editText
        uriTextView!!.isEnabled = false
        shareCheckBox = findViewById(R.id.share_checkBox) as CheckBox
    }

    private fun initAddTab() {
        uriEditText = (findViewById(R.id.uri_editText) as TextInputLayout).editText
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
            ppd = Uri.parse(intent!!.getStringExtra("ppd"))
            ppdTextView!!.text = ppd!!.path//FileUtil.getNameByPathWithSuffix(context!!, ppd!!)
        }

    }
}