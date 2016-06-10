package com.android.printclient.fragment.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.dialog.PrinterDialog
import com.android.printclient.objects.Job
import com.android.printclient.objects.Printer
import com.android.printclient.view.adapter.MainAdapter
import java.util.*

/**
 * Created by jianglei on 16/6/9.
 */
class SubMainFragment : Fragment() {
    init {
        System.loadLibrary("extension")
    }


    companion object {
        val ARG_SELECTION_TITLE = "title"
        val CLASSES_URI = "file:///dev/null"
        fun newInstance(title: String): SubMainFragment {
            val fragment = SubMainFragment()
            val args = Bundle()
            args.putString(ARG_SELECTION_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    external fun getPrinters(): List<Printer>
    external fun getJobs(): List<Job>
    external fun getAttributePrinter(name: String, instance: String?): HashMap<String, String>
    external fun checkCupsd(): Boolean
    external fun cancelJob(dest: String, id: Int): Boolean

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflater view
        var view = inflater?.inflate(R.layout.fragment_submain, container, false)


        if (activity is MainActivity)
            activity.title = getString(R.string.save_printers)

        var printerListView: ListView = view!!.findViewById(R.id.printer_ListView) as ListView
        var emptyView: View = view.findViewById(R.id.empty_LinearLayout)


        var title = arguments.get(ARG_SELECTION_TITLE)

        when (title) {
            getString(R.string.title_printer) -> {
                var printers = getPrinters().filter { !it.deviceuri.equals(CLASSES_URI) }

                var adapter: MainAdapter = MainAdapter(printers, activity, 0)
                printerListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    var instance: String? = null
                    var name: String = printers[position].name
                    if (printers[position].instance.length > 0) {
                        instance = printers[position].instance
                    }
                    var dialog: PrinterDialog = PrinterDialog(activity, getAttributePrinter(name, instance), name)
                    dialog.show()
                }

                printerListView.emptyView = emptyView
                printerListView.adapter = adapter
            }
            getString(R.string.title_class) -> {
                var classes = getPrinters().filter { it.deviceuri.equals(CLASSES_URI) }

                var adapter: MainAdapter = MainAdapter(classes, activity, 1)
                printerListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    var instance: String? = null
                    var name: String = classes[position].name
                    if (classes[position].instance.length > 0) {
                        instance = classes[position].instance
                    }
                    var dialog: PrinterDialog = PrinterDialog(activity, getAttributePrinter(name, instance), name)
                    dialog.show()
                }

                printerListView.emptyView = emptyView
                printerListView.adapter = adapter
            }
            getString(R.string.title_jobs) -> {
                var jobs = getJobs()
                var adapter: MainAdapter = MainAdapter(jobs, activity, 1)
                printerListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
                    var job = jobs[position]
                    var map = HashMap<String, String>()
                    map.put("ID", job.id.toString())
                    map.put("Title", job.title + "")
                    map.put("Dest", job.dest + "")
                    map.put("Format", job.format + "")
                    map.put("User", job.user + "");
                    map.put("Completed_time", job.completed_time.toString());
                    map.put("Creation_time", job.creation_time.toString());
                    map.put("Size", job.size.toString());
                    map.put("Priority", job.priority.toString());
                    map.put("Processing_time", job.processing_time.toString());
                    map.put("Size", job.size.toString());
                    var dialog: PrinterDialog = PrinterDialog(activity, map, job.title + " #" + job.id)
                    dialog.show()
                    false
                }
                printerListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, v, i, l ->
                    if (jobs[i].state.equals(MainAdapter.IPP_JSTATE_STOPPED))
                        AlertDialog.Builder(context)
                                .setTitle(R.string.job_operation)
                                .setItems(R.array.job_operation_array, { dialogInterface, i ->
                                    when (i) {
                                        0 -> {

                                        }
                                        1 -> {
                                            var result = cancelJob(jobs[i].dest!!, jobs[i].id)
                                            if (result)
                                                Snackbar.make(view, getString(R.string.cancel_failure), Snackbar.LENGTH_LONG).show()
                                            else
                                                Snackbar.make(view, getString(R.string.cancel_success), Snackbar.LENGTH_LONG).show()
                                        }

                                        2 -> {

                                        }
                                    }
                                })
                                .show()
                }
                printerListView.emptyView = emptyView
                printerListView.adapter = adapter
            }
        }
        return view
    }
}