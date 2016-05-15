package com.android.printclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.objects.Printer
import com.android.printclient.view.PrinterDialog
import com.android.printclient.view.adapter.MainAdapter
import java.util.*

class MainFragment : Fragment() {

    init {
        System.loadLibrary("extension")
    }

    external fun getPrinters(): List<Printer>
    external fun getAttributePrinter(name: String, instance: String?): HashMap<String, String>
    external fun checkCupsd():Boolean

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater view
        var view = inflater?.inflate(R.layout.fragment_main, container, false)


        if (activity is MainActivity)
            activity.title = getString(R.string.save_printers)

        var printerListView: ListView = view!!.findViewById(R.id.printer_ListView) as ListView
        var emptyView: View = view.findViewById(R.id.empty_LinearLayout)

        var printers = getPrinters()
        var adapter: MainAdapter = MainAdapter(printers, activity)

        printerListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var instance:String? = null
            var name:String = printers[position].name
            if (printers[position].instance.length > 0) {
                instance = printers[position].instance
            }
            var dialog: PrinterDialog = PrinterDialog(activity, getAttributePrinter(name,instance),name)
            dialog.show()
        }

        printerListView.emptyView = emptyView;
        printerListView.adapter = adapter
        return view
    }
}
