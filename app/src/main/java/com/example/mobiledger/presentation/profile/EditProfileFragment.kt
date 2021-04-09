package com.example.mobiledger.presentation.profile

import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.DummyNavigator
import com.example.mobiledger.databinding.FragmentEditProfileBinding


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, DummyNavigator>(R.layout.fragment_edit_profile) {

    override fun isBottomNavVisible(): Boolean = false

    companion object {
        fun newInstance() = EditProfileFragment()
    }
}