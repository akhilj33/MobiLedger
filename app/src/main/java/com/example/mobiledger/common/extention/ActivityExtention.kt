package com.example.mobiledger.common

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.mobiledger.R

private const val COPY_TO_CLIPBOARD = "copy_to_clipboard"

fun Activity.showDialog(
    dialogTitle: String, dialogMessage: String,
    positiveButtonText: String, NegativeButtonText: String,
    onCancelButtonClick: () -> Unit, onContinueClick: () -> Unit
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(dialogTitle)
    builder.setMessage(dialogMessage)
    builder.setPositiveButton(positiveButtonText) { dialogInterface, _ ->
        onContinueClick()
        dialogInterface.dismiss()
    }
    builder.setNegativeButton(NegativeButtonText) { dialogInterface, _ ->
        onCancelButtonClick()
        dialogInterface.cancel()
    }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setOnShowListener {
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun Activity.showToast(msg: String) {
    val toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
    toast.setGravity(Gravity.BOTTOM, 0, 250)
    toast.show()
}

fun Window.getSoftInputMode(): Int {
    return attributes.softInputMode
}

fun Activity.copyTextToClipBoard(text: String) {
    val clipboard =
        this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(COPY_TO_CLIPBOARD, text)
    clipboard.setPrimaryClip(clipData)
}

fun Activity.closePreviousDialogFragment(dialogFragmentName: String) {
    val prev: Fragment? =
        (this as FragmentActivity).supportFragmentManager.findFragmentByTag(dialogFragmentName)
    if (prev != null) {
        val df: DialogFragment = prev as DialogFragment
        df.dismiss()
    }
}

