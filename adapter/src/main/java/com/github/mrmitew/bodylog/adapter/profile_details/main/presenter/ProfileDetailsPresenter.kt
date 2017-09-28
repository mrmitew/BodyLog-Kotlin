package com.github.mrmitew.bodylog.adapter.profile_details.main.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.StateError
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile_details.main.model.ProfileDetailsState
import com.github.mrmitew.bodylog.adapter.profile_details.main.view.ProfileDetailsView
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ProfileDetailsPresenter @Inject constructor(
        // Loads a profile from the repository
        private val loadProfileInteractor: LoadProfileInteractor,
        /*
         * State relays are subscribed to the business logic (model) and will cache (and perhaps* emit) the latest changes in the
         * business logic.
         *
         * * If the View is not attached, the relays will keep a cached state of a particular result, which
         * will be emitted as soon as the View attaches once again.
         */
        private val profileResultStateRelay: BehaviorRelay<ResultState>,
        override val initialState: ProfileDetailsState,
        override val emptyView: ProfileDetailsView = ProfileDetailsView.NoOp())
    : DetachableMviPresenter<ProfileDetailsView, ProfileDetailsState>(emptyView) {

    override fun internalIntents(): Array<Disposable> =
            arrayOf(Observable.just(LoadProfileIntent())
                    .compose(loadProfileInteractor)
                    .doOnNext { println("[DETAILS] [PROFILE MODEL] (${it.hashCode()}) : $it") }
                    .subscribe(profileResultStateRelay))

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.loadProfileIntent().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared -> shared.ofType(LoadProfileIntent::class.java).flatMap { profileResultStateRelay } }

    override fun viewState(previousState: ProfileDetailsState, resultState: ResultState): ProfileDetailsState {
        when (resultState) {
            is LoadProfileInteractor.State ->
                return when (resultState) {
                    is LoadProfileInteractor.State.InProgress ->
                        previousState.copy(
                                inProgress = true,
                                loadSuccessful = false,
                                loadError = StateError.Empty.INSTANCE)

                    is LoadProfileInteractor.State.Successful ->
                        previousState.copy(
                                profile = resultState.profile,
                                inProgress = false,
                                loadSuccessful = true)

                    is LoadProfileInteractor.State.Error ->
                        previousState.copy(
                                inProgress = false,
                                loadSuccessful = false,
                                loadError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown partial state: " + resultState)
    }
}