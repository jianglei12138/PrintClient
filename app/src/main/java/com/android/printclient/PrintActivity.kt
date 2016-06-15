package com.android.printclient

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.android.printclient.objects.Paper
import com.android.printclient.objects.Printer
import kotlinx.android.synthetic.main.activity_spooler.*
import kotlinx.android.synthetic.main.toolbar_detail.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.io.File

class PrintActivity : AppCompatActivity() {

    init {
        System.loadLibrary("extension")
    }

    external fun getPrinters(): List<Printer>
    external fun getSupportPageSize(name: String): List<Paper>

    var SCREEN_HEIGHT: Int = 0
    var INIT_SWIPE_HEIGHT: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spooler)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = ""

        var array = getPrinters()
        text.text = array[0].name
        array.forEachWithIndex { i, printer ->
            if (printer.isdefault) text.text = printer.name
        }

        spinner.setOnClickListener { v -> showPopup(v, array) }

        pdfView.fromFile(File(intent.data.path))
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .enableDoubletap(false)
                .onDraw({ canvas, w, h, i -> })
                .onLoad({ })
                .onPageChange({ page, pageCount -> page_textView.text = getString(R.string.preview_page, page, pageCount) })
                .onError({ Toast.makeText(PrintActivity@this, "load failure", Toast.LENGTH_SHORT).show() })
                .load();


        //init swipe layout
        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        SCREEN_HEIGHT = dm.heightPixels
        INIT_SWIPE_HEIGHT = resources.getDimensionPixelSize(R.dimen.swipe_layout)
        //swipeLayout.y = (SCREEN_HEIGHT - INIT_SWIPE_HEIGHT).toFloat()

        var papers = getSupportPageSize(text.text.toString())
        var papersArray = arrayOfNulls<String>(papers.size)
        papers.forEachWithIndex { i, paper -> papersArray[i] = paper.name }

        setBackgroundAndAdapter(duplex, resources.getStringArray(R.array.duplex))
        setBackgroundAndAdapter(oddoreven, resources.getStringArray(R.array.oddoreven))
        setBackgroundAndAdapter(size, papersArray.requireNoNulls())
        setBackgroundAndAdapter(orientation, resources.getStringArray(R.array.orientation))
        setBackgroundAndAdapter(colors, resources.getStringArray(R.array.orientation))
        setBackgroundAndAdapter(sheets, resources.getStringArray(R.array.sheets))
        setBackgroundAndAdapter(direction, resources.getStringArray(R.array.orientation))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_spooler, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showPopup(v: View, array: List<Printer>) {
        var popup = PopupMenu(this, v);
        array.forEachWithIndex { i, it -> popup.menu.add(0, i, i, it.name) }
        popup.setOnMenuItemClickListener {
            item ->
            text.text = array[item.itemId].name
            return@setOnMenuItemClickListener true
        }
        popup.show();
    }

    var downX: Float = 0f
    var viewX: Float = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var y = event!!.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = y
                // viewX = swipeLayout.x


            }
        }
        return super.onTouchEvent(event)
    }

    fun setBackgroundAndAdapter(spinner: Spinner, list: Array<String>) {
        setBackground(spinner)
        setAdapter(spinner, list)
    }


    fun setBackground(spinner: Spinner) {
        var draw = spinner.background
        draw.setColorFilter(Color.WHITE, PorterDuff.Mode.DST_IN)
        spinner.background = draw
    }

    fun setAdapter(spinner: Spinner, list: Array<String>) {
        var adapter = ArrayAdapter<String>(this, R.layout.spinner_text, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

}
