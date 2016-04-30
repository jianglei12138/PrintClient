package com.android.printclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.android.printclient.fragment.AddFragment
import com.android.printclient.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mainFragment: MainFragment = MainFragment()
    private var addFragment: AddFragment = AddFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //add fragment
        supportFragmentManager
                .beginTransaction()
                .add(container_CoordinatorLayout.id, MainFragment(), "main-fragment")
                .commit()
        search_ActionButton.setOnClickListener() {
            //switch fragment
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null) //for back key
                    .replace(container_CoordinatorLayout.id, AddFragment())
                    .commit()
            //hide fab
            search_ActionButton.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (!addFragment.isHidden)
            search_ActionButton.visibility = View.VISIBLE
        super.onBackPressed()
    }

}

