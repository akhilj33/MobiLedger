package com.example.mobiledger.presentation.auth

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showToast
import com.example.mobiledger.databinding.FragmentLoginBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginNavigator>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setOnClickListener() {

        viewBinding.apply {
            btnLogin.setOnClickListener {
                val email = viewBinding.textEmail.text.toString()
               val  password = viewBinding.textPassword.text.toString()
                if (email.isEmpty() || password.isEmpty())
                    activity?.showToast("No field can be empty")
                else
                    viewModel.loginUserViaEmail(email, password)
            }

            tvSignUp.setOnClickListener {
                navigator?.navigateLoginToSignUpScreen()
            }

            btnGoogleSignIn.setOnClickListener{
                initSignInWithGoogle()
            }
        }
    }

    private fun setUpObserver() {
        viewModel.signInResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                navigator?.launchDashboard()
            })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                activity?.showToast("Login Failed")
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

    companion object {
        fun newInstance() = LoginFragment()
    }


}