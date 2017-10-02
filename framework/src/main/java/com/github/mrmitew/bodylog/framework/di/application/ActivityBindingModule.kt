package com.github.mrmitew.bodylog.framework.di.application

import com.github.mrmitew.bodylog.framework.dashboard.di.DashboardActivityComponent
import com.github.mrmitew.bodylog.framework.dashboard.view.DashboardActivity
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponentBuilder
import com.github.mrmitew.bodylog.framework.di.activity.ActivityKey
import com.github.mrmitew.bodylog.framework.profile.details.di.ProfileDetailsActivityComponent
import com.github.mrmitew.bodylog.framework.profile.details.view.ProfileDetailsActivity
import com.github.mrmitew.bodylog.framework.profile.edit.di.ProfileEditActivityComponent
import com.github.mrmitew.bodylog.framework.profile.edit.view.ProfileEditActivity

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * All components defined here will become subcomponents of the parent component - ApplicationComponent
 */
@Module(subcomponents = arrayOf(DashboardActivityComponent::class,
        ProfileDetailsActivityComponent::class,
        ProfileEditActivityComponent::class))
internal abstract class ActivityBindingModule {
    @Binds
    @IntoMap
    @ActivityKey(DashboardActivity::class)
    abstract fun dashboardActivityComponentComponentBuilder(impl: DashboardActivityComponent.Builder): ActivityComponentBuilder<*, *>

    @Binds
    @IntoMap
    @ActivityKey(ProfileDetailsActivity::class)
    abstract fun profileDetailsActivityComponentBuilder(impl: ProfileDetailsActivityComponent.Builder): ActivityComponentBuilder<*, *>

    @Binds
    @IntoMap
    @ActivityKey(ProfileEditActivity::class)
    abstract fun profileEditActivityComponentBuilder(impl: ProfileEditActivityComponent.Builder): ActivityComponentBuilder<*, *>
}
