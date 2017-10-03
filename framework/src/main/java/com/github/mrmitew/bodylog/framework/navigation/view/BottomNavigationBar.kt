package com.github.mrmitew.bodylog.framework.navigation.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.profile.common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile.details.main.model.ProfileDetailsState
import com.github.mrmitew.bodylog.adapter.profile.details.main.presenter.ProfileDetailsPresenter
import com.github.mrmitew.bodylog.adapter.profile.details.main.view.ProfileDetailsView
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import io.reactivex.Observable
import javax.inject.Inject

class BottomNavigationBar : BottomNavigationView, ProfileDetailsView {
    class PresenterHolder(application: Application) : BasePresenterHolder<ProfileDetailsView, ProfileDetailsState>(application) {
        // We'll re-use the ProfileDetailsPresenter here, since there will be no difference if we
        // were to create a separate presenter for the bottom navigation.
        // However, if need to add more reactive functionality, specifically for the navigation menu,
        // then we'll need to create a separate presenter, which will re-use/hook to the LoadProfileInteractor
        @Inject override lateinit var presenter: ProfileDetailsPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    private lateinit var presenterHolder: BasePresenterHolder<ProfileDetailsView, ProfileDetailsState>

    constructor(context: Context) : super(context) {
        presenterInit()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        presenterInit()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        presenterInit()
    }

    override fun render(state: ProfileDetailsState) {
        if (state.inProgress || state.loadError !is Error.Empty) {
            menu.findItem(R.id.action_profile).title = context.getString(R.string.profile)
        }

        if (state.loadSuccessful) {
            menu.findItem(R.id.action_profile).title = state.profile.name
        }
    }

    override fun loadProfileIntent(): Observable<LoadProfileIntent>
            = Observable.just(LoadProfileIntent())


    private fun presenterInit() {
        inflateMenu(R.menu.dashboard_bottom_navigation)
        if (!isInEditMode) {
            setPresenterHolder()
        }
    }

    private fun setPresenterHolder() {
        presenterHolder =
                ViewModelProviders.of(context as AppCompatActivity).get(PresenterHolder::class.java)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            presenterHolder.onAttachedToWindow(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode) {
            presenterHolder.onDetachedFromWindow()
        }
    }
}
