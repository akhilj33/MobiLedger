package com.example.mobiledger.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentProfileBinding
import com.example.mobiledger.domain.entities.UserInfoEntity
import com.example.mobiledger.presentation.NormalObserver

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileNavigator>(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        setObserver()
        viewModel.fetchUserData()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setObserver() {
        viewModel.userFromFirestoreResult.observe(viewLifecycleOwner, {
                updateProfileUI(it)
            }
        )
    }

    private fun setOnClickListener() {
        viewBinding.imgEdit.setOnClickListener {
            navigator?.navigateToEditProfileScreen()
        }
    }

    private fun updateProfileUI(user: UserInfoEntity) {
        viewBinding.displayName.text = user.userName?:""
        viewBinding.emailTv.text = user.emailId?:""
        viewBinding.contactNumTv.text = user.phoneNo?:""
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}