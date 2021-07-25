package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.databinding.DialogFragmentForgetPasswordBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.android.material.textfield.TextInputLayout

class ForgetPasswordDialogFragment : BaseDialogFragment<DialogFragmentForgetPasswordBinding, BaseNavigator>
    (R.layout.dialog_fragment_forget_password) {

    private val viewModel: ForgetPasswordViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.forgetPasswordProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.forgetPasswordProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                ForgetPasswordViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })
    }


    private fun setOnClickListeners() {
        viewBinding.apply {
            btnSend.setOnSafeClickListener {
                if (doValidations()) {
                    sendPasswordResetEmail()
                }
            }

            textEmail.addTextChangedListener(emailTextWatcher)
        }
    }

    private fun sendPasswordResetEmail() {
        viewModel.sendEmailToResetPassword(getEmailText())
    }

    private fun getEmailText(): String = viewBinding.textEmail.text.toString()

    private fun isValidEmail(): Boolean = (getEmailText().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(getEmailText()).matches())

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidEmail()) {
                updateViewBasedOnValidation(viewBinding.emailLayout, isValid = true)
            }
        }
    }

    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateViewBasedOnValidation(viewBinding.emailLayout, isValidEmail())
        return isValidEmail()
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
        fun newInstance() = ForgetPasswordDialogFragment()
    }
}