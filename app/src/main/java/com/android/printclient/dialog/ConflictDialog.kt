package com.android.printclient.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.android.printclient.R

/**
 * Created by jianglei on 16/6/9.
 */
class ConflictDialog : Dialog {

    constructor(context: Context) : super(context) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog_conflict)

    }
}