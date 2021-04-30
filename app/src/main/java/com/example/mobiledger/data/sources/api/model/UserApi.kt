package com.example.mobiledger.data.sources.api.model

import android.security.identity.UnknownAuthenticationKeyException
import com.example.mobiledger.common.utils.ConstantUtils.EMAIL_ID
import com.example.mobiledger.common.utils.ConstantUtils.INCOME
import com.example.mobiledger.common.utils.ConstantUtils.MONTH
import com.example.mobiledger.common.utils.ConstantUtils.NO_OF_EXPENSE_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.NO_OF_INCOME_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.NO_OF_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.NULL_STRING
import com.example.mobiledger.common.utils.ConstantUtils.PHONE_NUMBER
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_BALANCE
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_EXPENSE
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_INCOME
import com.example.mobiledger.common.utils.ConstantUtils.TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.UNAUTHORIZED_ERROR_MSG
import com.example.mobiledger.common.utils.ConstantUtils.USERS
import com.example.mobiledger.common.utils.ConstantUtils.USER_NAME
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.entities.UserInfoEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface UserApi {
    suspend fun addUserToFirebaseDb(user: UserEntity): AppResult<Unit>
    suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserInfoEntity?>
    suspend fun updateUserNameInAuth(userName: String, uid: String): AppResult<Unit>
    suspend fun updateEmailInAuth(email: String, uid: String): AppResult<Unit>
    suspend fun updateContactInFirebaseDB(contact: String, uid: String): AppResult<Unit>
    suspend fun updatePasswordInAuth(password: String): AppResult<Unit>
    suspend fun getMonthlyTransactionDetail(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit>
}

class UserApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : UserApi {

    override suspend fun addUserToFirebaseDb(user: UserEntity): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            response = firebaseDb.collection(USERS).document(user.uid!!).set(user)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserInfoEntity?> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            response = firebaseDb.collection(USERS).document(uid).get()
            response.await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val userInfo = userResultEntityMapper(result.data?.result)
                if (userInfo != null) {
                    AppResult.Success(userInfo)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateUserNameInAuth(userName: String, uid: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
            userProfileChangeRequest.displayName = userName
            response = user?.updateProfile(userProfileChangeRequest.build())
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val isUpdated = updateUserNameInDB(userName, uid)
                if (isUpdated) AppResult.Success(Unit)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }


    private suspend fun updateUserNameInDB(userName: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef
                .update(USER_NAME, userName).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateEmailInAuth(email: String, uid: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            response = user?.updateEmail(email)
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val isUpdated = updateEmailInDB(email, uid)
                if (isUpdated) AppResult.Success(Unit)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateContactInFirebaseDB(contact: String, uid: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            val docRef = firebaseDb.collection(USERS).document(uid)
            response = docRef.update(PHONE_NUMBER, contact)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updatePasswordInAuth(password: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            response = user?.updatePassword(password)
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getMonthlyTransactionDetail(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(monthlySummaryEntityMapper(result.data.result))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    private suspend fun updateEmailInDB(email: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef.update(EMAIL_ID, email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit> {
        lateinit var newMonthlySummary: MonthlyTransactionSummaryEntity
        var response: Task<Void>? = null
        var exception: Exception? = null

        val timeInMills = transaction.transactionTime?.seconds.toString()

        try {
            if (!authSource.isUserAuthorized()) throw UnknownAuthenticationKeyException(UNAUTHORIZED_ERROR_MSG)
            if (transactionId == NULL_STRING) {
                val docMonthRef = firebaseDb.collection(USERS)
                    .document(uid)
                    .collection(MONTH)
                    .document(monthYear)

                newMonthlySummary = MonthlyTransactionSummaryEntity(0, 0, 0, 0, 0, 0)
                docMonthRef.set(newMonthlySummary).await()
            }

            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(TRANSACTION)
                .document(timeInMills)
                .set(transaction)

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (transactionId != NULL_STRING)
                    updateMonthlySummary(uid, monthYear, monthlyTransactionSummaryEntity!!, transaction)
                else
                    updateMonthlySummary(uid, monthYear, newMonthlySummary, transaction)

                AppResult.Success(Unit)

            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    private suspend fun updateMonthlySummary(
        uid: String, monthYear: String,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity,
        transaction: TransactionEntity
    ): Boolean {
        val noOfTransaction = monthlyTransactionSummaryEntity.noOfTransaction?.plus(1)
        var noOfIncome = monthlyTransactionSummaryEntity.noOfIncomeTransaction
        var noOfExpense = monthlyTransactionSummaryEntity.noOfExpenseTransaction
        var totalIncome = monthlyTransactionSummaryEntity.totalIncome
        var totalExpense = monthlyTransactionSummaryEntity.totalExpense
        if (transaction.transactionType == INCOME) {
            noOfIncome = noOfIncome?.plus(1)
            totalIncome = totalIncome?.plus(transaction.amount!!)
        } else {
            noOfExpense = noOfExpense?.plus(1)
            totalExpense = totalExpense?.plus(transaction.amount!!)
        }
        return try {
            val docRef = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)

            docRef.update(NO_OF_TRANSACTION, noOfTransaction).await()
            docRef.update(NO_OF_INCOME_TRANSACTION, noOfIncome).await()
            docRef.update(NO_OF_EXPENSE_TRANSACTION, noOfExpense).await()
            docRef.update(TOTAL_INCOME, totalIncome).await()
            docRef.update(TOTAL_EXPENSE, totalExpense).await()
            docRef.update(TOTAL_BALANCE, totalIncome!! - totalExpense!!).await()
            true
        } catch (e: Exception) {
            false
        }

    }
}

private fun userResultEntityMapper(user: DocumentSnapshot?): UserInfoEntity? {
    return user?.toObject(UserInfoEntity::class.java)
}

private fun monthlySummaryEntityMapper(user: DocumentSnapshot?): MonthlyTransactionSummaryEntity? {
    return user?.toObject(MonthlyTransactionSummaryEntity::class.java)
}
