package com.android.printclient.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.printclient.R
import com.android.printclient.objects.Job
import com.android.printclient.objects.Printer

/**
 * Created by jianglei on 16/5/14.
 */
class MainAdapter : BaseAdapter {

    var printers: List<Any>
    var context: Context
    var type: Int

    constructor(printers: List<Any>?, context: Context, type: Int) : super() {
        this.printers = printers!!
        this.context = context
        this.type = type
    }

    companion object {
        var IPP_JSTATE_PENDING = 0        /* Job is waiting to be printed */
        var IPP_JSTATE_HELD = 1           /* Job is held for printing */
        var IPP_JSTATE_PROCESSING = 2     /* Job is currently printing */
        var IPP_JSTATE_STOPPED = 3        /* Job has been stopped */
        var IPP_JSTATE_CANCELED = 4       /* Job has been canceled */
        var IPP_JSTATE_ABORTED = 5        /* Job has aborted due to error */
        var IPP_JSTATE_COMPLETED = 6      /* Job has completed successfully */
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
            holder.icon = convertView.findViewById(R.id.imageView) as ImageView
            convertView.tag = holder
        }

        var holder: ViewHolder = convertView.tag as ViewHolder
        var printer = printers[position]
        if (printer is Printer) {
            if (type == 0)
                holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.printer))
            else if (type == 1)
                holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.classes))
            holder.name!!.text = printer.name
            holder.instance!!.text = printer.instance
            holder.uri!!.text = printer.deviceuri

            if (printer.isdefault) {
                holder.default!!.visibility = View.VISIBLE
            } else {
                holder.default!!.visibility = View.GONE
            }
        } else if (printer is Job) {
            when (printer.state) {
                IPP_JSTATE_COMPLETED -> {
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.complete))
                    holder.uri!!.text = context.getString(R.string.job_state_complete)
                }
                IPP_JSTATE_STOPPED -> {
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.stop))
                    holder.uri!!.text = context.getString(R.string.job_state_stop)
                }
                IPP_JSTATE_ABORTED -> {
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.abort))
                    holder.uri!!.text = context.getString(R.string.job_state_abort)
                }
                IPP_JSTATE_CANCELED ->{
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.cancel))
                    holder.uri!!.text = context.getString(R.string.job_state_cancel)
                }
                IPP_JSTATE_PROCESSING ->{
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.process))
                    holder.uri!!.text = context.getString(R.string.job_state_processing)
                }
                IPP_JSTATE_HELD -> {
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.held))
                    holder.uri!!.text = context.getString(R.string.job_state_held)
                }
                IPP_JSTATE_PENDING ->{
                    holder.icon!!.setImageDrawable(context.getDrawable(R.drawable.pending))
                    holder.uri!!.text = context.getString(R.string.job_state_pending)
                }
            }
            holder.name!!.text = printer.title + " #" + printer.id
            //holder.instance!!.text = printer.instance
            holder.default!!.visibility = View.GONE
        }
        return convertView
    }

    override fun getItem(position: Int): Any? {
        return printers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return printers.size
    }


    class ViewHolder {
        var name: TextView? = null
        var instance: TextView? = null
        var uri: TextView? = null
        var default: View? = null
        var icon: ImageView? = null
    }
}