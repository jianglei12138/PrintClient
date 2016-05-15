package com.android.printclient.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.printclient.R
import com.android.printclient.objects.Device

/**
 * Created by jianglei on 16/4/30.
 */
class AffixAdapter : RecyclerView.Adapter<AffixAdapter.ViewHolder> {

    private val TYPE_TITLE = 1
    private val TYPE_TEXT = 2

    private var mDataset: List<Any>
    private var context: Context

    constructor(mDataset: List<Any>, context: Context) {
        this.mDataset = mDataset
        this.context = context
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var data: Any = mDataset.get(position)
        if (holder is PrinterTitleHolder) {
            holder.title.text = data.toString()
        } else if (holder is PrinterTextHolder) {
            holder.text.text = (data as Device).deviceInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        when (viewType) {
            TYPE_TITLE -> {
                var view: View = LayoutInflater.from(context).inflate(R.layout.recycler_itemtitle, parent, false)
                return PrinterTitleHolder(view)
            }

            TYPE_TEXT -> {
                var view: View = LayoutInflater.from(context).inflate(R.layout.recycler_itemtext, parent, false)
                return PrinterTextHolder(view)
            }

            else -> return null;
        }
    }

    override fun getItemViewType(position: Int): Int {

        when (mDataset.get(position)) {
            context.getString(R.string.local_printer) -> return TYPE_TITLE
            context.getString(R.string.network_printer) -> return TYPE_TITLE
            context.getString(R.string.other_printer) -> return TYPE_TITLE
            else -> return TYPE_TEXT
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class PrinterTitleHolder(view: View) : AffixAdapter.ViewHolder(view) {
        var title: TextView

        init {
            title = view.findViewById(R.id.title) as TextView
        }

    }

    inner class PrinterTextHolder(view: View) : AffixAdapter.ViewHolder(view) {
        var text: TextView

        init {
            text = view.findViewById(R.id.text) as TextView
        }

    }
}