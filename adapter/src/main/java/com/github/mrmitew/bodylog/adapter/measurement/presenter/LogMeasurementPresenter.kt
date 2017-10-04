package com.github.mrmitew.bodylog.adapter.measurement.presenter

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.measurement.intent.LogMeasurementIntent
import com.github.mrmitew.bodylog.adapter.measurement.interactor.LogMeasurementInteractor
import com.github.mrmitew.bodylog.adapter.measurement.view.LogMeasurementView
import io.reactivex.Observable
import javax.inject.Inject

class LogMeasurementPresenter @Inject constructor(
        private val logMeasurementInteractor: LogMeasurementInteractor,
        override val initialState: LogMeasurementView.State = LogMeasurementView.State.Factory.default(),
        override val emptyView: LogMeasurementView = LogMeasurementView.NoOp())
    : DetachableMviPresenter<LogMeasurementView, LogMeasurementView.State>(emptyView) {

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.logMeasurement().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared ->
                shared.ofType(LogMeasurementIntent::class.java).compose(logMeasurementInteractor).cast(ResultState::class.java)
            }

    override fun viewState(previousState: LogMeasurementView.State, resultState: ResultState): LogMeasurementView.State {
        when (resultState) {
            is LogMeasurementInteractor.State ->
                return when (resultState) {
                    is LogMeasurementInteractor.State.InProgress -> previousState.copy(inProgress = true)
                    is LogMeasurementInteractor.State.Successful -> previousState.copy(inProgress = false, measurementLog = resultState.measurementLog, saveError = Error.Empty.INSTANCE)
                    is LogMeasurementInteractor.State.Error -> previousState.copy(inProgress = false, saveError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown result state: $resultState")
    }
}