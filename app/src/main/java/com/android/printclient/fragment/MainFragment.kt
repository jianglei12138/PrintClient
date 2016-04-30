package com.android.printclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.android.printclient.R
import com.android.printclient.view.RecyclerViewWrapper

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater view
        val view = inflater?.inflate(R.layout.fragment_main, container, false)
        val printerRecyclerView = view?.findViewById(R.id.printer_RecyclerView) as RecyclerViewWrapper;
        val emptyLinearLayout = view?.findViewById(R.id.empty_LinearLayout) as LinearLayout;

        //printer list
        printerRecyclerView.setHasFixedSize(true)
        printerRecyclerView.layoutManager = LinearLayoutManager(activity)
        printerRecyclerView.setEmptyView(emptyLinearLayout)

        return view
    }
}