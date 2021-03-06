package com.github.mrmitew.bodylog.adapter.profile.common.interactor

import com.github.mrmitew.bodylog.adapter.profile.common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.domain.executor.ImmediateJobExecutor
import com.github.mrmitew.bodylog.domain.executor.TestPostExecutionThread
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import io.reactivex.Observable
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
    private lateinit var mockRepository: Repository
    private lateinit var loadProfileInteractor: LoadProfileInteractor

    @Before
    @Throws(Exception::class)
    fun setUp() {
        loadProfileInteractor =
                LoadProfileInteractor(ImmediateJobExecutor(), TestPostExecutionThread(), mockRepository)
    }

    @Test
    fun shouldReturnInProgressState_WhenRepositoryObservableEmits() {
        //
        // Arrange
        //
        `when`(loadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.just(mock(Profile::class.java)))

        //
        // Act
        //
        val stateTestObserver = loadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        //
        // Assert
        //
        stateTestObserver
                .assertValueCount(2)
                .assertValueAt(0, { it is LoadProfileInteractor.State.InProgress })
                .assertNoErrors()
    }

    @Test
    fun shouldReturnSuccessfulState_WhenProfileIsRetrieved() {
        //
        // Arrange
        //
        val profile = Profile(
                name = "Test",
                description = "Test")

        `when`(loadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.just(profile))

        //
        // Act
        //
        val stateTestObserver = loadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        //
        // Assert
        //
        stateTestObserver
                .assertValueCount(2)
                .assertValueAt(0, { it is LoadProfileInteractor.State.InProgress })
                .assertValueAt(1, { it is LoadProfileInteractor.State.Successful })
                .assertNoErrors()
    }

    @Test
    fun shouldReturnMultipleSuccessfulStates_WhenMultipleProfilesAreEmitted() {
        //
        // Arrange
        //
        val profile = Profile(
                name = "Test",
                description = "Test")

        `when`(loadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.just(profile))

        // We'll emit two profiles to simulate profile update by the business logic, triggered
        // by another entity
        `when`(mockRepository.userProfileRefreshing())
                .thenReturn(Observable.range(1, 2)
                        .flatMap { Observable.just(profile) })

        //
        // Act
        //
        val stateTestObserver = loadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        //
        // Assert
        //
        stateTestObserver
                .assertValueCount(3)
                .assertValueAt(0, { it is LoadProfileInteractor.State.InProgress })
                .assertValueAt(1, { it is LoadProfileInteractor.State.Successful })
                .assertValueAt(2, { it is LoadProfileInteractor.State.Successful })
                .assertNoErrors()
    }

    @Test
    fun shouldReturnErrorState_WhenAnExceptionIsThrown() {
        //
        // Arrange
        //
        val expectedError = Throwable("Test")
        `when`(loadProfileInteractor.getUseCaseObservable())
                .thenReturn(Observable.error(expectedError))

        //
        // Act
        //
        val stateTestObserver = loadProfileInteractor.apply(Observable.just(LoadProfileIntent())).test()

        //
        // Assert
        //
        stateTestObserver
                // We expect only two states emitted - initial and profile/successful
                .assertValueCount(2)
                // Second state should be "error"
                .assertValueAt(1, { it is LoadProfileInteractor.State.Error })
                // Is the error we expect though?
                .assertValueAt(1, { state -> if (state is LoadProfileInteractor.State.Error) state.error == expectedError else false })
                // No stream errors should be emitted
                .assertNoErrors()
    }
}