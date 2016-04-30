package com.android.printclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.printclient.MainActivity
import com.android.printclient.R

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

        return view
    }
}