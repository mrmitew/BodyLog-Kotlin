package com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.intent.LoadWeightLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.weight.interactor.LoadWeightLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.weight.view.WeightLogView
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject

class WeightLogPresenter @Inject constructor(
        private val loadWeightLogInteractor: LoadWeightLogInteractor,
        private val weightLogStateRelay: BehaviorRelay<ResultState>,
        override val initialState: WeightLogView.State = WeightLogView.State.Factory.default(),
        override val emptyView: WeightLogView = WeightLogView.NoOp())
    : DetachableMviPresenter<WeightLogView, WeightLogView.State>(emptyView) {

    override fun internalIntents() =
            arrayOf(Observable.just(LoadWeightLogIntent())
                    .compose(loadWeightLogInteractor)
                    .doOnNext { println("[DASHBOARD] [WEIGHT LOG MODEL] (${it.hashCode()}) : $it") }
                    .subscribe(weightLogStateRelay))

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.loadWeightLogIntent().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared ->
                shared.ofType(LoadWeightLogIntent::class.java).flatMap { weightLogStateRelay }
            }

    override fun viewState(previousState: WeightLogView.State, resultState: ResultState): WeightLogView.State {
        when (resultState) {
            is LoadWeightLogInteractor.State ->
                return when (resultState) {
                    is LoadWeightLogInteractor.State.InProgress -> previousState.copy(inProgress = true)
                    is LoadWeightLogInteractor.State.Successful ->
                        previousState.copy(inProgress = false,
                                loadSuccessful = true,
                                weightLogList = resultState.weightLogList)
//                                weightLogList = previousState.weightLogList.toMutableList()
//                                        .apply { addAll(resultState.weightLogList) })
                    is LoadWeightLogInteractor.State.Error -> previousState.copy(inProgress = false,
                            loadSuccessful = false,
                            loadError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown result state: $resultState")
    }
}