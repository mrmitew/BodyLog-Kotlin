package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.view

import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.intent.GetProfileLastUpdatedIntent
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model.LastUpdatedTextState

import io.reactivex.Observable

interface LastUpdatedView : BaseView<LastUpdatedTextState> {
    fun profileLastUpdatedIntent(): Observable<GetProfileLastUpdatedIntent>

    class NoOp : LastUpdatedView, NoOpView {
        override fun render(state: LastUpdatedTextState) {
            // no-op
        }

        override fun profileLastUpdatedIntent(): Observable<GetProfileLastUpdatedIntent> = Observable.empty()
    }
}
