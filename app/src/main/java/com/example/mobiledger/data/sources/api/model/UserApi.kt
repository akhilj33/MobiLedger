package com.example.mobiledger.data.sources.api.model

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface UserApi {
    suspend fun addUserToFirebaseDb(user: UserEntity): Boolean
    suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserInfoEntity?>
    suspend fun updateUserNameInFirebase(userName: String, uid: String): Boolean
    suspend fun updateEmailInFirebase(email: String, uid: String): Boolean
    suspend fun updateContactInFirebaseDB(contact: String, uid: String): Boolean
    suspend fun updatePasswordInFirebase(password: String): Boolean
    suspend fun getMonthlyTransactionDetail(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): Boolean
}

class UserApiImpl(private val firebaseDb: FirebaseFirestore) : UserApi {

    override suspend fun addUserToFirebaseDb(user: UserEntity): Boolean {
        return try {
            firebaseDb.collection(USERS).document(user.uid!!).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserInfoEntity?> {
        var response: DocumentSnapshot? = null
        var exception: Exception? = null
        try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            response = docRef.get().await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(userResultEntityMapper(result.data))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateUserNameInFirebase(userName: String, uid: String): Boolean {
        return try {
            //todo
//            val user = Firebase.auth.currentUser
//            val profileUpdates = userProfileChangeRequest {
//                displayName = userName
//                photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
//            }
//            user?.updateProfile(profileUpdates)?.await()
            updateUserNameInDB(userName, uid)
        } catch (e: Exception) {
            false
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

    override suspend fun updateEmailInFirebase(email: String, uid: String): Boolean {
        return try {
            val user = Firebase.auth.currentUser
            user?.updateEmail(email)?.await()
            updateEmailInDB(email, uid)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateContactInFirebaseDB(contact: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef
                .update(PHONE_NUMBER, contact).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updatePasswordInFirebase(password: String): Boolean {
        return try {
            val user = Firebase.auth.currentUser
            user?.updatePassword(password)?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getMonthlyTransactionDetail(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        var response: DocumentSnapshot? = null
        var exception: Exception? = null
        try {
            val docRef = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)

            response = docRef.get().await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(monthlySummaryEntityMapper(result.data))
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
            docRef
                .update(EMAIL_ID, email).await()
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }

    override suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): Boolean {
        lateinit var newMonthlySummary: MonthlyTransactionSummaryEntity

        val timeInMills = transaction.transactionTime?.seconds.toString()

        if (transactionId == NULL_STRING) {
            val docMonthRef = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)

            newMonthlySummary = MonthlyTransactionSummaryEntity(0, 0, 0, 0, 0, 0)
            docMonthRef.set(newMonthlySummary).await()
        }

        val docRef = firebaseDb.collection(USERS)
            .document(uid)
            .collection(MONTH)
            .document(monthYear)
            .collection(TRANSACTION)
            .document(timeInMills)

        return try {
            docRef.set(transaction).await()
            if (transactionId != NULL_STRING)
                updateMonthlySummary(uid, monthYear, monthlyTransactionSummaryEntity!!, transaction)
            else
                updateMonthlySummary(uid, monthYear, newMonthlySummary, transaction)

        } catch (e: Exception) {
            false
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
    user.apply {
        return user?.toObject(UserInfoEntity::class.java)
    }
}

private fun monthlySummaryEntityMapper(user: DocumentSnapshot?): MonthlyTransactionSummaryEntity? {
    user.apply {
        return user?.toObject(MonthlyTransactionSummaryEntity::class.java)
    }
}
