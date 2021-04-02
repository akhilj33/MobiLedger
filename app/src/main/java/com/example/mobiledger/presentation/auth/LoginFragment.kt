package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showToast
import com.example.mobiledger.databinding.FragmentLoginBinding
import com.example.mobiledger.presentation.OneTimeObserver


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginNavigator>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private var email: String? = null
    private var password: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
    }

    private fun setOnClickListener() {

        viewBinding.btnLogin.setOnClickListener {
            email = viewBinding.textEmail.text.toString()
            password = viewBinding.textPassword.text.toString()
            if (email.isNullOrEmpty() || password.isNullOrEmpty())
                activity?.showToast("No field can be empty")
            else
                viewModel.loginUserViaEmail(email!!, password!!)
        }

        viewBinding.tvSignUp.setOnClickListener {
            navigator?.navigateToSignUpScreen()
        }
    }

    private fun setUpObserver() {
        viewModel.signInResult.observe(
            viewLifecycleOwner,
            OneTimeObserver { isLogInSuccess ->
                if (isLogInSuccess != null) {
                    navigator?.navigateToHomeScreen()
                }
            })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                activity?.showToast("Login Failed")
            }
        })
    }

    companion object {
        fun newInstance() = LoginFragment()
    }


}