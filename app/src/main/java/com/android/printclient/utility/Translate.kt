package com.android.printclient.utility

import android.content.Context
import com.android.printclient.R
import java.util.*

/**
 * Created by jianglei on 16/5/14.
 */
class Translate(context: Context) {
    val dictionary = HashMap<String,String>()
    init {
        dictionary.put("copies",context.getString(R.string.copies));
        dictionary.put("device-uri",context.getString(R.string.device_uri));
        dictionary.put("finishings",context.getString(R.string.finishings));
        dictionary.put("job-cancel-after",context.getString(R.string.job_cancel_after));
        dictionary.put("job-hold-until",context.getString(R.string.job_hold_until));
        dictionary.put("job-priority",context.getString(R.string.job_priority));
        dictionary.put("job-sheets",context.getString(R.string.job_sheets));
        dictionary.put("marker-change-time",context.getString(R.string.marker_change_time));
        dictionary.put("number-up",context.getString(R.string.number_up ));
        dictionary.put("printer-commands",context.getString(R.string.printer_commands));
        dictionary.put("printer-info",context.getString(R.string.printer_info));
        dictionary.put("printer-is-accepting-jobs",context.getString(R.string.printer_is_accepting_jobs));
        dictionary.put("printer-is-shared",context.getString(R.string.printer_is_shared));
        dictionary.put("printer-location",context.getString(R.string.printer_location));
        dictionary.put("printer-make-and-model",context.getString(R.string.printer_make_and_model));
        dictionary.put("printer-state",context.getString(R.string.printer_state));
        dictionary.put("printer-state-change-time",context.getString(R.string.printer_state_change_time));
        dictionary.put("printer-state-reasons",context.getString(R.string.printer_state_reasons));
        dictionary.put("printer-type",context.getString(R.string.printer_type));
        dictionary.put("printer-uri-supported",context.getString(R.string.printer_uri_supported));
    }
}