package com.android.printclient

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.printclient.data.PpdDB
import com.android.printclient.fragment.AddFragment
import com.android.printclient.fragment.MainFragment
import com.android.printclient.objects.Printer
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async

class MainActivity : AppCompatActivity() {

    companion object {
        val SHARED_NAME = "_ppd_init"
        val INIT_NAME = "_init_over"
    }

    var mainFragment: MainFragment = MainFragment()
    var addFragment: AddFragment = AddFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //add fragment
        supportFragmentManager
                .beginTransaction()
                .add(container_CoordinatorLayout.id, MainFragment())
                .commit()
        search_ActionButton.setOnClickListener() {
            //switch fragment
            supportFragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack("add") //for back key
                    .replace(container_CoordinatorLayout.id, AddFragment())
                    .commit()
            //hide fab
            search_ActionButton.visibility = View.GONE
        }

        asyncPpds()

    }

    private fun asyncPpds() {

        val sharedPreference = getSharedPreferences(SHARED_NAME, 0)
        if (sharedPreference.getBoolean(INIT_NAME, false))
            return
        val edit = sharedPreference.edit();
        async() {
            run {
                val db = PpdDB(this@MainActivity)
                db.initPpds()
                edit.putBoolean(INIT_NAME, true).apply()
            }
        }
    }

    override fun onBackPressed() {
        if (!addFragment.isHidden)
            search_ActionButton.visibility = View.VISIBLE
        super.onBackPressed()
    }

}

