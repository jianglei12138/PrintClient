package com.android.printclient.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.*
import com.android.printclient.MainActivity
import com.android.printclient.R
import com.android.printclient.objects.Conflict
import com.android.printclient.view.adapter.TabAdapter
import java.util.*

/**
 * Created by jianglei on 16/5/23.
 */
class OptionFragment : Fragment() {

    init {
        System.loadLibrary("options")
    }

    external fun getOptionGroups(name: String): ArrayList<String>
    external fun release()
    external fun getConflictData(name: String): ArrayList<Conflict>

    var printer: String? = null


    var tabs: TabLayout? = null

    var listConflict = ArrayList<Conflict>()

    var animatorSet = AnimatorSet()
    var isConflict: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        printer = bundle.getString("printer")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_options, container, false)

        if (activity is MainActivity) {
            var act = activity
            act.title = getString(R.string.set_options)
            tabs = act.findViewById(R.id.tabs) as TabLayout
            tabs!!.visibility = View.VISIBLE
        }

        var list = getOptionGroups(printer!!)

        listConflict.clear()
        listConflict = getConflictData(printer!!)
        if (listConflict.size > 0) isConflict = true

        val adapter = TabAdapter(childFragmentManager, list, printer!!, context)

        val mViewPager = view.findViewById(R.id.viewPager) as ViewPager
        mViewPager.adapter = adapter

        tabs!!.tabMode = TabLayout.MODE_SCROLLABLE
        tabs!!.setupWithViewPager(mViewPager)

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        if (isConflict) {
            menu!!.findItem(R.id.action_alert).isVisible = true
            showAlertAnimation(menu.findItem(R.id.action_alert))
        } else {
            menu!!.findItem(R.id.action_alert).isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_alert -> {

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertAnimation(menuItem: MenuItem) {

        hideAlertAnimation(menuItem)

        var alertView = View.inflate(context, R.layout.toolbar_alert, null)

        menuItem.actionView = alertView

        alertView.setOnClickListener { v ->
            v.clearAnimation()
            var conflicts = ArrayList<String>()
            listConflict.forEach {
                conflicts.add(it.text + ": " + it.choice)
            }
            AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.conflict_message))
                    .setItems(conflicts.toTypedArray(), null)
                    .setPositiveButton(context.getString(R.string.ok),null)
                    .show()
        }


        var counter = 0
        var anim1 = ObjectAnimator.ofFloat(alertView, "translationX", 0f, -4f);
        var anim2 = ObjectAnimator.ofFloat(alertView, "translationX", 0f, 8f);
        var anim3 = ObjectAnimator.ofFloat(alertView, "translationX", 0f, 4f);
        animatorSet.play(anim1).before(anim2).before(anim3)
        animatorSet.duration = 50
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (counter < 5) {
                    counter++
                    animatorSet.start()
                } else {
                    counter = 0
                    Handler().postDelayed({ animatorSet.start() }, 1000)
                }
            }
        })
        animatorSet.start()
    }

    private fun hideAlertAnimation(menuItem: MenuItem) {
        var alertView = menuItem.actionView
        if (alertView != null)
            alertView.clearAnimation()
        menuItem.actionView = null
    }
}