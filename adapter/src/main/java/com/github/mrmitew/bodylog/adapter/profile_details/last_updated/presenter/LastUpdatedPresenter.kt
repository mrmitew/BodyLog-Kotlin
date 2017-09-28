package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.StateError
import com.github.mrmitew.bodylog.adapter.common.model.UIIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.intent.GetProfileLastUpdatedIntent
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model.LastUpdatedTextState
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.view.LastUpdatedView
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject

class LastUpdatedPresenter
@Inject constructor(private val loadProfileInteractor: LoadProfileInteractor,
                    private val profileResultStateRelay: BehaviorRelay<ResultState>) :
        DetachableMviPresenter<LastUpdatedView, LastUpdatedTextState>(emptyView) {

    companion object {
        val emptyView = LastUpdatedView.NoOp()
    }

    override fun getEmptyView(): LastUpdatedView = emptyView

    // TODO: provide as constructor dependency
    override fun initialState(): LastUpdatedTextState = LastUpdatedTextState.Factory.idle()

    override fun viewIntents(): Observable<UIIntent> =
            view.getProfileLastUpdatedIntent().cast(UIIntent::class.java)

    override fun bindInternalIntents() {
        super.bindInternalIntents()
        modelGateways.add(Observable.just(LoadProfileIntent())
                .compose(loadProfileInteractor)
                .doOnNext { println("[LAST_UPDATED] [PROFILE MODEL] (${it.hashCode()}) : $it") }
                .subscribe(profileResultStateRelay))
    }

    override fun createResultStateObservable(uiIntentStream: Observable<UIIntent>): Observable<ResultState> =
            uiIntentStream.publish { shared -> shared.ofType(GetProfileLastUpdatedIntent::class.java).flatMap { profileResultStateRelay } }

    override fun createViewState(previousState: LastUpdatedTextState, resultState: ResultState): LastUpdatedTextState {
        when (resultState) {
            is LoadProfileInteractor.State ->
                return when (resultState) {
                    is LoadProfileInteractor.State.InProgress -> // We are not going to indicate we are currently fetching new data,
                        // nor will replace the old one with something, but we'll just clear the error field (if there was one)
                        if (previousState.error !== StateError.Empty.INSTANCE)
                            previousState.copy(error = StateError.Empty.INSTANCE)
                        else
                            previousState

                    // No error? Then, just emit the old state. No view state changes needed to be done here.
                    is LoadProfileInteractor.State.Successful -> return previousState.copy(
                            lastUpdated = LastUpdatedTextState.Factory.DATE_FORMAT.format(resultState.profile.timestamp),
                            error = StateError.Empty.INSTANCE)

                    is LoadProfileInteractor.State.Error -> return previousState.copy(
                            lastUpdated = LastUpdatedTextState.Factory.DEFAULT_VALUE,
                            error = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown partial state: " + resultState)
    }
}