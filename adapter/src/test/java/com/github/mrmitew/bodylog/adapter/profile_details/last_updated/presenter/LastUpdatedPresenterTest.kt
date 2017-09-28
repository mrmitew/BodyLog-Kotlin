package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.StateError
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.intent.GetProfileLastUpdatedIntent
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model.LastUpdatedTextState
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.view.LastUpdatedView
import com.github.mrmitew.bodylog.domain.executor.ImmediateJobExecutor
import com.github.mrmitew.bodylog.domain.executor.TestPostExecutionThread
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
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

        presenter = LastUpdatedPresenter(loadProfileInteractor = loadProfileInteractor,
                profileResultStateRelay = profileResultStateRelay,
                initialState = LastUpdatedTextState(lastUpdated = LastUpdatedTextState.Factory.DEFAULT_VALUE,
                        error = StateError.Empty.INSTANCE))
    }

    @Test
    fun shouldReturnProfileResultState_ForGetProfileLastUpdatedIntent() {
        //
        // Arrange
        //
        val expectedProfile = mock(Profile::class.java)
        val viewIntentStream = Observable.just(GetProfileLastUpdatedIntent()).cast(ViewIntent::class.java)

        //
        // Act
        //
        val resultStateStream = presenter.resultStateStream(viewIntentStream)
        // Emit a result state for a profile request, just like as if it was from the repository
        profileResultStateRelay.accept(LoadProfileInteractor.State.Successful(expectedProfile))
        val resultStreamObserver = resultStateStream.test()

        //
        // Assert
        //
        assertTrue(resultStreamObserver.valueCount() == 1)
        val resultState = resultStreamObserver.values()[0]
        when (resultState) {
            is LoadProfileInteractor.State.Successful -> assertTrue(resultState.profile == expectedProfile)
            else -> throw RuntimeException("Expected a successful state")
        }
    }

    // Integration between LoadProfileInteractor and presenter
    @Test
    fun shouldGetProfileResultState_WhenProfileIntentFires() {
        //
        // Arrange
        //
        val expectedProfile = mock(Profile::class.java)
        val viewIntentStream = Observable.just(GetProfileLastUpdatedIntent()).cast(ViewIntent::class.java)

        // Fire a view intent to load a profile
        `when`(view.profileLastUpdatedIntent()).thenReturn(Observable.just(GetProfileLastUpdatedIntent()))

        // Return a profile when the repository will be queried
        `when`(loadProfileInteractor.getUseCaseObservable()).thenReturn(Observable.just(expectedProfile))

        //
        // Act
        //
        attachView()

        val resultStateStream = presenter.resultStateStream(viewIntentStream)
        val resultStreamObserver = resultStateStream.test()

        //
        // Assert
        //
        assertTrue(resultStreamObserver.valueCount() == 1)
        val resultState = resultStreamObserver.values()[0]
        when (resultState) {
            is LoadProfileInteractor.State.Successful -> assertTrue(resultState.profile == expectedProfile)
            else -> throw RuntimeException("Expected a successful state")
        }

        //
        // Act: Clean up
        //
        detachView()
    }

    @Test
    fun shouldClearError_WhenProfileResultIsSuccessful() {
        //
        // Arrange
        //
        val currentState = LastUpdatedTextState.Factory.error(mock(Throwable::class.java))

        //
        // Act
        //
        val newUiState = presenter.viewState(currentState, resultState = LoadProfileInteractor.State.Successful(mock(Profile::class.java)))

        //
        // Assert
        //
        assertTrue(newUiState.error == StateError.Empty.INSTANCE)
    }

    @Test
    fun shouldDefaultAndSetErrorState_WhenProfileResultIsError() {
        //
        // Arrange
        //
        val currentState = LastUpdatedTextState.Factory.success(0L)

        //
        // Act
        //
        val newUiState = presenter.viewState(currentState, resultState = LoadProfileInteractor.State.Error(error))

        //
        // Assert
        //
        assertTrue(newUiState.error == error)
        assertTrue(newUiState.lastUpdated == LastUpdatedTextState.Factory.DEFAULT_VALUE)
    }

    @Test
    fun shouldReturnPreviousState_WhenProfileResultIsInProgress_AndPreviosStateHasNoError() {
        //
        // Arrange
        //
        val currentState = LastUpdatedTextState.Factory.success(0L)

        //
        // Act
        //
        val newUiState = presenter.viewState(currentState, resultState = LoadProfileInteractor.State.InProgress())

        //
        // Assert
        //
        assertTrue(currentState == newUiState)
        assertTrue(currentState.hashCode() == newUiState.hashCode())
    }

    @Test
    fun shouldReturnPreviousState_WhenProfileResultIsInProgress_AndPreviosStateHasError() {
        //
        // Arrange
        //
        val currentState = LastUpdatedTextState.Factory.error(error)

        //
        // Act
        //
        val newUiState = presenter.viewState(currentState, resultState = LoadProfileInteractor.State.InProgress())

        //
        // Assert
        //
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
