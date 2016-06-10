package com.android.printclient

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.printclient.objects.Printer
import kotlinx.android.synthetic.main.activity_spooler.*
import kotlinx.android.synthetic.main.content_spooler.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.io.File

class PrintActivity : AppCompatActivity() {

    init {
        System.loadLibrary("extension")
    }

    external fun getPrinters(): List<Printer>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spooler)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        title = "打印机: "

        var array = getPrinters()
        text.text = array[0].name
        array.forEachWithIndex { i, printer ->
            if (printer.isdefault) text.text = printer.name
        }

        spinner.setOnClickListener { v -> showPopup(v, array) }

        pdfView.fromFile(File(intent.data.path))
                .defaultPage(1)
                .showMinimap(true)
                .enableSwipe(true)
                .onDraw({ canvas, w, h, i ->
                    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    paint.color = Color.BLACK
                    canvas.drawText("第 $i 页",10f,20f,paint)
                })
                .onLoad({  })
                .onPageChange({ i1, i2 ->  })
                .onError({ Toast.makeText(PrintActivity@this, "load failure", Toast.LENGTH_SHORT).show() })
                .load();


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
}
