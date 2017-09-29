package com.github.mrmitew.bodylog.adapter.profile.details.last_updated.presenter

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.profile.common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile.common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.intent.GetProfileLastUpdatedIntent
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.model.LastUpdatedTextState
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.view.LastUpdatedView
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject

class LastUpdatedPresenter
@Inject constructor(private val loadProfileInteractor: LoadProfileInteractor,
                    private val profileResultStateRelay: BehaviorRelay<ResultState>,
                    override val initialState: LastUpdatedTextState,
                    override val emptyView: LastUpdatedView = LastUpdatedView.NoOp()) :
        DetachableMviPresenter<LastUpdatedView, LastUpdatedTextState>(emptyView) {

    override fun internalIntents() =
            arrayOf(Observable.just(LoadProfileIntent())
                    .compose(loadProfileInteractor)
                    .doOnNext { println("[LAST_UPDATED] [PROFILE MODEL] (${it.hashCode()}) : $it") }
                    .subscribe(profileResultStateRelay))

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.profileLastUpdatedIntent().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared ->
                shared.ofType(GetProfileLastUpdatedIntent::class.java).flatMap { profileResultStateRelay }
            }

    override fun viewState(previousState: LastUpdatedTextState, resultState: ResultState): LastUpdatedTextState {
        when (resultState) {
            is LoadProfileInteractor.State ->
                return when (resultState) {
                    is LoadProfileInteractor.State.InProgress -> previousState

                // No error? Then, just emit the old state. No view state changes needed to be done here.
                    is LoadProfileInteractor.State.Successful -> return previousState.copy(
                            lastUpdated = LastUpdatedTextState.Factory.DATE_FORMAT.format(resultState.profile.timestamp),
                            error = Error.Empty.INSTANCE)

                    is LoadProfileInteractor.State.Error -> return previousState.copy(
                            lastUpdated = LastUpdatedTextState.Factory.DEFAULT_VALUE,
                            error = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown partial state: $resultState")
    }
}