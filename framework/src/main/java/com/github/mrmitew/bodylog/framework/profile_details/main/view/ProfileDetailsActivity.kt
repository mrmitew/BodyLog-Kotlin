package com.github.mrmitew.bodylog.framework.profile_details.main.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_details.main.model.ProfileDetailsState
import com.github.mrmitew.bodylog.adapter.profile_details.main.presenter.ProfileDetailsPresenter
import com.github.mrmitew.bodylog.adapter.profile_details.main.view.ProfileDetailsView
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableActivity
import com.github.mrmitew.bodylog.framework.di.activity.HasActivitySubcomponentBuilders
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import com.github.mrmitew.bodylog.framework.profile_details.di.ProfileDetailsActivityComponent
import com.github.mrmitew.bodylog.framework.profile_edit.main.view.ProfileEditActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_profile_details.*
import javax.inject.Inject

class ProfileDetailsActivity : BasePresentableActivity<ProfileDetailsView, ProfileDetailsState>(), ProfileDetailsView {
    class PresenterHolder(application: Application) : BasePresenterHolder<ProfileDetailsView, ProfileDetailsState>(application) {
        @Inject override lateinit var presenter: ProfileDetailsPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    override val view: ProfileDetailsView = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_details)
        setClickListeners()
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) =
            (hasActivitySubcomponentBuilders.getActivityComponentBuilder(ProfileDetailsActivity::class.java) as ProfileDetailsActivityComponent.Builder)
                    .activityModule(ProfileDetailsActivityComponent.ComponentModule(this))
                    .build()
                    .injectMembers(this)

    override fun injectPresenterHolder(): BasePresenterHolder<ProfileDetailsView, ProfileDetailsState> =
            ViewModelProviders.of(this).get(PresenterHolder::class.java)

    override fun loadProfileIntent(): Observable<LoadProfileIntent> = Observable.just(LoadProfileIntent())

    override fun render(state: ProfileDetailsState) {
        val hasError = state.loadError !is com.github.mrmitew.bodylog.adapter.common.model.StateError.Error.Empty

        // Inflate the layout with the content from the state
        inflate(state.profile)

        if (hasError) {
            // TODO: 9/5/17 Give feedback to the user
            println("render: (hasError) ${state.loadError}")
        }

        // Layout visibility
        vg_state_loading.visibility = if (state.inProgress) View.VISIBLE else View.GONE
        vg_state_no_result.visibility = if (state.loadSuccessful) View.GONE else View.VISIBLE
        vg_state_error.visibility = if (hasError) View.VISIBLE else View.GONE
    }

    private fun inflate(profile: Profile) =
            profile.apply {
                tv_name.text = name
                tv_description.text = description
                tv_weight.text = weight.toString()
                tv_body_fat_percentage.text = bodyFatPercentage.toString()
                tv_back_size.text = backSize.toString()
                tv_chest_size.text = chestSize.toString()
                tv_arms_size.text = armsSize.toString()
                tv_waist_size.text = waistSize.toString()
            }

    private fun setClickListeners() = btn_edit.setOnClickListener { onEditRequest() }

    private fun onEditRequest() = startActivity(ProfileEditActivity.getCallingIntent(this))
}