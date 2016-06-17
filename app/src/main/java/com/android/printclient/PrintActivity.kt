package com.android.printclient

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.text.Html
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.android.printclient.objects.Item
import com.android.printclient.objects.Paper
import com.android.printclient.objects.Printer
import com.android.printclient.view.adapter.TextWatcherAdapter
import kotlinx.android.synthetic.main.activity_spooler.*
import kotlinx.android.synthetic.main.toolbar_detail.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.io.File
import java.util.*
import java.util.regex.Pattern

class PrintActivity : AppCompatActivity() {

    init {
        System.loadLibrary("extension")
    }

    external fun getPrinters(): ArrayList<Printer>
    external fun init(name: String): Boolean
    external fun getSupportPageSize(): List<Paper>
    external fun getSupportDuplex(): List<Item>
    external fun isSupportColor(): Boolean

    var animationShow: TranslateAnimation? = null
    var printers: List<Printer> = ArrayList()
    @Volatile var canExecute = Stack<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spooler)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = ""

        var pagecount = 0
        pdfView.fromFile(File(intent.data.path))
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .enableDoubletap(false)
                .onDraw({ canvas, w, h, i -> })
                .onLoad({ })
                .onPageChange({ page, pageCount ->
                    page_textView.text = getString(R.string.preview_page, page, pageCount)
                    pagecount = pageCount
                })
                .onError({ Toast.makeText(PrintActivity@this, "load failure", Toast.LENGTH_SHORT).show() })
                .load()

        printers = getPrinters()
        if (printers.size < 1) {
            text.text = resources.getString(R.string.no_able_printer)
            spinner.visibility = View.GONE
        } else {
            text.text = printers[0].name
            printers.forEachWithIndex { i, printer ->
                if (printer.isdefault) text.text = printer.name
            }

            spinner.setOnClickListener { v -> showPopup(v, printers) }

            //animation
            animationShow = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
            animationShow!!.duration = 200


            //init all
            init(text.text.toString())

            //paper size
            var papers = getSupportPageSize()
            var papersArray = arrayOfNulls<String>(papers.size)
            var defaultPaperSize = 0
            papers.forEachWithIndex {
                i, paper ->
                papersArray[i] = paper.name
                if (paper.marked) defaultPaperSize = i
            }
            setBackgroundAndAdapter(size, papersArray.requireNoNulls())
            size.setSelection(defaultPaperSize)

            //duplex
            var duplexs = getSupportDuplex()
            if (duplexs == null) {
                setBackgroundAndAdapter(duplex, resources.getStringArray(R.array.duplex))
            } else {
                var duplexsArray = arrayOfNulls<String>(duplexs.size)
                var defaultDuplex = 0
                duplexs.forEachWithIndex {
                    i, item ->
                    duplexsArray[i] = item.text
                    if (item.marked) defaultDuplex = i;
                }
                setBackgroundAndAdapter(duplex, duplexsArray.requireNoNulls())
                duplex.setSelection(defaultDuplex)
            }

            setBackgroundAndAdapter(oddoreven, resources.getStringArray(R.array.oddoreven))
            setBackgroundAndAdapter(orientation, resources.getStringArray(R.array.orientation))
            setBackgroundAndAdapter(colors, resources.getStringArray(R.array.colors))
            setBackgroundAndAdapter(sheets, resources.getStringArray(R.array.sheets))
            setBackgroundAndAdapter(order, resources.getStringArray(R.array.order))
            setBackgroundAndAdapter(sheets_layout, resources.getStringArray(R.array.layout))

            copies.addTextChangedListener(object : TextWatcherAdapter() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!TextUtils.isDigitsOnly(s) || TextUtils.isEmpty(s)) {
                        val error = Html.fromHtml("<font color='#FFFFFF'>" + getString(R.string.copies_only_integer) + "</font>")
                        copies.error = error
                    } else if (s.toString().length > 3 || s.toString().toLong() > 100 || s.toString().toLong() < 1) {
                        val error = Html.fromHtml("<font color='#FFFFFF'>" + getString(R.string.copies_number_many) + "</font>")
                        copies.error = error
                    }
                }

            })

            pages.addTextChangedListener(object : TextWatcherAdapter() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!rangeFilter(s.toString())) {
                        val error = Html.fromHtml("<font color='#FFFFFF'> " + getString(R.string.pages_format_error) + " </font>")
                        pages.error = error
                    } else {
                        s!!.split(",").toList().forEach {
                            if (it.contains("-")) {
                                var page = it.split("-")
                                if (page[0].toInt() >= page[1].toInt() || page[1].toInt() > pagecount) {
                                    val error = Html.fromHtml("<font color='#FFFFFF'> " + getString(R.string.pages_format_error) + "</font>")
                                    pages.error = error
                                }
                            } else if (it.toInt() > pagecount) {
                                val error = Html.fromHtml("<font color='#FFFFFF'> " + getString(R.string.pages_number_error) + " </font>")
                                pages.error = error
                            }
                        }
                    }
                }
            })

            var lessBg = resources.getDrawable(R.drawable.ic_expand_less_black_24dp)
            var moreBg = resources.getDrawable(R.drawable.ic_expand_more_black_24dp)

            more_layout.visibility = View.VISIBLE
            //init swipe layout
            more.background = moreBg
            more.setOnClickListener({
                v ->
                if (include.visibility == View.VISIBLE) {
                    v.background = moreBg
                    include.visibility = View.GONE
                } else {
                    v.background = lessBg
                    include.startAnimation(animationShow)
                    include.visibility = View.VISIBLE
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_spooler, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> finish()
            R.id.action_print ->
                if (printers.size < 1)
                    Snackbar.make(content, R.string.no_able_printer, Snackbar.LENGTH_LONG).show()
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

    fun rangeFilter(range: String): Boolean {
        var regEx = "^(\\d+(-\\d+)?,)*\\d+(-\\d+)?$"
        var pattern = Pattern.compile(regEx)
        var matcher = pattern.matcher(range)
        return matcher.matches()
    }
}
