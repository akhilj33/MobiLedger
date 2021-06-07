package com.example.mobiledger.common.utils

import androidx.biometric.BiometricManager


enum class BiometricDeviceState {
    BIOMETRIC_AVAILABLE,
    BIOMETRIC_ERROR_NO_HARDWARE,
    BIOMETRIC_ERROR_HW_UNAVAILABLE,
    BIOMETRIC_ERROR_NONE_ENROLLED
}

fun canAuthenticateUsingBiometrics(biometricManager: BiometricManager): BiometricDeviceState {
    return when (biometricManager.canAuthenticate()) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            BiometricDeviceState.BIOMETRIC_AVAILABLE
        }
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            BiometricDeviceState.BIOMETRIC_ERROR_NO_HARDWARE
        }
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            BiometricDeviceState.BIOMETRIC_ERROR_HW_UNAVAILABLE
        }
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            BiometricDeviceState.BIOMETRIC_ERROR_NONE_ENROLLED
        }
        else ->
            BiometricDeviceState.BIOMETRIC_ERROR_HW_UNAVAILABLE
    }
}