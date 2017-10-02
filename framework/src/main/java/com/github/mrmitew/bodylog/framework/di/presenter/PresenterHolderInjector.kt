package com.github.mrmitew.bodylog.framework.di.presenter

import com.github.mrmitew.bodylog.framework.profile.details.view.LastUpdatedTextView
import com.github.mrmitew.bodylog.framework.profile.details.view.ProfileDetailsActivity
import com.github.mrmitew.bodylog.framework.profile.edit.view.ProfileEditActivity
import com.github.mrmitew.bodylog.framework.weight.view.WeightLogLineChart

interface PresenterHolderInjector {
    fun inject(target: ProfileDetailsActivity.PresenterHolder)
    fun inject(target: ProfileEditActivity.PresenterHolder)
    fun inject(target: LastUpdatedTextView.PresenterHolder)
    fun inject(target: WeightLogLineChart.PresenterHolder)
}
