package com.github.mrmitew.bodylog.adapter.profile_edit.main.view

import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_edit.main.intent.CheckRequiredFieldsIntent
import com.github.mrmitew.bodylog.adapter.profile_edit.main.intent.SaveProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_edit.main.model.ProfileEditState
import io.reactivex.Observable

interface ProfileEditView : BaseView<ProfileEditState> {
    fun loadProfileIntent(): Observable<LoadProfileIntent>

    fun saveIntent(): Observable<SaveProfileIntent>

    fun requiredFieldsFilledInIntent(): Observable<CheckRequiredFieldsIntent>

    class NoOp : ProfileEditView, NoOpView {
        override fun loadProfileIntent(): Observable<LoadProfileIntent> = Observable.empty()
        override fun saveIntent(): Observable<SaveProfileIntent> = Observable.empty()
        override fun requiredFieldsFilledInIntent(): Observable<CheckRequiredFieldsIntent> = Observable.empty()
        override fun render(state: ProfileEditState) {
            // no-op
        }
    }
}
