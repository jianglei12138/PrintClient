package com.android.printclient.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.objects.Conflict
import com.android.printclient.view.adapter.TabAdapter
import java.util.*

/**
 * Created by jianglei on 16/5/23.
 */
class OptionFragment : Fragment() {

    init {
        System.loadLibrary("options")
    }

    external fun getOptionGroups(name: String): ArrayList<String>
    external fun release()
    external fun getConflictData(name: String): ArrayList<Conflict>

    var printer: String? = null


    var tabs: TabLayout? = null

    var mapConflict = ArrayList<HashMap<String, String>>()
    var listConflict = ArrayList<Conflict>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        printer = bundle.getString("printer")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_options, container, false)

        if (activity is MainActivity) {
            var act = activity
            act.title = getString(R.string.set_options)
            tabs = act.findViewById(R.id.tabs) as TabLayout
            tabs!!.visibility = View.VISIBLE
        }

        var list = getOptionGroups(printer!!)

        getData(printer!!)
        var adapterConflict = SimpleAdapter(
                context,
                mapConflict,
                R.layout.listview_conflicts,
                arrayOf("text", "value"),
                intArrayOf(R.id.text, R.id.value)
        )

        val conflictView = view.findViewById(R.id.conflict_listView) as ListView
        conflictView.adapter = adapterConflict

        val adapter = TabAdapter(childFragmentManager, list, printer!!, context)

        val mViewPager = view.findViewById(R.id.viewPager) as ViewPager
        mViewPager.adapter = adapter

        tabs!!.tabMode = TabLayout.MODE_SCROLLABLE
        tabs!!.setupWithViewPager(mViewPager)
        return view
    }

    private fun getData(name: String) {
        listConflict.clear()
        listConflict = getConflictData(name)
        if (listConflict != null)
            listConflict.forEach {
                val map = HashMap<String, String>()
                map.put("text", it.key!!)
                map.put("value", it.choice!!)
                mapConflict.add(map)
            }
    }


    override fun onDestroy() {
        release()
        super.onDestroy()
    }
}