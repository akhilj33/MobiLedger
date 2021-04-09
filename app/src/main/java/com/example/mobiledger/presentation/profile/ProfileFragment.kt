package com.example.mobiledger.presentation.profile

import android.os.Bundle
import android.view.View
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileNavigator>(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setOnClickListener() {
        viewBinding.imgEdit.setOnClickListener {
            navigator?.navigateToEditProfileScreen()
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}