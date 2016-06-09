package com.android.printclient.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.android.printclient.fragment.fragment.SubMainFragment

/**
 * Created by jianglei on 16/5/23.
 */
class MainTabAdapter : FragmentPagerAdapter {

    var title: List<String>
    var context: Context

    constructor(manager: FragmentManager, title: List<String>, context: Context) : super(manager) {
        this.title = title
        this.context = context
    }

    override fun getItem(position: Int): Fragment {
        return SubMainFragment.newInstance(title[position])
    }

    override fun getCount(): Int {
        return title.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}