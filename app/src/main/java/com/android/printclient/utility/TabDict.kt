package com.android.printclient.utility

import android.content.Context
import com.android.printclient.R
import java.util.*

/**
 * Created by jianglei on 16/5/23.
 */
class TabDict(context: Context) {

    //for print options GENERAL,BANNERS,POLICIES,PORT MONITOR,JCL,OPTIONS_INSTALLED
    var dictionary = HashMap<String, String>()

    init {
        dictionary.put("GENERAL",context.getString(R.string.general));
        dictionary.put("BANNERS",context.getString(R.string.banners));
        dictionary.put("POLICIES",context.getString(R.string.policies));
        dictionary.put("PORTMONITOR",context.getString(R.string.port_monitor));
        dictionary.put("JCL",context.getString(R.string.jcl))
        dictionary.put("OPTIONSINSTALLED",context.getString(R.string.options_installed))
        dictionary.put("PRINTERSETTINGS",context.getString(R.string.printer_settings))
    }
}