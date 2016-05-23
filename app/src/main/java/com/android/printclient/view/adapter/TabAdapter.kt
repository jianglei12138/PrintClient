package com.android.printclient.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by jianglei on 16/5/23.
 */
class TabAdapter : FragmentPagerAdapter {

    var mfragments: List<Fragment>
    var title: List<String>

    constructor(manager: FragmentManager, fragments: List<Fragment>, title: List<String>) : super(manager) {
        this.mfragments = fragments
        this.title = title
    }

    override fun getItem(position: Int): Fragment {
        return mfragments[position]
    }

    override fun getCount(): Int {
        return mfragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}