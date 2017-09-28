package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.StateError
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model.LastUpdatedTextState
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.view.LastUpdatedView
import com.github.mrmitew.bodylog.domain.executor.ImmediateJobExecutor
import com.github.mrmitew.bodylog.domain.executor.TestPostExecutionThread
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.jakewharton.rxrelay2.BehaviorRelay
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LastUpdatedPresenterTest {
    @Mock private lateinit var mockRepository: Repository
    @Mock private lateinit var view: LastUpdatedView
    @Mock private lateinit var error: Throwable
    private lateinit var presenter: LastUpdatedPresenter
    private lateinit var loadProfileInteractor: LoadProfileInteractor
    private lateinit var profileResultStateRelay: BehaviorRelay<ResultState>

    @Before
    fun setUp() {
        loadProfileInteractor =
                LoadProfileInteractor(threadExecutor = ImmediateJobExecutor(),
                        postExecutionThread = TestPostExecutionThread(),
                        repository = mockRepository)

        profileResultStateRelay = BehaviorRelay.create()

        presenter = spy(LastUpdatedPresenter(loadProfileInteractor = loadProfileInteractor,
                profileResultStateRelay = profileResultStateRelay,
                initialState = LastUpdatedTextState(lastUpdated = LastUpdatedTextState.Factory.DEFAULT_VALUE,
                        error = StateError.Empty.INSTANCE)))

//        `when`(view.getProfileLastUpdatedIntent()).thenReturn(Observable.just(GetProfileLastUpdatedIntent()))
    }

    @Test
    fun shouldClearError_WhenProfileResultIsSuccessful() {
        // Arrange
        val currentState = LastUpdatedTextState.Factory.error(mock(Throwable::class.java))

        // Act
        val newUiState = presenter.createViewState(currentState, resultState = LoadProfileInteractor.State.Successful(mock(Profile::class.java)))

        // Assert
        assertTrue(newUiState.error == StateError.Empty.INSTANCE)
    }

    @Test
    fun shouldDefaultAndSetErrorState_WhenProfileResultIsError() {
        // Arrange
        val currentState = LastUpdatedTextState.Factory.success(0L)

        // Act
        val newUiState = presenter.createViewState(currentState, resultState = LoadProfileInteractor.State.Error(error))

        // Assert
        assertTrue(newUiState.error == error)
        assertTrue(newUiState.lastUpdated == LastUpdatedTextState.Factory.DEFAULT_VALUE)
    }

    @Test
    fun shouldReturnPreviousState_WhenProfileResultIsInProgress_AndPreviosStateHasNoError() {
        // Arrange
        val currentState = LastUpdatedTextState.Factory.success(0L)

        // Act
        val newUiState = presenter.createViewState(currentState, resultState = LoadProfileInteractor.State.InProgress())

        // Assert
        assertTrue(currentState == newUiState)
        assertTrue(currentState.hashCode() == newUiState.hashCode())
    }

    @Test
    fun shouldReturnPreviousState_WhenProfileResultIsInProgress_AndPreviosStateHasError() {
        // Arrange
        val currentState = LastUpdatedTextState.Factory.error(error)

        // Act
        val newUiState = presenter.createViewState(currentState, resultState = LoadProfileInteractor.State.InProgress())

        // Assert
        assertTrue(currentState == newUiState)
        assertTrue(currentState.hashCode() == newUiState.hashCode())
    }

    private fun detachView() {
        presenter.detachView()
        presenter.unbindIntents()
    }

    private fun attachView() {
        presenter.attachView(view)
        presenter.bindIntents()
    }
}
