package com.example.mobiledger.data.sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mobiledger.common.utils.RoomConverters
import com.example.mobiledger.data.sources.room.profile.ProfileDao
import com.example.mobiledger.data.sources.room.profile.UserRoomItem


@Database(entities = [UserRoomItem::class], version = 1)
@TypeConverters(RoomConverters::class)
abstract class MobiLedgerDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}