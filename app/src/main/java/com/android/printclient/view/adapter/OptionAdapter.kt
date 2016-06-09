package com.android.printclient.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.android.printclient.R
import com.android.printclient.objects.Option
import java.util.*

/**
 * Created by jianglei on 16/5/25.
 */
class OptionAdapter : RecyclerView.Adapter<OptionAdapter.AbsViewHolder> {

    companion object {
        val UI_TYPE_BOOLEAN = "BOOLEAN"
        val UI_TYPE_PICKONE = "PICKONE"
        val UI_TYPE_PICKMANY = "PICKMANY"

        val TYPE_BOOLEAN = 1
        val TYPE_PICKONE = 2
        val TYPE_PICKMANY = 3


//        enum class TYPE(var type: Int) {
//            BOOLEAN(1), PICKONE(2), PICKMANY(3)
//        }
    }

    interface OnItemOrBoxClicked {
        fun onItemClick(option: String?);
        fun onClickBox(key: String?, select: Boolean);
    }

    var options: List<Option> = ArrayList()
    var context: Context

    var onItemOrBoxClicked: OnItemOrBoxClicked? = null;

    constructor(options: List<Option>, context: Context) {
        this.options = options
        this.context = context
    }

    override fun getItemViewType(position: Int): Int {
        //Log.d("TAG", options[position].ui)
        when (options[position].ui) {
            UI_TYPE_BOOLEAN -> return TYPE_BOOLEAN
            UI_TYPE_PICKONE -> return TYPE_PICKONE
            UI_TYPE_PICKMANY -> return TYPE_PICKMANY
            else -> return TYPE_PICKONE
        }
    }


    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: OptionAdapter.AbsViewHolder?, position: Int) {
        var option = options[position]
        if (holder is BooleanHolder) {
            holder.itemText.text = option.text
            holder.itemText.tag = option.key
            holder.itemCheckBox.isSelected = option.choice!!.toUpperCase().equals("TRUE")
            holder.itemCheckBox.setOnCheckedChangeListener {
                v, select ->
                onItemOrBoxClicked!!.onClickBox(option.key, select)
            }
            holder.itemView.setOnClickListener { v-> holder.itemCheckBox.isChecked = !holder.itemCheckBox.isChecked }
        } else if (holder is OneHolder) {
            holder.itemText.text = option.text
            var options = option.items.filter { it.choice.equals(option.choice) }
            if(options.size == 1){
                holder.itemHint.text = options[0].text
            }
            holder.view.setOnClickListener { v -> onItemOrBoxClicked!!.onItemClick(option.key) }
        } else if (holder is ManyHolder) {
            holder.itemText.text = option.text
            var options = option.items.filter { it.choice.equals(option.choice) }
            if(options.size == 1){
                holder.itemHint.text = options[0].text
            }
            holder.view.setOnClickListener { v -> onItemOrBoxClicked!!.onItemClick(option.key) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AbsViewHolder? {

        when (viewType) {
            TYPE_BOOLEAN -> {
                val view = LayoutInflater.from(context).inflate(R.layout.recycle_boolean, parent, false)
                return BooleanHolder(view)
            }
            TYPE_PICKONE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.recycle_one, parent, false)
                return OneHolder(view)
            }
            TYPE_PICKMANY -> {
                val view = LayoutInflater.from(context).inflate(R.layout.recycle_many, parent, false)
                return ManyHolder(view)
            }
            else -> return null
        }
    }


    abstract class AbsViewHolder(v: View) : RecyclerView.ViewHolder(v)

    inner class BooleanHolder(v: View) : AbsViewHolder(v) {
        var itemText: TextView
        var itemCheckBox: CheckBox

        init {
            itemCheckBox = v.findViewById(R.id.item_checkBox) as CheckBox
            itemText = v.findViewById(R.id.item_textView) as TextView
        }
    }

    inner class OneHolder(v: View) : AbsViewHolder (v) {
        var itemText: TextView
        var itemHint: TextView
        var view: View

        init {
            itemHint = v.findViewById(R.id.item_hint) as TextView
            itemText = v.findViewById(R.id.item_textView) as TextView
            view = v
        }
    }

    inner class ManyHolder(v: View) : AbsViewHolder(v) {
        var itemText: TextView
        var itemHint: TextView
        var view: View

        init {
            itemHint = v.findViewById(R.id.item_hint) as TextView
            itemText = v.findViewById(R.id.item_textView) as TextView
            view = v
        }
    }

}