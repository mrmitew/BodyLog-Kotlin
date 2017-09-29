package com.github.mrmitew.bodylog.adapter.dashboard.weight.view

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.dashboard.weight.intent.LoadWeightLogIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import io.reactivex.Observable

interface WeightLogGraphView : BaseView<WeightLogGraphView.State> {
    data class State(val weightLogList: List<Log.Weight>,
                     val inProgress: Boolean,
                     val loadSuccessful: Boolean,
                     val loadError: Throwable) : ViewState {
        object Factory {
            fun idle() = State(weightLogList = arrayListOf(),
                    inProgress = false,
                    loadSuccessful = false,
                    loadError = Error.Empty.INSTANCE)
        }
    }

    class NoOp : WeightLogGraphView, NoOpView {
        override fun render(state: State) {
            // no-op
        }

        override fun loadWeightLogIntent(): Observable<LoadWeightLogIntent> = Observable.empty()
    }

    fun loadWeightLogIntent(): Observable<LoadWeightLogIntent>
}