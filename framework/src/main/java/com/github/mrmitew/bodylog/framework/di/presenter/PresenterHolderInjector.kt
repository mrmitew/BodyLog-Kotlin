package com.github.mrmitew.bodylog.framework.di.presenter

import com.github.mrmitew.bodylog.framework.profile.details.view.LastUpdatedTextView
import com.github.mrmitew.bodylog.framework.profile.details.view.ProfileDetailsActivity
import com.github.mrmitew.bodylog.framework.profile.edit.view.ProfileEditActivity

interface PresenterHolderInjector {
    fun inject(target: ProfileDetailsActivity.PresenterHolder)
    fun inject(target: ProfileEditActivity.PresenterHolder)
    fun inject(target: LastUpdatedTextView.PresenterHolder)
}
