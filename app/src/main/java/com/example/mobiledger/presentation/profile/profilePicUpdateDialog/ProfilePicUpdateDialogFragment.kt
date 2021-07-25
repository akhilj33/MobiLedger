package com.example.mobiledger.presentation.profile.profilePicUpdateDialog

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.utils.ConstantUtils.PROFILE_PIC_BUNDLE_KEY
import com.example.mobiledger.common.utils.ConstantUtils.PROFILE_PIC_REQUEST_KEY
import com.example.mobiledger.databinding.FragmentProfilePicUpdateDialogBinding
import com.example.mobiledger.domain.enums.ProfilePicUpdateType

class ProfilePicUpdateDialogFragment :
    BaseDialogFragment<FragmentProfilePicUpdateDialogBinding, BaseNavigator>(R.layout.fragment_profile_pic_update_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        viewBinding.apply {
            ivRemoveProfilePic.setOnSafeClickListener {
                onRemovePhotoIconClick.invoke()
            }

            tvDeletePic.setOnSafeClickListener {
                onRemovePhotoIconClick.invoke()
            }

            ivChangeProfilePic.setOnSafeClickListener {
                onChangePhotoIconClick.invoke()
            }

            tvChangePic.setOnSafeClickListener {
                onChangePhotoIconClick.invoke()
            }
        }
    }

    private val onRemovePhotoIconClick = fun() {
        setFragmentResult(PROFILE_PIC_REQUEST_KEY, bundleOf(Pair(PROFILE_PIC_BUNDLE_KEY, ProfilePicUpdateType.DELETE.type)))
        dismiss()
    }

    private val onChangePhotoIconClick = fun() {
        setFragmentResult(PROFILE_PIC_REQUEST_KEY, bundleOf(Pair(PROFILE_PIC_BUNDLE_KEY, ProfilePicUpdateType.CHANGE.type)))
        dismiss()
    }

    companion object {
        fun newInstance() = ProfilePicUpdateDialogFragment()
    }
}