package com.android.printclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.objects.Entry
import com.android.printclient.objects.Printer
import com.android.printclient.view.MainRecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*

class MainFragment : Fragment() {

    init {
        System.loadLibrary("extension")
    }

    external fun getPrinters(): List<Printer>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater view
        var view = inflater?.inflate(R.layout.fragment_main, container, false)


        if (activity is MainActivity)
            activity.title = getString(R.string.save_printers)
        //printer list
        //        printerRecyclerView.setHasFixedSize(true)
        //        printerRecyclerView.layoutManager = LinearLayoutManager(activity)
        //        printerRecyclerView.setEmptyView(emptyLinearLayout)
        //printer_ListView.

        var printerListView: ListView = view!!.findViewById(R.id.printer_ListView) as ListView
        var emptyView: View = view!!.findViewById(R.id.empty_LinearLayout)

        var listItem: ArrayList<HashMap<String, String>> = ArrayList()

        var printers = getPrinters()
        for (item in printers) {
            var map: HashMap<String, String> = HashMap()
            map.put("name", item.name)
            map.put("instance", item.instance)
            var options: List<Entry> = item.options
            var entry = options.filter { it.name.equals("device-uri") }.get(0)
            map.put("device-uri", entry.value)
            listItem.add(map)
        }

        var adapter: SimpleAdapter = SimpleAdapter(
                activity,
                listItem,
                R.layout.listview_item,
                arrayOf("name", "instance", "device-uri"),
                intArrayOf(R.id.name_textView, R.id.name_textView, R.id.instance_textView)
        )

        printerListView.emptyView = emptyView;
        printerListView.adapter = adapter
        return view
    }
}
