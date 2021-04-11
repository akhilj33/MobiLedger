package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.ValidationUtils
import com.example.mobiledger.databinding.FragmentSignUpBinding
import com.example.mobiledger.presentation.OneTimeObserver
import java.util.regex.Pattern


class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpNavigator>(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
        setUpObserver()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setUpObserver() {
        viewModel.signUpResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                navigator?.launchDashboard()
            })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                activity?.showToast(getString(R.string.signup_failed))
            }
        })
    }

    private fun setOnClick() {

        viewBinding.btnSignup.setOnClickListener {
            val name = viewBinding.nameEditText.text.toString()
            val email = viewBinding.textEmail.text.toString()
           val password = viewBinding.textPassword.text.toString()
           val  phoneNo = viewBinding.phoneEditText.text.toString()
           val confirmPassword = viewBinding.textConfirmPassword.text.toString()
            doValidations(name, email, password, confirmPassword, phoneNo)
        }
    }

    private fun doValidations(name:String, email:String, password:String, confirmPassword:String, phoneNo:String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank())
            activity?.showToast(getString(R.string.empty_field_msg))
        else if (password != confirmPassword)
            activity?.showToast(getString(R.string.confirm_password_mismatched_msg))
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            activity?.showToast(getString(R.string.incorrect_email))
        else if (!ValidationUtils.passwordValidator(password))
            activity?.showToast(getString(R.string.password_requirement))
        else if(phoneNo.isNotBlank() && !ValidationUtils.phoneNoValidator(phoneNo))
            activity?.showToast(getString(R.string.incorrect_phone_no))
        else {
            viewModel.signUpViaEmail(name, phoneNo, email, password)
        }
    }

    companion object {
        fun newInstance() = SignUpFragment()
    }


}