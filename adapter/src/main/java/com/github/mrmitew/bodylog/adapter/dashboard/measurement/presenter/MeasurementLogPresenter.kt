package com.github.mrmitew.bodylog.adapter.dashboard.measurement.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.intent.LoadMeasurementLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.interactor.LoadMeasurementLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.view.MeasurementLogView
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject

class MeasurementLogPresenter @Inject constructor(
        private val loadMeasurementLogInteractor: LoadMeasurementLogInteractor,
        private val measurementLogResultStateRelay: BehaviorRelay<ResultState>,
        override val initialState: MeasurementLogView.State = MeasurementLogView.State.Factory.default(),
        override val emptyView: MeasurementLogView = MeasurementLogView.NoOp())
    : DetachableMviPresenter<MeasurementLogView, MeasurementLogView.State>(emptyView) {

    override fun internalIntents() =
            arrayOf(Observable.just(LoadMeasurementLogIntent())
                    .compose(loadMeasurementLogInteractor)
                    .doOnNext { println("[DASHBOARD] [MEASUREMENT LOG MODEL] (${it.hashCode()}) : $it") }
                    .subscribe(measurementLogResultStateRelay))

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.loadMeasurementLogIntent().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.cast(LoadMeasurementLogIntent::class.java).flatMap { measurementLogResultStateRelay }

    override fun viewState(previousState: MeasurementLogView.State, resultState: ResultState): MeasurementLogView.State {
        when (resultState) {
            is LoadMeasurementLogInteractor.State ->
                return when (resultState) {
                    is LoadMeasurementLogInteractor.State.InProgress -> previousState.copy(inProgress = true)
                    is LoadMeasurementLogInteractor.State.Successful ->
                        previousState.copy(inProgress = false,
                                loadSuccessful = true,
                                // TODO: Add support for pagination
                                measurementLogList = resultState.measurementLogList)
                    is LoadMeasurementLogInteractor.State.Error -> previousState.copy(inProgress = false,
                            loadSuccessful = false,
                            loadError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown result state: $resultState")
    }
}