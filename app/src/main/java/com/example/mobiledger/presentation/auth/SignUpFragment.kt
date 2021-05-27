package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.ValidationUtils
import com.example.mobiledger.databinding.FragmentSignUpBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.android.material.textfield.TextInputLayout


class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpNavigator>(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
        setUpObserver()
    }

    override fun isBottomNavVisible(): Boolean = false
    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    private fun setUpObserver() {
        viewModel.signUpResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                navigator?.launchDashboard()
            })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                SignUpViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), false)
                }
            }
        })

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.signUpProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.signUpProgressBar.visibility = View.GONE
            }
        })
    }

    private fun setOnClick() {

        viewBinding.apply {
            btnSignup.setOnClickListener {
                signUp(getNameText(), getEmailText(), getPasswordText(), getConfirmPasswordText(), getContactText())
            }

            nameEditText.addTextChangedListener(nameTextWatcher)
            textEmail.addTextChangedListener(emailTextWatcher)
            textPassword.addTextChangedListener(passwordTextWatcher)
            textConfirmPassword.addTextChangedListener(confirmPasswordTextWatcher)
            phoneEditText.addTextChangedListener(phoneTextWatcher)
        }
    }

    private fun signUp(name: String, email: String, password: String, confirmPassword: String, phoneNo: String) {
        if (doValidations()) {
            viewModel.signUpViaEmail(name, phoneNo, email, password)
        } else if (!ValidationUtils.passwordValidator(password) && password.isNotEmpty())
            activity?.showToast(getString(R.string.password_requirement))
        else if (password != confirmPassword && confirmPassword.isNotEmpty())
            activity?.showToast(getString(R.string.confirm_password_mismatched_msg))
    }

    private fun getNameText(): String = viewBinding.nameEditText.text.toString()
    private fun getEmailText(): String = viewBinding.textEmail.text.toString()
    private fun getContactText(): String = viewBinding.phoneEditText.text.toString()
    private fun getPasswordText(): String = viewBinding.textPassword.text.toString()
    private fun getConfirmPasswordText(): String = viewBinding.textConfirmPassword.text.toString()

    private fun isValidName(): Boolean = getNameText().isNotBlank()
    private fun isValidEmail(): Boolean = (getEmailText().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(getEmailText()).matches())
    private fun isValidPhone(): Boolean = (getContactText().isNotBlank() && ValidationUtils.phoneNoValidator(getContactText()))
    private fun isValidPassword(): Boolean = (getPasswordText().isNotBlank() && ValidationUtils.passwordValidator(getPasswordText()))
    private fun isValidConfirmPassword(): Boolean =
        (getPasswordText().isNotBlank() && ValidationUtils.passwordValidator(getPasswordText()) && getPasswordText() == (getConfirmPasswordText()))

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidName()) {
                updateViewBasedOnValidation(viewBinding.emailLayout, isValid = true)
            }
        }
    }

    private val phoneTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidPhone()) {
                updateViewBasedOnValidation(viewBinding.emailLayout, isValid = true)
            }
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidEmail()) {
                updateViewBasedOnValidation(viewBinding.emailLayout, isValid = true)
            }
        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidPassword()) {
                updateViewBasedOnValidation(viewBinding.passwordLayout, isValid = true)
            }
        }
    }

    private val confirmPasswordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidConfirmPassword()) {
                updateViewBasedOnValidation(viewBinding.emailLayout, isValid = true)
            }
        }
    }

    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateViewBasedOnValidation(viewBinding.nameInputLayout, isValidName())
        updateViewBasedOnValidation(viewBinding.emailLayout, isValidEmail())
        updateViewBasedOnValidation(viewBinding.phoneInputLayout, isValidPhone())
        updateViewBasedOnValidation(viewBinding.passwordLayout, isValidPassword())
        updateViewBasedOnValidation(viewBinding.confirmPasswordLayout, isValidConfirmPassword())

        return isValidName() && isValidEmail() && isValidPhone() && isValidPassword() && isValidConfirmPassword()
    }

    private fun updateViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            textInputLayout.error = null
        } else {
            textInputLayout.error = getString(R.string.field_invalid)
        }
    }

    companion object {
        fun newInstance() = SignUpFragment()
    }


}