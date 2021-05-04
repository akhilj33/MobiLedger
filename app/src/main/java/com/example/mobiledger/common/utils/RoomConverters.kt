package com.example.mobiledger.common.utils

import androidx.room.TypeConverter
import java.math.BigDecimal

object RoomConverters {

    /*--------------------------------------Big Decimal Converters--------------------------------------*/

    @TypeConverter
    @JvmStatic
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.toBigDecimalOrNull()
    }

    /*--------------------------------------Double Converters--------------------------------------*/

    @TypeConverter
    @JvmStatic
    fun fromDouble(value: Double?): String? {
        return value.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toDouble(value: String?): Double? {
        return value?.toDoubleOrNull()
    }
}