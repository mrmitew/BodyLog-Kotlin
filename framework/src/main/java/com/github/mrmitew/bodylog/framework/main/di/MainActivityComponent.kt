package com.github.mrmitew.bodylog.framework.main.di

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.interactor.LoadMeasurementLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.presenter.MeasurementLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.interactor.LoadWeightLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter.WeightLogPresenter
import com.github.mrmitew.bodylog.adapter.measurement.interactor.LogMeasurementInteractor
import com.github.mrmitew.bodylog.adapter.measurement.presenter.LogMeasurementPresenter
import com.github.mrmitew.bodylog.adapter.profile.common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.model.LastUpdatedTextState
import com.github.mrmitew.bodylog.adapter.profile.details.last_updated.presenter.LastUpdatedPresenter
import com.github.mrmitew.bodylog.adapter.profile.details.main.model.ProfileDetailsState
import com.github.mrmitew.bodylog.adapter.profile.details.main.presenter.ProfileDetailsPresenter
import com.github.mrmitew.bodylog.adapter.weight.interactor.LogWeightInteractor
import com.github.mrmitew.bodylog.adapter.weight.presenter.LogWeightPresenter
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponent
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponentBuilder
import com.github.mrmitew.bodylog.framework.di.activity.ActivityModule
import com.github.mrmitew.bodylog.framework.di.activity.ActivityScope
import com.github.mrmitew.bodylog.framework.main.view.MainActivity
import com.github.mrmitew.bodylog.framework.measurement.adapter.MeasurementLogAdapter
import com.github.mrmitew.bodylog.framework.measurement.view.MeasurementLogLayout
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Singleton

@ActivityScope
@Subcomponent(modules = arrayOf(MainActivityComponent.ComponentModule::class))
interface MainActivityComponent : ActivityComponent<MainActivity>, MeasurementLogLayout.MeasurementLogProviderComponent {
    @Singleton
    @Module
    class PresenterModule {
        /**
         * Dashboard
         */
        @Provides
        internal fun providesWeightLogPresenter(loadWeightLogInteractor: LoadWeightLogInteractor,
                                                @Named("weightLogInteractorRelay") weightLogInteractorRelay: BehaviorRelay<ResultState>) =
                WeightLogPresenter(loadWeightLogInteractor, weightLogInteractorRelay)

        @Provides
        internal fun providesMeasurementLogPresenter(loadMeasurementLogInteractor: LoadMeasurementLogInteractor,
                                                     @Named("measurementLogInteractorRelay") measurementLogResultStateRelay: BehaviorRelay<ResultState>) =
                MeasurementLogPresenter(loadMeasurementLogInteractor, measurementLogResultStateRelay)

        // Share these relays across instances of presenters as we are reusing them for some views
        @Provides
        @Named("loadProfileInteractorRelay")
        internal fun providesLoadProfileInteractorRelay(): BehaviorRelay<ResultState> =
                BehaviorRelay.create()

        @Provides
        @Named("measurementLogInteractorRelay")
        internal fun provideMeasurementLogRelay(): BehaviorRelay<ResultState> =
                BehaviorRelay.create()

        @Provides
        @Named("weightLogInteractorRelay")
        internal fun provideWeightLogStateRelay(): BehaviorRelay<ResultState> =
                BehaviorRelay.create()

        /**
         * Profile details
         */
        @Provides
        internal fun providesProfileDetailsPresenter(loadProfileInteractor: LoadProfileInteractor,
                                                     @Named("loadProfileInteractorRelay") resultStateBehaviorRelay: BehaviorRelay<ResultState>): ProfileDetailsPresenter =
                ProfileDetailsPresenter(loadProfileInteractor, resultStateBehaviorRelay, ProfileDetailsState.Factory.inProgress())

        @Provides
        internal fun providesLastUpdatedPresenter(loadProfileInteractor: LoadProfileInteractor,
                                                  @Named("loadProfileInteractorRelay") resultStateBehaviorRelay: BehaviorRelay<ResultState>): LastUpdatedPresenter =
                LastUpdatedPresenter(loadProfileInteractor, resultStateBehaviorRelay, LastUpdatedTextState.Factory.idle())

        /**
         * Log weight
         */
        @Provides
        internal fun providesLogWeightPresenter(logWeightInteractor: LogWeightInteractor) =
                LogWeightPresenter(logWeightInteractor)

        /**
         * Log measurement
         */
        @Provides
        internal fun providesLogMeasurementPresenter(logMeasurementInteractor: LogMeasurementInteractor) =
                LogMeasurementPresenter(logMeasurementInteractor)

    }

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ComponentModule, MainActivityComponent>

    @ActivityScope
    @Module
    class ComponentModule(activity: MainActivity) : ActivityModule<MainActivity>(activity) {
        /**
         * Dashboard
         */
        @Provides
        fun provideMeasurementLogAdapter(activity: MainActivity) = MeasurementLogAdapter(activity)
    }
}