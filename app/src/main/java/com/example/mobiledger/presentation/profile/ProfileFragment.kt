package com.example.mobiledger.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentProfileBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.presentation.OneTimeObserver


class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileNavigator>(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        setObserver()
        viewModel.fetchUserData()
    }

    override fun isBottomNavVisible(): Boolean = false
    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    private fun setObserver() {
        viewModel.userFromFirestoreResult.observe(viewLifecycleOwner, {
            updateProfileUI(it)
        })

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.profileProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.profileProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                ProfileViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), false)
                }
            }
        })
    }

    private fun setOnClickListener() {
        viewBinding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewBinding.imgEdit.setOnClickListener {
            navigator?.navigateToEditProfileScreen()
        }

        viewBinding.textCategory.setOnClickListener {
            navigator?.navigateToCategoryFragmentScreen()
        }
    }

    private fun updateProfileUI(user: UserEntity) {
        viewBinding.displayName.text = user.userName ?: ""
        viewBinding.emailTv.text = user.emailId ?: ""
        viewBinding.contactNumTv.text = user.phoneNo ?: ""
    }


    companion object {
        fun newInstance() = ProfileFragment()
    }
}