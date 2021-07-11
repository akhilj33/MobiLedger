package com.example.mobiledger.presentation.auth

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.ValidationUtils
import com.example.mobiledger.common.utils.showForgetPasswordDialog
import com.example.mobiledger.databinding.FragmentLoginBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginNavigator>(R.layout.fragment_login, StatusBarColor.BLUE) {

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setOnClickListener() {
        viewBinding.apply {
            btnLogin.setOnClickListener {
                loginWithEmail()
            }

            btnGoogleSignInView.setOnClickListener {
                initSignInWithGoogle()
            }

            tvForgetPassword.setOnClickListener {
                sendResetPasswordEmail()
            }

            textPassword.addTextChangedListener(passwordTextWatcher)
            textEmail.addTextChangedListener(emailTextWatcher)
        }
    }

    private fun sendResetPasswordEmail() {
        showForgetPasswordDialog(requireActivity().supportFragmentManager)
    }

    private fun setUpObserver() {
        viewModel.signInResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                navigator?.launchDashboard()
            })

        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.loginProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.loginProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                LoginViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })
    }


    private fun initSignInWithGoogle() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    viewModel.signInUserViaGoogle(task.result?.idToken)
                } catch (e: ApiException) {
                    //todo decide what to do when google sign in fails
                }
            }
        }

    private fun getEmailText(): String = viewBinding.textEmail.text.toString().trim()
    private fun getPasswordText(): String = viewBinding.textPassword.text.toString()

    private fun isValidEmail(): Boolean = (getEmailText().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(getEmailText()).matches())
    private fun isValidPassword(): Boolean = (getPasswordText().isNotBlank() && ValidationUtils.passwordValidator(getPasswordText()))

    private fun loginWithEmail() {
        if (doValidations()) {
            viewModel.loginUserViaEmail(getEmailText(), getPasswordText())
        }
    }

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

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidPassword()) {
                updateViewBasedOnValidation(viewBinding.passwordLayout, isValid = true)
            }
        }
    }


    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateViewBasedOnValidation(viewBinding.emailLayout, isValidEmail())
        updateViewBasedOnValidation(viewBinding.passwordLayout, isValidPassword())
        return isValidEmail() && isValidPassword()
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
        fun newInstance() = LoginFragment()
    }
}