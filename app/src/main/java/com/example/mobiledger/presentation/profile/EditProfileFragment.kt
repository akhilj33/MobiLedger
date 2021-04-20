package com.example.mobiledger.presentation.profile

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.ValidationUtils
import com.example.mobiledger.databinding.FragmentEditProfileBinding


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, BaseNavigator>(R.layout.fragment_edit_profile) {

    override fun isBottomNavVisible(): Boolean = false

    private val viewModel: EditProfileViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        setUpObserver()
        viewModel.getUIDForProfile()
    }


    private fun setUpObserver() {
        viewModel.dataUpdatedResult.observe(
            viewLifecycleOwner,
            Observer {
                activity?.showToast(getString(R.string.updated))
            }
        )
    }


    private fun setOnClickListener() {
        viewBinding.btnNameUpdate.setOnClickListener {
            val userName = viewBinding.textName.text.toString()
            if (userName.isEmpty()) {
                activity?.showToast(getString(R.string.single_empty_field_msg))
            } else {
                viewModel.updateUserName(viewBinding.textName.text.toString())
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

        viewBinding.btnPasswordUpdate.setOnClickListener {
            val password = viewBinding.textPassword.text.toString()
            val confirmPassword = viewBinding.textConfirmPassword.text.toString()
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                activity?.showToast(getString(R.string.single_empty_field_msg))
            } else if (password != confirmPassword) {
                activity?.showToast(getString(R.string.confirm_password_mismatched_msg))
            } else if (!ValidationUtils.passwordValidator(password)) {
                activity?.showToast(getString(R.string.password_requirement))
            } else {
                viewModel.updatePassword(viewBinding.textPassword.text.toString())
            }
        }
    }


    companion object {
        fun newInstance() = EditProfileFragment()
    }
}