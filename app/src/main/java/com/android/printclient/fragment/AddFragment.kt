package com.android.printclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.view.adapter.AddAdapter
import java.util.*

/**
 * Created by jianglei on 16/4/30.
 */
class AddFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater
        var view = inflater?.inflate(R.layout.fragment_add, container, false)

        if (activity is MainActivity) {
            var act = activity
            act.title = getString(R.string.add_printers)
        }

        var waysRecyclerView = view?.findViewById(R.id.ways_RecyclerView) as RecyclerView
        waysRecyclerView.setHasFixedSize(true)
        waysRecyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

}