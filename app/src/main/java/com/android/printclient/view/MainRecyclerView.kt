package com.android.printclient.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View

class MainRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    //empty view
    var emptyView: View? = null
    private val emptyObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            if (adapter != null && adapter.itemCount > 0 && emptyView != null) {
                visibility = View.GONE
                emptyView!!.visibility = View.VISIBLE
            } else {
                emptyView!!.visibility = View.GONE
                visibility = View.VISIBLE
            }
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<ViewHolder>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        Log.d("TAG", "" + adapter!!.itemCount + "==>")
        emptyObserver.onChanged()
    }

}