package com.android.printclient.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.android.printclient.fragment.fragment.SubFragment
import com.android.printclient.utility.TabDict
import java.util.*

/**
 * Created by jianglei on 16/5/23.
 */
class TabAdapter : FragmentPagerAdapter {

    var title: List<String>
    var printer: String
    var context: Context

    var dictionary: HashMap<String, String>


    constructor(manager: FragmentManager, title: List<String>, printer: String, context: Context) : super(manager) {
        this.title = title
        this.printer = printer
        this.context = context
        dictionary = TabDict(context).dictionary
    }

    override fun getItem(position: Int): Fragment {
        return SubFragment.newInstance(title[position])
    }

    override fun getCount(): Int {
        return title.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var language = dictionary[title[position].toUpperCase().replace(" ","")]
        if (language == null) language = title[position]
        return language
    }
}