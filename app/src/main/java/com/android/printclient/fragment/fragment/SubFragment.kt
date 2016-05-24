package com.android.printclient.fragment.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.printclient.R

/**
 * Created by jianglei on 16/5/23.
 */
class SubFragment : Fragment() {


    var title: String? = null

    companion object {
        val ARG_SELECTION_TITLE = "arg"
        fun newInstance(title: String): SubFragment {
            val fragment = SubFragment()
            val args = Bundle()
            args.putString(ARG_SELECTION_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments.getString("title")
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_sub, container, false)

        var txt = view.findViewById(R.id.textView) as TextView
        txt.text = "title"

        return view
    }
}