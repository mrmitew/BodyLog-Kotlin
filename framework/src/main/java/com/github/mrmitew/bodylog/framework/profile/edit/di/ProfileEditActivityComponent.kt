package com.github.mrmitew.bodylog.framework.profile.edit.di

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.profile.common.interactor.CheckRequiredFieldsInteractor
import com.github.mrmitew.bodylog.adapter.profile.common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile.edit.interactor.SaveProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile.edit.model.ProfileEditState
import com.github.mrmitew.bodylog.adapter.profile.edit.presenter.ProfileEditPresenter
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponent
import com.github.mrmitew.bodylog.framework.di.activity.ActivityComponentBuilder
import com.github.mrmitew.bodylog.framework.di.activity.ActivityModule
import com.github.mrmitew.bodylog.framework.di.activity.ActivityScope
import com.github.mrmitew.bodylog.framework.profile.edit.view.ProfileEditActivity
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton

@ActivityScope
@Subcomponent(modules = arrayOf(ProfileEditActivityComponent.ComponentModule::class))
interface ProfileEditActivityComponent : ActivityComponent<ProfileEditActivity> {
    @Singleton
    @Module
    class PresenterModule {
        @Provides
        internal fun providesProfileEditPresenter(loadProfileInteractor: LoadProfileInteractor,
                                                  checkRequiredFieldsInteractor: CheckRequiredFieldsInteractor,
                                                  saveProfileInteractor: SaveProfileInteractor): ProfileEditPresenter {
            return ProfileEditPresenter(loadProfileInteractor = loadProfileInteractor,
                    saveProfileInteractor = saveProfileInteractor,
                    checkRequiredFieldsInteractor = checkRequiredFieldsInteractor,
                    profileResultStateRelay = BehaviorRelay.create<ResultState>(),
                    initialState = ProfileEditState.Factory.idle())
        }
    }

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<ProfileEditActivityComponent.ComponentModule, ProfileEditActivityComponent>

    @ActivityScope
    @Module
    class ComponentModule(activity: ProfileEditActivity) : ActivityModule<ProfileEditActivity>(activity)
}