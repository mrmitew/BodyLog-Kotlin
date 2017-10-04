package com.github.mrmitew.bodylog.framework.main.di

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.interactor.LoadMeasurementLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.presenter.MeasurementLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.interactor.LoadWeightLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter.WeightLogPresenter
import com.github.mrmitew.bodylog.adapter.profile.common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.model.LastUpdatedTextState
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.presenter.LastUpdatedPresenter
import com.github.mrmitew.bodylog.adapter.profile.details.main.model.ProfileDetailsState
import com.github.mrmitew.bodylog.adapter.profile.details.main.presenter.ProfileDetailsPresenter
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponent
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponentBuilder
import com.github.mrmitew.bodylog.framework.di.activity.ActivityModule
import com.github.mrmitew.bodylog.framework.di.activity.ActivityScope
import com.github.mrmitew.bodylog.framework.main.view.MainActivity
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Singleton

@ActivityScope
@Subcomponent(modules = arrayOf(MainActivityComponent.ComponentModule::class))
interface MainActivityComponent : ActivityComponent<MainActivity> {
    @Singleton
    @Module
    class PresenterModule {
        /**
         * Dashboard
         */
        @Provides
        internal fun providesWeightLogPresenter(loadWeightLogInteractor: LoadWeightLogInteractor) =
                WeightLogPresenter(loadWeightLogInteractor)

        @Provides
        internal fun providesMeasurementLogPresenter(loadMeasurementLogInteractor: LoadMeasurementLogInteractor) =
                MeasurementLogPresenter(loadMeasurementLogInteractor)

        /**
         * Profile details
         */
        @Provides
        @Named("loadProfileInteractorRelay")
        internal fun providesLoadProfileInteractorRelay(): BehaviorRelay<ResultState> {
            return BehaviorRelay.create()
        }

        @Provides
        internal fun providesProfileDetailsPresenter(loadProfileInteractor: LoadProfileInteractor, @Named("loadProfileInteractorRelay") resultStateBehaviorRelay: BehaviorRelay<ResultState>): ProfileDetailsPresenter {
            return ProfileDetailsPresenter(loadProfileInteractor, resultStateBehaviorRelay, ProfileDetailsState.Factory.inProgress())
        }

        @Provides
        internal fun providesLastUpdatedPresenter(loadProfileInteractor: LoadProfileInteractor, @Named("loadProfileInteractorRelay") resultStateBehaviorRelay: BehaviorRelay<ResultState>): LastUpdatedPresenter {
            return LastUpdatedPresenter(loadProfileInteractor, resultStateBehaviorRelay, LastUpdatedTextState.Factory.idle())
        }
    }

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ComponentModule, MainActivityComponent>

    @ActivityScope
    @Module
    class ComponentModule(activity: MainActivity) : ActivityModule<MainActivity>(activity)
}