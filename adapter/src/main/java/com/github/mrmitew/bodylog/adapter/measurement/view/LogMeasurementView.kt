package com.github.mrmitew.bodylog.adapter.measurement.view

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.measurement.intent.LogMeasurementIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import io.reactivex.Observable

interface LogMeasurementView : BaseView<LogMeasurementView.State> {
    data class State(val measurementLog: Log.Measurement,
                     val inProgress: Boolean,
                     val saveSuccessful: Boolean,
                     val saveError: Throwable) : ViewState {
        object Factory {
            fun default() = State(measurementLog = Log.Measurement(0f, 0f, 0f, 0f, 0),
                    inProgress = false,
                    saveSuccessful = false,
                    saveError = Error.Empty.INSTANCE)
        }
    }

    class NoOp : LogMeasurementView, NoOpView {
        override fun render(state: State) {
            // no-op
        }

        override fun logMeasurement(): Observable<LogMeasurementIntent> = Observable.empty()
    }

    fun logMeasurement(): Observable<LogMeasurementIntent>
}