package com.example.mobiledger.presentation.home

import com.example.mobiledger.common.BaseTestClass
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.enums.SignInType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HomeViewModelTest : BaseTestClass() {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var testHomeViewModel: HomeViewModel

    @Mock
    private lateinit var mockedProfileUseCase: ProfileUseCase

    @Mock
    private lateinit var mockedTransactionUseCase: TransactionUseCase

    @Mock
    private lateinit var mockedBudgetUseCase: BudgetUseCase

    @Mock
    private lateinit var mockedCategoryUseCase: CategoryUseCase


    @Before
    override fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        testHomeViewModel = HomeViewModel(mockedProfileUseCase, mockedTransactionUseCase, mockedBudgetUseCase, mockedCategoryUseCase)
    }

    @Test
    fun getUserName_returnUserProfile_Success() {
        val testUserEntityResponse = UserEntity("123", "tonu", "", "xyz@gmail.com", "46378246", SignInType.Email)
        var actualResult = ""

        val expectedResult = testHomeViewModel.extractFirstName(testUserEntityResponse.userName ?: "")

        val lock = CountDownLatch(1)
        testHomeViewModel.userNameLiveData.observeForever {
            actualResult = it
            lock.countDown()
        }

        runBlocking {
            Mockito.`when`(mockedProfileUseCase.fetchUserFromFirebase()).thenReturn(AppResult.Success(testUserEntityResponse))
            testHomeViewModel.getUserName(false)
        }

        lock.await(timeOut, TimeUnit.MILLISECONDS)

        assertEquals(expectedResult, actualResult)
    }

    @After
    override fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

}