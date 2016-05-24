package com.android.printclient.fragment.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.printclient.R
import com.android.printclient.objects.Option

/**
 * Created by jianglei on 16/5/23.
 */
class SubFragment : Fragment() {

    init {
        System.loadLibrary("options")
    }


    companion object {
        val ARG_SELECTION_TITLE = "title"
        val ARG_SELECTION_NAME = "name"
        fun newInstance(title: String, printer: String): SubFragment {
            val fragment = SubFragment()
            val args = Bundle()
            args.putString(ARG_SELECTION_TITLE, title)
            args.putString(ARG_SELECTION_NAME, printer)
            fragment.arguments = args
            return fragment
        }
    }

    external fun getGroup(group: String, printer: String): List<Option>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_sub, container, false)

        val title = arguments.getString(ARG_SELECTION_TITLE)
        val name = arguments.getString(ARG_SELECTION_NAME)

        var options = getGroup(title, name);
        var txt = view.findViewById(R.id.textView) as TextView
        txt.text = title+  ""

        options.forEach {
            txt.append(it.text+"---->")
        }

        return view
    }
}