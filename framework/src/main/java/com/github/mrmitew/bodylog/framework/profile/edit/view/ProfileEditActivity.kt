package com.github.mrmitew.bodylog.framework.profile.edit.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.profile.common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile.edit.intent.CheckRequiredFieldsIntent
import com.github.mrmitew.bodylog.adapter.profile.edit.intent.SaveProfileIntent
import com.github.mrmitew.bodylog.adapter.profile.edit.model.ProfileEditState
import com.github.mrmitew.bodylog.adapter.profile.edit.presenter.ProfileEditPresenter
import com.github.mrmitew.bodylog.adapter.profile.edit.view.ProfileEditView
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableActivity
import com.github.mrmitew.bodylog.framework.di.activity.HasActivitySubcomponentBuilders
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import com.github.mrmitew.bodylog.framework.profile.edit.di.ProfileEditActivityComponent
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_profile_edit.*
import javax.inject.Inject

class ProfileEditActivity : BasePresentableActivity<ProfileEditView, ProfileEditState>(), ProfileEditView {
    class PresenterHolder(application: Application) : BasePresenterHolder<ProfileEditView, ProfileEditState>(application) {
        @Inject override lateinit var presenter: ProfileEditPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    companion object Factory {
        fun getCallingIntent(context: Context): Intent = Intent(context, ProfileEditActivity::class.java)
    }

    override val view: ProfileEditView = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
    }

    override fun render(state: ProfileEditState) {
        // Widgets
        btn_save.isEnabled = state.requiredFieldsFilledIn && state.requiredFieldsError is Error.Empty

        // Content
        inflate(state.profile)

        // Layout visibility
        vg_state_loading.visibility = if (state.isInProgress) View.VISIBLE else View.GONE
        vg_state_error.visibility =
                if (!state.isLoadSuccessful || state.loadError !is Error.Empty)
                    View.VISIBLE
                else
                    View.GONE
        vg_state_result.visibility = if (state.loadError is Error.Empty) View.VISIBLE else View.GONE

        if (!state.isSaveSuccessful && state.saveError is Error.Empty) {
            // TODO: 9/5/17 Give feedback to the user
            println("render: ${state.saveError}")
        }
    }

    override fun injectPresenterHolder(): BasePresenterHolder<ProfileEditView, ProfileEditState> =
            ViewModelProviders.of(this).get(PresenterHolder::class.java)

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) =
            (hasActivitySubcomponentBuilders.getActivityComponentBuilder(ProfileEditActivity::class.java) as ProfileEditActivityComponent.Builder)
                    .activityModule(ProfileEditActivityComponent.ComponentModule(this))
                    .build()
                    .injectMembers(this)

    override fun loadProfileIntent(): Observable<LoadProfileIntent> = Observable.just(LoadProfileIntent())

    override fun saveIntent(): Observable<SaveProfileIntent> =
            btn_save.clicks()
                    .map {
                        SaveProfileIntent(Profile(
                                name = et_name.text.toString(),
                                description = et_description.text.toString(),
                                weight = et_weight.text.toString().toFloat(),
                                bodyFatPercentage = et_body_fat_percentage.text.toString().toFloat(),
                                armsSize = et_arms_size.text.toString().toFloat(),
                                backSize = et_back_size.text.toString().toFloat(),
                                chestSize = et_chest_size.text.toString().toFloat(),
                                waistSize = et_waist_size.text.toString().toFloat()))
                    }

    override fun requiredFieldsFilledInIntent(): Observable<CheckRequiredFieldsIntent> =
            Observable.combineLatest(
                    getNameIntent().map { name -> name.isNotEmpty() },
                    getDescriptionIntent().map { description -> description.isNotEmpty() },
                    BiFunction({ isNameFilledIn: Boolean, isDescriptionFilledIn: Boolean -> isNameFilledIn && isDescriptionFilledIn }))
                    .distinctUntilChanged()
                    .map { CheckRequiredFieldsIntent(it) }

    private fun getNameIntent() =
            et_name.textChanges()
                    .skip(1)
                    .map { it.toString() }

    private fun getDescriptionIntent() =
            et_description.textChanges()
                    .skip(1)
                    .map { it.toString() }

    private fun inflate(profile: Profile) =
            profile.apply {
                et_name.setText(name)
                et_description.setText(description)
                et_weight.setText(weight.toString())
                et_body_fat_percentage.setText(bodyFatPercentage.toString())
                et_back_size.setText(backSize.toString())
                et_chest_size.setText(chestSize.toString())
                et_arms_size.setText(armsSize.toString())
                et_waist_size.setText(waistSize.toString())
            }
}