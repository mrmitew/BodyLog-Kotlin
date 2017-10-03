package com.github.mrmitew.bodylog.framework.weight.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.dashboard.weight.intent.LoadWeightLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter.WeightLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.view.WeightLogView
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableLinearLayout
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_weight_log.view.*
import javax.inject.Inject

class WeightLogLayout : BasePresentableLinearLayout<WeightLogView, WeightLogView.State>, WeightLogView {
    class PresenterHolder(application: Application) : BasePresenterHolder<WeightLogView, WeightLogView.State>(application) {
        @Inject override lateinit var presenter: WeightLogPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    override val view: WeightLogView = this

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun injectPresenterHolder(): PresenterHolder =
            ViewModelProviders.of(context as AppCompatActivity).get(PresenterHolder::class.java)

    override fun loadWeightLogIntent(): Observable<LoadWeightLogIntent> =
            Observable.just(LoadWeightLogIntent())

    override fun render(state: WeightLogView.State) {
        vg_state_loading.visibility = if (state.inProgress) VISIBLE else GONE

        if (state.loadError !is Error.Empty) {
            //TODO
            println(state.loadError)
        }
    }

    private fun init() {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.layout_weight_log, this, true)
    }
}