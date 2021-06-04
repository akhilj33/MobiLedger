package com.example.mobiledger.common.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import java.util.*


object AnimationDialogUtils {

    fun animatedDialog(activity: Activity, layout: Int, disappearTime: Long) {

        val builder = AlertDialog.Builder(activity)
        val view: View = LayoutInflater.from(activity)
            .inflate(layout, null, false)
        builder.setView(view)
        val dialog = builder.create()
        Objects.requireNonNull(dialog.window)!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, disappearTime)
    }
}