package com.example.mobiledger.data.sources.attachment

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.mobiledger.common.extention.getFileExtension
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.common.utils.FileUtils
import com.example.mobiledger.common.utils.FileUtils.getCacheDirPath
import com.example.mobiledger.common.utils.FileUtils.getFile
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

interface AttachmentSource {
    suspend fun uploadPicture(uid: String, uri: Uri): AppResult<Unit>
    suspend fun downloadProfilePicUri(uid: String): AppResult<Uri>
}

class AttachmentSourceImpl(
    private val authSource: AuthSource, private val storage: StorageReference,
    private val context: Context
) : AttachmentSource {

    companion object {
        private const val FIREBASE_STORAGE_IMAGE_PATH = "images/profile_pic/"
    }

    override suspend fun uploadPicture(uid: String, uri: Uri): AppResult<Unit> {
        var response: UploadTask? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, ConstantUtils.UNAUTHORIZED_ERROR_MSG)
            val profilePicRef = storage.child(FIREBASE_STORAGE_IMAGE_PATH + uid)
            val metadata = storageMetadata {
                contentType = uri.getFileExtension()
            }
            response = profilePicRef.putFile(uri, metadata)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun downloadProfilePicUri(uid: String): AppResult<Uri> {
        var response: Task<Uri>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, ConstantUtils.UNAUTHORIZED_ERROR_MSG)
            val profilePicRef = storage.child(FIREBASE_STORAGE_IMAGE_PATH + uid)

            response = profilePicRef.downloadUrl
            response.await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data?.result!=null)
                    AppResult.Success(result.data.result!!)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }


}
