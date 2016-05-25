package com.android.printclient.fragment.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.printclient.R
import com.android.printclient.objects.Item
import com.android.printclient.objects.Option
import com.android.printclient.view.adapter.OptionAdapter
import java.util.*

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

        var recyleView = view.findViewById(R.id.sub_recyclerView) as RecyclerView


        Log.d("options", " =====>  " + options.size)

        recyleView.setHasFixedSize(true)
        recyleView.layoutManager = LinearLayoutManager(context)
        //recyleView.emptyView = view.findViewById(R.id.empty_LinearLayout)
        recyleView.itemAnimator = DefaultItemAnimator()
        var optionAdapter = OptionAdapter(options, context)
        optionAdapter.onItemOrBoxClicked = object : OptionAdapter.OnItemOrBoxClicked {
            override fun onItemClick(items: String?, options: List<Item>, choice: String) {
                var itemsText = ArrayList<String>()
                var itemsChoice = ArrayList<String>()
                options.forEach {
                    itemsText.add(it.text!!)
                    itemsChoice.add(it.choice!!)
                }
                var index = itemsChoice.indexOf(choice)
                AlertDialog.Builder(context)
                        .setMessage("选项")
                        .setSingleChoiceItems(itemsText.toTypedArray(), index, { v, which ->  })
                        .setPositiveButton("确定", null)
                        .show()
            }

            override fun onClickBox(key: String?, select: Boolean) {
            }

        }
        recyleView.adapter = optionAdapter

        return view
    }
}