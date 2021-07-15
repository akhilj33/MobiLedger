package com.example.mobiledger.common.extention

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun View.hideKeyboard() {
    context?.let {
        val inputMethodManager =
            it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

@Suppress("DEPRECATION")
fun View.changeStatusBarColor(
    activity: Activity,
    color: BaseFragment.StatusBarColor,
    isFullScreen: Boolean = false
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        // on devices below API 23 set color to black
        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.colorAppBlue)
    } else {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        post {
            activity.window.decorView.systemUiVisibility = 0
            if (isFullScreen) {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else if (color.isLightColor) {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            activity.window.statusBarColor = ContextCompat.getColor(activity, color.color)
        }
    }
}


fun Long.toAmount(): String {
    return if (this >= 0) "\u20B9" + this.toString()
    else "-" + "\u20B9" + this.absoluteValue.toString()
}

fun String.toPercent() = "$this \u0025"
fun Float.roundToOneDecimal(): String = " %.1f".format(this)

fun Long.toPercent(total: Long) = ((this.toFloat() / total) * 100).roundToInt()