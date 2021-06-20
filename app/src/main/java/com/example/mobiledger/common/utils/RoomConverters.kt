package com.example.mobiledger.common.utils

import androidx.room.TypeConverter
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.example.mobiledger.data.sources.room.categories.DocumentReferenceRoomItem
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.google.firebase.Timestamp

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
        return convertJsonStringToObject<Timestamp>(value) ?: Timestamp.now()
    }

    @TypeConverter
    @JvmStatic
    fun fromDocRefEntityList(value: MutableList<DocumentReferenceRoomItem>?): String? {
        return JsonUtils.convertToJsonString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toDocRefEntityList(value: String?): MutableList<DocumentReferenceRoomItem>? {
        return convertJsonStringToObject<MutableList<DocumentReferenceRoomItem>>(value)
    }
}