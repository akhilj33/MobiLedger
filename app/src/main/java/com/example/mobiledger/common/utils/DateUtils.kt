package com.example.mobiledger.common.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_FORMAT_MMMM_yyyy = "MMMM yyyy"
    private const val DATE_FORMAT_MM_yyyy = "MM-yyyy"

    private val simpleDateFormat_MMMM_yyyy = SimpleDateFormat(DATE_FORMAT_MMMM_yyyy, Locale.ENGLISH)
    private val simpleDateFormat_MM_yyyy = SimpleDateFormat(DATE_FORMAT_MM_yyyy, Locale.ENGLISH)


    fun getDateInMMMMyyyyFormat(cal: Calendar): String {
        return simpleDateFormat_MMMM_yyyy.format(cal.time)
    }

    fun getDateInMMyyyyFormat(cal: Calendar): String {
        return simpleDateFormat_MM_yyyy.format(cal.time)
    }

    fun getCurrentDate(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

}