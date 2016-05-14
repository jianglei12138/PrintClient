package com.android.printclient.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.android.printclient.R
import com.android.printclient.objects.Printer

/**
 * Created by jianglei on 16/5/14.
 */
class MainAdapter : BaseAdapter {

    var printers: List<Printer>? = null
    var context: Context? = null

    constructor(printers: List<Printer>?, context: Context?) : super() {
        this.printers = printers
        this.context = context
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.listview_item, null)
            var holder: ViewHolder = ViewHolder()
            holder.name = convertView!!.findViewById(R.id.name_textView) as TextView
            holder.instance = convertView.findViewById(R.id.instance_textView) as TextView
            holder.uri = convertView.findViewById(R.id.url_textView) as TextView
            holder.default = convertView.findViewById(R.id.default_textView)
            convertView.tag = holder
        }

        var holder: ViewHolder = convertView.tag as ViewHolder
        var printer = printers!![position]
        holder.name!!.text = printer.name
        holder.instance!!.text = printer.instance
        holder.uri!!.text = printer.deviceuri

        if (printer.isdefault) {
            holder.default!!.visibility = View.VISIBLE
        } else {
            holder.default!!.visibility = View.GONE
        }
        return convertView
    }

    override fun getItem(position: Int): Any? {
        return printers!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return printers!!.size
    }


    class ViewHolder {
        var name: TextView? = null
        var instance: TextView? = null
        var uri: TextView? = null
        var default: View? = null
    }
}