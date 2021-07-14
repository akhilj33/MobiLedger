package com.example.mobiledger.data.sources.room.profile

import android.net.Uri
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity

interface ProfileDb {
    suspend fun fetchUserProfile(): AppResult<UserEntity>
    suspend fun saveUser(userInfo: UserEntity)
    suspend fun hasUser(): Boolean
    suspend fun updateUserName(username: String, uId: String)
    suspend fun updatePhoto(photoUri: Uri?, uId: String)
    suspend fun updateEmailId(emailId: String, uId: String)
    suspend fun updatePhoneNo(phoneNo: String, uId: String)

}

class ProfileDbImpl(private val dao: ProfileDao) : ProfileDb {
    override suspend fun fetchUserProfile(): AppResult<UserEntity> {
        val userProfile = dao.fetchUserProfile()?.let {
            mapToUserEntity(it)
        }
        return if (userProfile != null) AppResult.Success(userProfile)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun saveUser(userInfo: UserEntity) {
        val userRoomItem = mapToUserRoomEntity(userInfo)
        dao.deleteUserProfile()
        dao.saveUser(userRoomItem)
    }

    override suspend fun hasUser(): Boolean {
        val userCount = dao.hasUser()
        return !(userCount == null || userCount == 0)
    }

    override suspend fun updateUserName(username: String, uId: String) {
        dao.updateUserName(username, uId)
    }

    override suspend fun updatePhoto(photoUri: Uri?, uId: String) {
        dao.updatePhotoUri(photoUri?.toString(), uId)
    }

    override suspend fun updateEmailId(emailId: String, uId: String) {
        dao.updateEmail(emailId, uId)
    }

    override suspend fun updatePhoneNo(phoneNo: String, uId: String) {
        dao.updatePhoneNo(phoneNo, uId)
    }

}

private fun mapToUserEntity(roomItem: UserRoomItem): UserEntity {
    roomItem.apply {
        return UserEntity(uid, userName, photoUrl, emailId, phoneNo, signInType)
    }
}

private fun mapToUserRoomEntity(userEntity: UserEntity): UserRoomItem {
    userEntity.apply {
        return UserRoomItem(uid, userName, photoUrl, emailId, phoneNo, signInType)
    }
}