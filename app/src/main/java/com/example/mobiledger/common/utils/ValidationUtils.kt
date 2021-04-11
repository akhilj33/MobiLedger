package com.example.mobiledger.common.utils

import java.util.regex.Pattern

object ValidationUtils {

    fun phoneNoValidator(phoneNo: String): Boolean {
        val phoneNoPattern: Pattern = Pattern.compile(
            "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$"
        )
        return phoneNoPattern.matcher(phoneNo).matches()
    }

    fun passwordValidator(password: String?): Boolean {
        val emailPattern: Pattern = Pattern.compile(
            "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[@#$%^&+=])"
                    + "(?=\\S+$).{8,20}$"
        )
        return emailPattern.matcher(password).matches()
    }
}