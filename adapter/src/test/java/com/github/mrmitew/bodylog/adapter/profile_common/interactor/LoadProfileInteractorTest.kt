package com.github.mrmitew.bodylog.adapter.profile_common.interactor

import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.domain.executor.ImmediateJobExecutor
import com.github.mrmitew.bodylog.domain.executor.TestPostExecutionThread
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoadProfileInteractorTest {
    @Mock
    private lateinit var mMockRepository: Repository
    private lateinit var mLoadProfileInteractor: LoadProfileInteractor

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mLoadProfileInteractor =
                LoadProfileInteractor(ImmediateJobExecutor(), TestPostExecutionThread(), mMockRepository)
    }

    @Test
    fun shouldReturnInProgressState_WhenRepositoryObservableEmits() {
        //
        // Arrange
        //
        `when`(mLoadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.just(mock(Profile::class.java)))

        //
        // Act
        //
        val stateTestObserver = mLoadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        //
        // Assert
        //
        assertEquals(true, stateTestObserver.values()[0] is LoadProfileInteractor.State.InProgress)

        // No errors should be emitted
        stateTestObserver.assertNoErrors()
    }

    @Test
    fun shouldReturnSuccessfulState_WhenProfileIsRetrieved() {
        //
        // Arrange
        val profile = Profile(
                name = "Test",
                description = "Test",
                weight = 0f,
                bodyFatPercentage = 0f,
                backSize = 0f,
                chestSize = 0f,
                armsSize = 0f,
                waistSize = 0f,
                timestamp = System.currentTimeMillis(),
                empty = false)

        `when`(mLoadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.just(profile))

        //
        // Act
        //
        val stateTestObserver = mLoadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        val values = stateTestObserver.values()

        //
        // Assert
        //
        assertEquals(2, values.size.toLong())
        assertEquals(true, values[0] is LoadProfileInteractor.State.InProgress)
        assertEquals(true, values[1] is LoadProfileInteractor.State.Successful)

        // No errors should be emitted
        stateTestObserver.assertNoErrors()
    }

    @Test
    fun shouldReturnMultipleSuccessfulStates_WhenMultipleProfilesAreEmitted() {
        //
        // Arrange
        //
        val profile = Profile(
                name = "Test",
                description = "Test",
                weight = 0f,
                bodyFatPercentage = 0f,
                backSize = 0f,
                chestSize = 0f,
                armsSize = 0f,
                waistSize = 0f,
                timestamp = System.currentTimeMillis(),
                empty = false)

        `when`(mLoadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.just(profile))

        // We'll emit two profiles to simulate profile update by the business logic, triggered
        // by another entity
        `when`(mMockRepository.getProfileRefreshing())
                .thenReturn(Observable.range(1, 2)
                        .flatMap { Observable.just(profile) })

        //
        // Act
        //
        val stateTestObserver = mLoadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        val values = stateTestObserver.values()

        //
        // Assert
        //
        assertEquals(3, values.size.toLong())
        assertEquals(true, values[0] is LoadProfileInteractor.State.InProgress)
        assertEquals(true, values[1] is LoadProfileInteractor.State.Successful)
        assertEquals(true, values[2] is LoadProfileInteractor.State.Successful)

        // No errors should be emitted
        stateTestObserver.assertNoErrors()
    }

    @Test
    fun shouldReturnErrorState_WhenAnExceptionIsThrown() {
        //
        // Arrange
        //
        val expectedError = Throwable("Test")
        `when`(mLoadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.error(expectedError))

        //
        // Act
        //
        val stateTestObserver = mLoadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        val values = stateTestObserver.values()

        //
        // Assert
        //
        assertEquals(2, values.size.toLong())

        // Second state should be "error"
        assertEquals(true, values[1] is LoadProfileInteractor.State.Error)

        // No errors should be emitted
        stateTestObserver.assertNoErrors()
    }
}