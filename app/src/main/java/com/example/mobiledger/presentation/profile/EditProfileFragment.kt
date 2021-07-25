package com.example.mobiledger.presentation.profile

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.*
import com.example.mobiledger.common.extention.showAlertDialog
import com.example.mobiledger.common.extention.showToast
import com.example.mobiledger.common.utils.*
import com.example.mobiledger.common.utils.ConstantUtils.PROFILE_PIC_BUNDLE_KEY
import com.example.mobiledger.common.utils.ConstantUtils.PROFILE_PIC_REQUEST_KEY
import com.example.mobiledger.common.utils.FileUtils.Temp_Cache_FolderName
import com.example.mobiledger.common.utils.FileUtils.deleteFileDir
import com.example.mobiledger.common.utils.FileUtils.getCacheDirPath
import com.example.mobiledger.common.utils.FileUtils.getFile
import com.example.mobiledger.common.utils.FileUtils.getType
import com.example.mobiledger.databinding.FragmentEditProfileBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.enums.ProfilePicUpdateType
import com.example.mobiledger.domain.enums.ProfilePicUpdateType.Companion.getProfilePicUpdateType
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.android.material.textfield.TextInputLayout


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, BaseNavigator>(R.layout.fragment_edit_profile, StatusBarColor.BLUE) {

    override fun isBottomNavVisible(): Boolean = false
    private var cameraFileUri: Uri? = null
    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    private val viewModel: EditProfileViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
        viewModel.fetchUserData()
        fragmentResultListener()
    }

    private fun setUpObserver() {
        viewModel.userFromFirebaseResult.observe(viewLifecycleOwner, OneTimeObserver {
            updateProfileUI(it)
        })

        viewModel.profilePhotoUpdateLiveData.observe(viewLifecycleOwner, {
            if (it != null) Glide.with(this).load(it).circleCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewBinding.editImage)
            else Glide.with(this).load(R.drawable.profile_colorful).circleCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewBinding.editImage)
        })

        viewModel.dataUpdatedResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                activity?.showToast(getString(R.string.updated))
            }
        )

        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.editProfileProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.editProfileProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                EditProfileViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })
    }


    private fun setOnClickListener() {
        viewBinding.btnBack.setOnSafeClickListener {
            activity?.onBackPressed()
        }

        viewBinding.btnNameUpdate.setOnSafeClickListener {
            if (it.isEnabled) {
                updateNameViewBasedOnValidation(isValidName())
                if (isValidName()) {
                    viewModel.updateUserName(getNameText())
                }
            }
        }

        viewBinding.btnEmailUpdate.setOnSafeClickListener {
            if (it.isEnabled) {
                updateEmailViewBasedOnValidation(viewBinding.emailLayout, isValidEmail())
                if (isValidEmail()) {
                    viewModel.updateEmail(getEmailText())
                }
            }
        }

        viewBinding.btnContactUpdate.setOnSafeClickListener {
            if (it.isEnabled) {
                updateContactNoViewBasedOnValidation(viewBinding.contactNumLayout, isValidPhone())
                if (isValidPhone()) {
                    viewModel.updatePhoneNo(getContactText())
                }
            }
        }

        viewBinding.changePassword.setOnSafeClickListener {
            sendPasswordResetEmail()
        }
        viewBinding.imgChangePassword.setOnSafeClickListener {
            sendPasswordResetEmail()
        }

        viewBinding.editImage.setOnSafeClickListener {
            if (viewModel.oldPhoto.isNullOrEmpty()) handleProfilePicChange()
            else showUpdateProfilePicDialogFragment(requireActivity().supportFragmentManager)
        }

        viewBinding.nameTv.addTextChangedListener(nameTextWatcher)
        viewBinding.textEmail.addTextChangedListener(emailTextWatcher)
        viewBinding.textContactNum.addTextChangedListener(phoneTextWatcher)
    }

    private fun fragmentResultListener() {
        setFragmentResultListener(PROFILE_PIC_REQUEST_KEY) { requestKey, bundle ->
            if (requestKey == PROFILE_PIC_REQUEST_KEY) {
                val type = bundle.getString(PROFILE_PIC_BUNDLE_KEY)
                if (type == null) Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                else {
                    if (getProfilePicUpdateType(type) == ProfilePicUpdateType.DELETE) {
                        handleProfilePicDeletion()
                    } else handleProfilePicChange()
                }

            }
        }
    }

    private fun handleProfilePicDeletion() {
        viewModel.deleteProfilePic()
        deleteFileDir(getFile(getCacheDirPath(requireContext(), Temp_Cache_FolderName)))
        viewModel.oldPhoto = null
    }

    private fun handleProfilePicChange() {
        checkAndAskForPermissions(
            PermissionUtils.uploadPermissions,
            R.string.permission_required_dialog_msg,
            requestMultiplePermissions, isPermissionsGranted
        )
    }

    private fun updateProfileUI(user: UserEntity) {
        viewBinding.nameTv.setText(user.userName ?: "")
        viewBinding.textEmail.setText(user.emailId ?: "")
        viewBinding.textContactNum.setText(user.phoneNo ?: "")

        if (!user.photoUrl.isNullOrEmpty()) Glide.with(this).load(user.photoUrl).circleCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(viewBinding.editImage)
        else Glide.with(this).load(R.drawable.profile_colorful).circleCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(viewBinding.editImage)
        viewBinding.btnNameUpdate.disable()
        viewBinding.btnEmailUpdate.disable()
        viewBinding.btnContactUpdate.disable()
    }

    private fun sendPasswordResetEmail() {
        activity?.showAlertDialog(
            getString(R.string.change_password),
            getString(R.string.change_password_message),
            getString(R.string.yes),
            getString(R.string.no),
            onCancelButtonClick,
            onContinueClick
        )
    }

    private val onCancelButtonClick = {

    }

    private val onContinueClick = {
        viewModel.sendEmailToResetPassword()
    }

    private fun getNameText(): String = viewBinding.nameTv.text.toString().trim()
    private fun getEmailText(): String = viewBinding.textEmail.text.toString().trim()
    private fun getContactText(): String = viewBinding.textContactNum.text.toString().trim()

    private fun isValidName(): Boolean = getNameText().isNotBlank()
    private fun isValidEmail(): Boolean = (getEmailText().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(getEmailText()).matches())
    private fun isValidPhone(): Boolean = (getContactText().isNotBlank() && ValidationUtils.phoneNoValidator(getContactText()))

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidName()) {
                updateNameViewBasedOnValidation(true)
            }
        }
    }

    private val phoneTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidPhone()) {
                updateContactNoViewBasedOnValidation(viewBinding.contactNumLayout, isValid = true)
            }
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidEmail()) {
                updateEmailViewBasedOnValidation(viewBinding.emailLayout, isValid = true)
            }
        }
    }

    private fun updateNameViewBasedOnValidation(
        isValid: Boolean
    ) {
        if (isValid && getNameText() != viewModel.oldName) {
            viewBinding.btnNameUpdate.enable()
        } else {
            viewBinding.btnNameUpdate.disable()
        }
    }

    private fun updateEmailViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            if (getEmailText() != viewModel.oldEmail) {
                viewBinding.btnEmailUpdate.enable()
                textInputLayout.error = null
            } else viewBinding.btnEmailUpdate.disable()
        } else {
            textInputLayout.error = getString(R.string.email_invalid)
            viewBinding.btnEmailUpdate.disable()
        }
    }

    private fun updateContactNoViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            if (getContactText() != viewModel.oldContactNo) {
                viewBinding.btnContactUpdate.enable()
                textInputLayout.error = null
            } else viewBinding.btnContactUpdate.disable()
        } else {
            textInputLayout.error = getString(R.string.contact_no_invalid)
            viewBinding.btnContactUpdate.disable()
        }
    }

    /*-------------------------Permission handling------------------------------------------------*/

    private fun startChooser() {
        cameraFileUri = FileShareUtils.getCaptureImageOutputUri(requireContext(), viewModel.uId)
        cameraFileUri?.let {
            activityResultLauncher.launch(FileShareUtils.getFileChooserIntent(requireContext(), it))
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                when {
                    it.data?.data != null -> {
                        val uri = it.data?.data
                        val extension = uri?.getType(requireContext()) ?: uri?.getFileExtension()
                        if (extension == null) handleFileUri(null)
                        else handleFileUri(
                            it.data?.data?.getFile(requireContext(), "profile_pic.${getType(extension)}")?.toUri()
                        )
                    }
                    else -> handleFileUri(cameraFileUri)
                }
            }
        }

    private fun handleFileUri(imageURi: Uri?) {
        if (imageURi == null) {
            activity?.showToast(getString(R.string.something_went_wrong))
            return
        }
        viewModel.oldPhoto = imageURi.toString()
        viewModel.uploadProfilePic(imageURi)
    }

    private val isPermissionsGranted = fun(isGranted: Boolean) {
        if (isGranted) startChooser()
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.contains(false)) {
            requireActivity().showToast(getString(R.string.required_permission_not_granted))
        } else {
            startChooser()
        }
    }

    companion object {
        fun newInstance() = EditProfileFragment()
    }
}