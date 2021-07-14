package com.example.mobiledger.data.repository

import android.net.Uri
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.attachment.AttachmentSource
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AttachmentRepository {
    suspend fun uploadPicture(uri: Uri): AppResult<Unit>
    suspend fun downloadProfilePicUri(): AppResult<Uri>
    suspend fun deletePicture(): AppResult<Unit>
}

class AttachmentRepositoryImpl(
    private val attachmentSource: AttachmentSource,
    private val cacheSource: CacheSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    AttachmentRepository {
    override suspend fun uploadPicture(uri: Uri): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                attachmentSource.uploadPicture(uId, uri)
            } else {
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }

    override suspend fun downloadProfilePicUri(): AppResult<Uri> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                attachmentSource.downloadProfilePicUri(uId)
            } else {
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }

    override suspend fun deletePicture(): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                attachmentSource.deletePicture(uId)
            } else {
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }
}