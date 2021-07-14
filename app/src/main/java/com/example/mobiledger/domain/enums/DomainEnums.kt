package com.example.mobiledger.domain.enums

enum class TransactionType(val type: String) {
    Expense("expense"), Income("income");

    companion object {
        fun getTransactionType(type: String): TransactionType {
            return when (type) {
                Expense.type -> Expense
                Income.type -> Income
                else -> Expense
            }
        }
    }
}

enum class SignInType(val type: String) {
    Google("google"), Email("email")
}

enum class EditCategoryTransactionType(val increment: Long) {
    ADD(1), DELETE(-1), NOTHING(0)
}

enum class ProfilePicUpdateType(val type: String) {
    DELETE("delete"), CHANGE("change");

    companion object {
        fun getProfilePicUpdateType(type: String): ProfilePicUpdateType {
            return when (type) {
                DELETE.type -> DELETE
                else -> CHANGE
            }
        }
    }
}
