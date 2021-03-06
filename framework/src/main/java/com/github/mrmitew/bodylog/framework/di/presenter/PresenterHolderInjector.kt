package com.github.mrmitew.bodylog.framework.di.presenter

import com.github.mrmitew.bodylog.framework.measurement.adapter.MeasurementLogAdapter
import com.github.mrmitew.bodylog.framework.measurement.view.LogMeasurementDialogFragment
import com.github.mrmitew.bodylog.framework.measurement.view.MeasurementLogLayout
import com.github.mrmitew.bodylog.framework.navigation.view.BottomNavigationBar
import com.github.mrmitew.bodylog.framework.profile.details.view.LastUpdatedTextView
import com.github.mrmitew.bodylog.framework.profile.details.view.ProfileDetailsFragment
import com.github.mrmitew.bodylog.framework.profile.edit.view.ProfileEditActivity
import com.github.mrmitew.bodylog.framework.weight.view.LogWeightDialogFragment
import com.github.mrmitew.bodylog.framework.weight.view.WeightLogLayout
import com.github.mrmitew.bodylog.framework.weight.view.WeightLogLineChart

interface PresenterHolderInjector {
    fun inject(target: ProfileEditActivity.PresenterHolder)
    fun inject(target: LastUpdatedTextView.PresenterHolder)
    fun inject(target: WeightLogLineChart.PresenterHolder)
    fun inject(target: MeasurementLogLayout.PresenterHolder)
    fun inject(target: WeightLogLayout.PresenterHolder)
    fun inject(target: ProfileDetailsFragment.PresenterHolder)
    fun inject(target: BottomNavigationBar.PresenterHolder)
    fun inject(target: LogWeightDialogFragment.PresenterHolder)
    fun inject(target: LogMeasurementDialogFragment.PresenterHolder)
    fun inject(target: MeasurementLogAdapter.PresenterHolder)
}
