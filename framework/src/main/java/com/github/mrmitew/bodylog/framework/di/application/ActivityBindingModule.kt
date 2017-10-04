package com.github.mrmitew.bodylog.framework.di.application

import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponentBuilder
import com.github.mrmitew.bodylog.framework.di.activity.ActivityKey
import com.github.mrmitew.bodylog.framework.main.di.MainActivityComponent
import com.github.mrmitew.bodylog.framework.main.view.MainActivity
import com.github.mrmitew.bodylog.framework.profile.edit.di.ProfileEditActivityComponent
import com.github.mrmitew.bodylog.framework.profile.edit.view.ProfileEditActivity
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * All components defined here will become subcomponents of the parent component - ApplicationComponent
 */
@Module(subcomponents = arrayOf(MainActivityComponent::class,
        ProfileEditActivityComponent::class))
internal abstract class ActivityBindingModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    abstract fun dashboardActivityComponentComponentBuilder(impl: MainActivityComponent.Builder): ActivityComponentBuilder<*, *>

    @Binds
    @IntoMap
    @ActivityKey(ProfileEditActivity::class)
    abstract fun profileEditActivityComponentBuilder(impl: ProfileEditActivityComponent.Builder): ActivityComponentBuilder<*, *>
}
