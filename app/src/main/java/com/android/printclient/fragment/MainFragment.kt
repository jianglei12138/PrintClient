package com.android.printclient.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.view.adapter.MainTabAdapter
import java.util.*

class MainFragment : Fragment() {

    init {
        System.loadLibrary("extension")
    }

    var tabs: TabLayout? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater view
        var view = inflater?.inflate(R.layout.fragment_main, container, false)

        if (activity is MainActivity) {
            var act = activity
            tabs = act.findViewById(R.id.tabs) as TabLayout
            tabs!!.visibility = View.VISIBLE
        }

        //init title
        val title = ArrayList<String>()
        title.add(getString(R.string.title_printer))
        title.add(getString(R.string.title_class))
        title.add(getString(R.string.title_jobs))
        title.add(getString(R.string.title_rss))

        val adapter = MainTabAdapter(childFragmentManager, title, context)

        val mViewPager = view!!.findViewById(R.id.viewPager) as ViewPager
        mViewPager.adapter = adapter

        tabs!!.tabMode = TabLayout.MODE_SCROLLABLE
        tabs!!.setupWithViewPager(mViewPager)

        return view
    }
}
