package com.example.mobiledger.common.utils

import androidx.room.TypeConverter
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.google.firebase.Timestamp
import java.math.BigDecimal

object RoomConverters {

    /*--------------------------------------Income Expanse EntityConverters--------------------------------------*/

    @TypeConverter
    @JvmStatic
    fun fromIncomeCategoryEntity(value: IncomeCategoryListEntity?): String? {
        return JsonUtils.convertToJsonString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toIncomeCategoryEntity(value: String?): IncomeCategoryListEntity? {
        return convertJsonStringToObject<IncomeCategoryListEntity>(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromExpenseCategoryEntity(value: ExpenseCategoryListEntity?): String? {
        return JsonUtils.convertToJsonString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toExpenseCategoryEntity(value: String?): ExpenseCategoryListEntity? {
        return convertJsonStringToObject<ExpenseCategoryListEntity>(value)
    }

    /*-------------------------------------- Converters--------------------------------------*/

    @TypeConverter
    @JvmStatic
    fun fromTimeStamp(value: Timestamp?): String? {
        return JsonUtils.convertToJsonString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toTimeStamp(value: String?): Timestamp {
        return convertJsonStringToObject<Timestamp>(value)?:Timestamp.now()
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