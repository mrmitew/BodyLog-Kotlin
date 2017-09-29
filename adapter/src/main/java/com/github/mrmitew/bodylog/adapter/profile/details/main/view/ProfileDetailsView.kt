package com.github.mrmitew.bodylog.adapter.profile.details.main.view

import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.profile.common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile.details.main.model.ProfileDetailsState

import io.reactivex.Observable

interface ProfileDetailsView : BaseView<ProfileDetailsState> {
    fun loadProfileIntent(): Observable<LoadProfileIntent>

    class NoOp : ProfileDetailsView, NoOpView {
        override fun render(state: ProfileDetailsState) {
            // no-op
        }

        override fun loadProfileIntent(): Observable<LoadProfileIntent> = Observable.empty()
    }
}