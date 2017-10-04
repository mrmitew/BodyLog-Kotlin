package com.github.mrmitew.bodylog.adapter.weight.view

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.weight.intent.LogWeightIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import io.reactivex.Observable

interface LogWeightView : BaseView<LogWeightView.State> {
    data class State(val weightLog: Log.Weight,
                     val inProgress: Boolean,
                     val saveSuccessful: Boolean,
                     val saveError: Throwable) : ViewState {
        object Factory {
            fun default() = State(weightLog = Log.Weight(0f, 0f, 0),
                    inProgress = false,
                    saveSuccessful = false,
                    saveError = Error.Empty.INSTANCE)
        }
    }

    class NoOp : LogWeightView, NoOpView {
        override fun render(state: State) {
            // no-op
        }

        override fun logWeightIntent(): Observable<LogWeightIntent> = Observable.empty()
    }

    fun logWeightIntent(): Observable<LogWeightIntent>
}