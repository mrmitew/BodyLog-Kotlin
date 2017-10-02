package com.github.mrmitew.bodylog.adapter.dashboard.measurement.view

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.intent.LoadMeasurementLogIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import io.reactivex.Observable

interface MeasurementLogView : BaseView<MeasurementLogView.State> {
    data class State(val measurementLogList: List<Log.Measurement>,
                     val inProgress: Boolean,
                     val loadSuccessful: Boolean,
                     val loadError: Throwable) : ViewState {
        object Factory {
            fun default() = State(measurementLogList = arrayListOf(),
                    inProgress = false,
                    loadSuccessful = false,
                    loadError = Error.Empty.INSTANCE)
        }
    }

    class NoOp : MeasurementLogView, NoOpView {
        override fun render(state: State) {
            // no-op
        }

        override fun loadMeasurementLogIntent(): Observable<LoadMeasurementLogIntent> = Observable.empty()
    }

    fun loadMeasurementLogIntent(): Observable<LoadMeasurementLogIntent>
}