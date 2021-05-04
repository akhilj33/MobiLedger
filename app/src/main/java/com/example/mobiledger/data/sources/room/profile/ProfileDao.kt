package com.example.mobiledger.data.sources.room.profile

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDao {

    @Query("SELECT count(*) FROM profile")
    suspend fun hasUser(): Int?

    @Query("SELECT * FROM profile")
    suspend fun fetchUserProfile(): UserRoomItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(userRoomItem: UserRoomItem)

    @Query("DELETE FROM profile")
    suspend fun deleteUserProfile()

    @Query("UPDATE profile SET userName=:name WHERE uid=:uId")
    suspend fun updateUserName(name: String, uId:String)

    @Query("UPDATE profile SET emailId=:email WHERE uid=:uId")
    suspend fun updateEmail(email: String, uId:String)

    @Query("UPDATE profile SET phoneNo=:phoneNo WHERE uid=:uId")
    suspend fun updatePhoneNo(phoneNo: String, uId:String)

}