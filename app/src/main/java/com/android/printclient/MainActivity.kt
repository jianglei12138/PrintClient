package com.android.printclient

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.printclient.data.PpdDB
import com.android.printclient.dialog.DeviceDialog
import com.android.printclient.fragment.AddFragment
import com.android.printclient.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async

class MainActivity : AppCompatActivity() {

    companion object {
        val SHARED_NAME = "_ppd_init"
        val INIT_NAME = "_init_over"
        val FILE_SELECT_CODE = 0x11;
        val CHOOSE_PPD_ACTION = "com.android.printclient.choose.ppd"
    }

    var mainFragment: MainFragment = MainFragment()
    var addFragment: AddFragment = AddFragment()
    var receiver = DeviceDialog.ResultPPDFile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            //add fragment
            supportFragmentManager
                    .beginTransaction()
                    .add(container_CoordinatorLayout.id, MainFragment())
                    .commit()
        }
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

        var filter = IntentFilter()
        filter.addAction(CHOOSE_PPD_ACTION)
        filter.priority = Int.MAX_VALUE
        registerReceiver(receiver, filter)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE && data != null) {
            var resultIntent = Intent(CHOOSE_PPD_ACTION)
            //data.action = CHOOSE_PPD_ACTION
            resultIntent.putExtra("ppd", data.data.toString());
            sendBroadcast(resultIntent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

