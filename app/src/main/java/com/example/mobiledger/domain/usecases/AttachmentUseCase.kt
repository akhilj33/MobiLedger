package com.example.mobiledger.domain.usecases

import android.net.Uri
import com.example.mobiledger.data.repository.AttachmentRepository
import com.example.mobiledger.domain.AppResult

interface AttachmentUseCase {
    suspend fun uploadPicture(uri: Uri): AppResult<Unit>
    suspend fun downloadProfilePicUri(): AppResult<Uri>
}

class AttachmentUseCaseImpl(private val attachmentRepository: AttachmentRepository,
                            private val profileUseCase: ProfileUseCase) :
    AttachmentUseCase {
    override suspend fun uploadPicture(uri: Uri): AppResult<Unit> {
        return attachmentRepository.uploadPicture(uri)
    }

    override suspend fun downloadProfilePicUri(): AppResult<Uri> {
        return when(val result = attachmentRepository.downloadProfilePicUri()){
            is AppResult.Success -> profileUseCase.updatePhotoInAuth(result.data)
            is AppResult.Failure -> result
        }
    }
}