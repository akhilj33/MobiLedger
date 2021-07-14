package com.example.mobiledger.presentation.profile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.usecases.AttachmentUseCase
import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val profileUseCase: ProfileUseCase,
    private val authUseCase: AuthUseCase,
    private val attachmentUseCase: AttachmentUseCase
) : BaseViewModel() {

    val userFromFirebaseResult: LiveData<Event<UserEntity>> get() = _userFromFirebaseResult
    private val _userFromFirebaseResult: MutableLiveData<Event<UserEntity>> = MutableLiveData()

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    val profilePhotoUpdateLiveData: LiveData<Uri?> get() = _profilePhotoUpdateLiveData
    private val _profilePhotoUpdateLiveData: MutableLiveData<Uri?> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _emailSent = MutableLiveData<Unit>()
    val emailSent: LiveData<Unit> get() = _emailSent

    lateinit var uId: String
    lateinit var oldName: String
    lateinit var oldEmail: String
    lateinit var oldContactNo: String
    var oldPhoto: String? = null


    fun fetchUserData() {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = profileUseCase.fetchUserFromFirebase()) {
                is AppResult.Success -> {
                    uId = result.data.uid
                    oldName = result.data.userName ?: ""
                    oldEmail = result.data.emailId ?: ""
                    oldContactNo = result.data.phoneNo ?: ""
                    oldPhoto = result.data.photoUrl
                    _userFromFirebaseResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
            _loadingState.value = false
        }
    }

    fun updateUserName(userName: String) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = profileUseCase.updateUserNameInFirebase(userName)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
            _loadingState.value = false
        }
    }

    fun updateEmail(email: String) {
        viewModelScope.launch {
            _loadingState.value = true

            when (val result = profileUseCase.updateEmailInFirebase(email)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
            _loadingState.value = false
        }
    }

    fun updatePhoneNo(phone: String) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = profileUseCase.updatePhoneInFirebase(phone)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
            _loadingState.value = false
        }
    }

    fun sendEmailToResetPassword() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = authUseCase.sendPasswordResetEmail(userFromFirebaseResult.value?.peekContent()?.emailId.toString())) {
                is AppResult.Success -> {
                    _emailSent.value = Unit
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
        }
        _loadingState.value = false
    }

    fun uploadProfilePic(uri: Uri) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = attachmentUseCase.uploadPicture(uri)) {
                is AppResult.Success -> {
                    when(val downloadResult = attachmentUseCase.downloadProfilePicUri()){
                        is AppResult.Success -> {
                            _profilePhotoUpdateLiveData.value = downloadResult.data
                            _loadingState.value = false
                        }
                        is AppResult.Failure -> {
                            if (needToHandleAppError(downloadResult.error)) {
                                _errorLiveData.value = Event(
                                    ViewError(
                                        viewErrorType = ViewErrorType.NON_BLOCKING,
                                        message = downloadResult.error.message
                                    )
                                )
                            }
                            _loadingState.value = false
                        }
                    }
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = result.error.message
                            )
                        )
                    }
                    _loadingState.value = false
                }
            }
        }
    }

    fun deleteProfilePic() {
        _loadingState.value = true
        viewModelScope.launch {
            when(val result = attachmentUseCase.deletePicture()){
                is AppResult.Success -> {
                    _profilePhotoUpdateLiveData.value = null
                    _loadingState.value = false
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = result.error.message
                            )
                        )
                    }
                    _loadingState.value = false
                }
            }
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}
