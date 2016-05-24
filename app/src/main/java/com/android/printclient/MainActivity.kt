package com.android.printclient

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.printclient.data.PpdDB
import com.android.printclient.dialog.DeviceDialog
import com.android.printclient.fragment.AddFragment
import com.android.printclient.fragment.MainFragment
import com.android.printclient.utility.Permission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async

class MainActivity : AppCompatActivity() {

    companion object {
        val SHARED_NAME = "_ppd_init"
        val INIT_NAME = "_init_over"
        val FILE_SELECT_CODE = 0x11;
        val CHOOSE_PPD_ACTION = "com.android.printclient.choose.ppd"
        val READ_STORAGE_CODE = 200
    }

    var mainFragment: MainFragment = MainFragment()
    var addFragment: AddFragment = AddFragment()

    var ppdReceiver = DeviceDialog.ResultPPDFile()

    interface RequestCallback {
        fun onResult(result: Boolean)
    }

    var requestCallback: MainActivity.RequestCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            //add fragment
            supportFragmentManager
                    .beginTransaction()
                    .add(container_CoordinatorLayout.id, mainFragment)
                    .commit()
        }
        search_ActionButton.setOnClickListener() {

            var hasReadStoragePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
                //request permission
                Permission.requestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), this, this, MainActivity.READ_STORAGE_CODE)
            } else {
                //switch fragment
                supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack("add") //for back key
                        .replace(container_CoordinatorLayout.id, addFragment)
                        .commit()
                //hide fab
                search_ActionButton.visibility = View.GONE
            }
        }

        //filter for choose ppd
        var ppdfilter = IntentFilter()
        ppdfilter.addAction(CHOOSE_PPD_ACTION)
        ppdfilter.priority = Int.MAX_VALUE
        registerReceiver(ppdReceiver, ppdfilter)

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
        if (!mainFragment.isVisible && addFragment.isVisible)
            search_ActionButton.visibility = View.VISIBLE
        else
            search_ActionButton.visibility = View.GONE
        tabs.visibility = View.GONE
        super.onBackPressed()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE && data != null) {
            var resultIntent = Intent(CHOOSE_PPD_ACTION)
            resultIntent.putExtra("ppd", data.data.toString());
            sendBroadcast(resultIntent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(ppdReceiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_CODE && Permission.checkRequestPermission(grantResults[0])) {
            //switch fragment
            supportFragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack("add") //for back key
                    .replace(container_CoordinatorLayout.id, addFragment)
                    .commitAllowingStateLoss()
            //hide fab
            search_ActionButton.visibility = View.GONE

        } else {
            Snackbar.make(container_CoordinatorLayout, R.string.attention_storage_permission, Snackbar.LENGTH_LONG).show();
        }
    }


}

