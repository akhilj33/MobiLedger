package com.example.mobiledger.data.sources.room.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobiledger.domain.enums.SignInType

@Entity(tableName = "profile")
data class UserRoomItem(
    @PrimaryKey @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "userName") val userName: String? = null,
    @ColumnInfo(name = "photoUrl") val photoUrl: String? = null,
    @ColumnInfo(name = "emailId") val emailId: String? = null,
    @ColumnInfo(name = "phoneNo") val phoneNo: String? = null,
    @ColumnInfo(name = "signInType") val signInType: SignInType
)
