package com.github.mrmitew.bodylog.framework.di.application

import com.github.mrmitew.bodylog.framework.AndroidApplication
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import com.github.mrmitew.bodylog.framework.main.di.MainActivityComponent
import com.github.mrmitew.bodylog.framework.profile.edit.di.ProfileEditActivityComponent
import com.github.mrmitew.bodylog.framework.repository.di.RepositoryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class,
        ActivityBindingModule::class,
        RepositoryModule::class,
        MainActivityComponent.PresenterModule::class,
        ProfileEditActivityComponent.PresenterModule::class))
interface ApplicationComponent : PresenterHolderInjector {
    fun inject(application: AndroidApplication): AndroidApplication
}