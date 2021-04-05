package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showToast
import com.example.mobiledger.databinding.FragmentSignUpBinding
import com.example.mobiledger.presentation.OneTimeObserver
import java.util.regex.Pattern


class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpNavigator>(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by viewModels { viewModelFactory }

    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.signUpResult.observe(
            viewLifecycleOwner,
            OneTimeObserver { isSignUpSuccess ->
                if (isSignUpSuccess != null) {
                    navigator?.navigateAuthToHomeScreen()
                }
            })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                activity?.showToast("Sign up Failed")
            }
        })
    }

    private fun setOnClick() {

        viewBinding.btnSignup.setOnClickListener {
            email = viewBinding.textEmail.text.toString()
            password = viewBinding.textPassword.text.toString()
            confirmPassword = viewBinding.textConfirmPassword.text.toString()
            emailAndPasswordValidation(email, password, confirmPassword)
        }
    }

    private fun emailAndPasswordValidation(
        email: String?,
        password: String?,
        confirmPassword: String?
    ) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty())
            activity?.showToast("You cannot leave any field empty")
        else if (password != confirmPassword)
            activity?.showToast("Password and Conform password should be same")
        else if (!emailValidator(email))
            activity?.showToast("Incorrect Email")
        else if (!passwordValidator(password))
            activity?.showToast(
                "Password should be of size 8-20 \n" +
                        "It should contain at least a digit \n" +
                        "It should have a lower case letter and upper case letter \n" +
                        "It should have a special character"
            )
        else {
            viewModel.signUpViaEmail(email, password)
        }

    }

    private fun emailValidator(email: String?): Boolean {

        val emailPattern: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun passwordValidator(password: String?): Boolean {

        val emailPattern: Pattern = Pattern.compile(
            "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[@#$%^&+=])"
                    + "(?=\\S+$).{8,20}$"
        )

        return emailPattern.matcher(password).matches()
    }


    companion object {
        fun newInstance() = SignUpFragment()
    }


}