package com.example.mobiledger.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {

    val uploadPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    fun permissionsNeeded(context: Context, permissions: List<String>): Array<String> {
        val permissionsNeeded = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.forEach {
                if (ContextCompat.checkSelfPermission(context, it)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsNeeded.add(it)
                }
            }
        }
        return permissionsNeeded.toTypedArray()
    }

}