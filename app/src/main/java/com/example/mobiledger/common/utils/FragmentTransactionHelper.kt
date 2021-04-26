package com.example.mobiledger.common.utils

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.extention.getName

object FragmentTransactionHelper {
    fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        frameId: Int,
        addToBackStack: Boolean = true
    ) {
        val transaction = manager.beginTransaction()
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getName())
        }
        transaction.replace(frameId, fragment, fragment.getName())
        transaction.commit()
    }

    fun popFragmentFromBackStack(
        fragmentManager: FragmentManager,
        tag: String?,
        isExclusive: Boolean = true
    ) {
        val flag = if (isExclusive) 0 else FragmentManager.POP_BACK_STACK_INCLUSIVE
        fragmentManager.popBackStack(tag, flag)
    }

    fun isFragmentOnTopOfStack(
        fragmentManager: FragmentManager,
        tag: String,
        @IdRes containerId: Int
    ): Boolean {
        val fragment = fragmentManager.findFragmentById(containerId)
        return (fragment != null && fragment.tag == tag)
    }

    fun popAllFragments(fragmentManager: FragmentManager) {
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    fun dismissAllDialogs(manager: FragmentManager) {
        val fragments = manager.fragments
        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}