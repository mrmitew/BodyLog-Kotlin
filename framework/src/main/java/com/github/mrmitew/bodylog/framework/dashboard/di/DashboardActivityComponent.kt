package com.github.mrmitew.bodylog.framework.dashboard.di

import com.github.mrmitew.bodylog.adapter.dashboard.measurement.interactor.LoadMeasurementLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.presenter.MeasurementLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.interactor.LoadWeightLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter.WeightLogPresenter
import com.github.mrmitew.bodylog.framework.dashboard.view.DashboardActivity
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponent
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponentBuilder
import com.github.mrmitew.bodylog.framework.di.activity.ActivityModule
import com.github.mrmitew.bodylog.framework.di.activity.ActivityScope
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton

@ActivityScope
@Subcomponent(modules = arrayOf(DashboardActivityComponent.ComponentModule::class))
interface DashboardActivityComponent : ActivityComponent<DashboardActivity> {
    @Singleton
    @Module
    class PresenterModule {
        @Provides
        internal fun providesWeightLogPresenter(loadWeightLogInteractor: LoadWeightLogInteractor) =
                WeightLogPresenter(loadWeightLogInteractor)

        @Provides
        internal fun providesMeasurementLogPresenter(loadMeasurementLogInteractor: LoadMeasurementLogInteractor) =
                MeasurementLogPresenter(loadMeasurementLogInteractor)
    }

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ComponentModule, DashboardActivityComponent>

    @ActivityScope
    @Module
    class ComponentModule(activity: DashboardActivity) : ActivityModule<DashboardActivity>(activity)
}