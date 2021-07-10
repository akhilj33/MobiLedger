package com.example.mobiledger.presentation.profile

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.showAlertDialog
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.ValidationUtils
import com.example.mobiledger.databinding.FragmentEditProfileBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, BaseNavigator>(R.layout.fragment_edit_profile, StatusBarColor.BLUE) {

    override fun isBottomNavVisible(): Boolean = false
    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    private val viewModel: EditProfileViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        setUpObserver()
        viewModel.fetchUserData()
    }


    private fun setUpObserver() {
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

        viewBinding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewBinding.btnNameUpdate.setOnClickListener {
            val userName = viewBinding.nameTv.text.toString()
            if (userName.isEmpty()) {
                activity?.showToast(getString(R.string.single_empty_field_msg))
            } else {
                viewModel.updateUserName(viewBinding.nameTv.text.toString())
            }
        }

        viewBinding.btnEmailUpdate.setOnClickListener {
            val email = viewBinding.textEmail.text.toString()
            if (email.isEmpty()) {
                activity?.showToast(getString(R.string.single_empty_field_msg))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                activity?.showToast(getString(R.string.incorrect_email))
            else if (email.isNotEmpty())
                viewModel.updateEmail(viewBinding.textEmail.text.toString())
        }

        viewBinding.btnContactUpdate.setOnClickListener {
            val phoneNo = viewBinding.textContactNum.text.toString()
            if (phoneNo.isEmpty()) {
                activity?.showToast(getString(R.string.single_empty_field_msg))
            } else if (!ValidationUtils.phoneNoValidator(phoneNo)) {
                activity?.showToast(getString(R.string.incorrect_phone_no))
            } else {
                viewModel.updatePhoneNo(viewBinding.textContactNum.text.toString())
            }
        }

        viewBinding.changePassword.setOnClickListener {
            sendPasswordResetEmail()
        }
        viewBinding.imgChangePassword.setOnClickListener {
            sendPasswordResetEmail()
        }
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

    companion object {
        fun newInstance() = EditProfileFragment()
    }
}