package com.example.mobiledger.common.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_FORMAT_DD__MMMM_yyyy = "dd MMMM yyyy"
    private const val DATE_FORMAT_DD__MMM_yyyy = "dd MMM yyyy"
    private const val DATE_FORMAT_MMMM_yyyy = "MMMM yyyy"
    private const val DATE_FORMAT_MM_yyyy = "MM-yyyy"
    private const val DATE_FORMAT_DD = "DD"

    private val simpleDateFormat_DD_MMM_yyyy = SimpleDateFormat(DATE_FORMAT_DD__MMM_yyyy, Locale.ENGLISH)
    private val simpleDateFormat_MMMM_yyyy = SimpleDateFormat(DATE_FORMAT_MMMM_yyyy, Locale.ENGLISH)
    private val simpleDateFormat_MM_yyyy = SimpleDateFormat(DATE_FORMAT_MM_yyyy, Locale.ENGLISH)
    private val simpleDateFormat_DD_MMMM_yyyy = SimpleDateFormat(DATE_FORMAT_DD__MMMM_yyyy, Locale.ENGLISH)
    private val simpleDateFormat_DD = SimpleDateFormat(DATE_FORMAT_DD, Locale.ENGLISH)


    fun getDateInMMMMyyyyFormat(cal: Calendar): String {
        return simpleDateFormat_MMMM_yyyy.format(cal.time)
    }

    fun getDateInDDFormat(cal: Calendar): String {
        return simpleDateFormat_DD.format(cal.time)
    }

    fun getDateInMMyyyyFormat(cal: Calendar): String {
        return simpleDateFormat_MM_yyyy.format(cal.time)
    }

    fun getCalendarFromMillis(millis: Long): Calendar{
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return cal
    }

    fun getDateInDDMMMMyyyyFormat(timestamp: Timestamp): String {
       return simpleDateFormat_DD_MMMM_yyyy.format(timestamp.toDate())
    }

    fun getDateInDDMMMyyyyFormat(timestamp: Timestamp): String {
        return simpleDateFormat_DD_MMM_yyyy.format(timestamp.toDate())
    }

    fun getCurrentDate(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun isCurrentMonthYear(monthYear: String):Boolean{
        return monthYear==getDateInMMyyyyFormat(getCurrentDate())
    }

}