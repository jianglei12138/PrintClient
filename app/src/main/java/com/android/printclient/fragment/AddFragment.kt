package com.android.printclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.objects.Device
import com.android.printclient.utility.CleftDevice
import com.android.printclient.view.adapter.AffixAdapter
import java.util.*

/**
 * Created by jianglei on 16/4/30.
 */
class AddFragment : Fragment() {

    init {
        System.loadLibrary("extension")
    }

    external fun getDevices(listener: OnFoundDeviceListener)

    interface OnFoundDeviceListener {
        fun onFound(newDevices: Device)
        fun onFinish()
    }

    var data = ArrayList<Any>()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater
        var view = inflater?.inflate(R.layout.fragment_add, container, false)

        if (activity is MainActivity) {
            var act = activity
            act.title = getString(R.string.add_printers)
        }

        var waysRecyclerView = view?.findViewById(R.id.ways_RecyclerView) as RecyclerView
        waysRecyclerView.setHasFixedSize(true)
        waysRecyclerView.layoutManager = LinearLayoutManager(activity)

        //init data list
        data.add(activity.getString(R.string.local_printer))
        data.add(activity.getString(R.string.network_printer))
        data.add(activity.getString(R.string.other_printer))

        var adapter = AffixAdapter(data, activity)
        waysRecyclerView.adapter = adapter

        getDevices(object : OnFoundDeviceListener {
            override fun onFound(newDevices: Device) {
                Log.e("JNIEnv", newDevices.deviceClass + " " + newDevices.deviceId + " " + newDevices.deviceInfo + " " + newDevices.deviceLocaton + " " + newDevices.deviceMakeModel + " " + newDevices.deviceUri)
                var result = CleftDevice.cleftDevice(newDevices, activity)
                data.add(data.indexOf(result) + 1, newDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onFinish() {
            }
        })

        return view
    }

}