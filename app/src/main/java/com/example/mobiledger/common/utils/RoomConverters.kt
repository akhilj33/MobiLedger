package com.example.mobiledger.common.utils

import androidx.room.TypeConverter
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.google.firebase.Timestamp
import java.math.BigDecimal

object RoomConverters {

    /*--------------------------------------TimeStamp Converters--------------------------------------*/

    @TypeConverter
    @JvmStatic
    fun fromTimeStamp(value: Timestamp?): String? {
        return JsonUtils.convertToJsonString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toTimeStamp(value: String?): Timestamp? {
        return convertJsonStringToObject<Timestamp>(value)
    }

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