package com.github.mrmitew.bodylog.framework.measurement.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.intent.LoadMeasurementLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.presenter.MeasurementLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.view.MeasurementLogView
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableLinearLayout
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import com.github.mrmitew.bodylog.framework.measurement.adapter.MeasurementLogAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_measurement_log.view.*
import javax.inject.Inject

class MeasurementLogLayout : BasePresentableLinearLayout<MeasurementLogView, MeasurementLogView.State>, MeasurementLogView {
    interface MeasurementLogProviderComponent {
        fun inject(target: MeasurementLogLayout)
    }

    interface MeasurementLogInjectionProvider {
        fun measurementLogProviderComponent(): MeasurementLogProviderComponent
    }

    class PresenterHolder(application: Application) : BasePresenterHolder<MeasurementLogView, MeasurementLogView.State>(application) {
        @Inject override lateinit var presenter: MeasurementLogPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    override val view: MeasurementLogView = this

    @Suppress("ProtectedInFinal") // Because of dagger
    @Inject protected lateinit var adapter: MeasurementLogAdapter

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

    override fun loadMeasurementLogIntent(): Observable<LoadMeasurementLogIntent> =
            Observable.just(LoadMeasurementLogIntent())

    override fun render(state: MeasurementLogView.State) {
        rv_measurement.visibility = if (!state.inProgress) View.VISIBLE else View.GONE
        pb_state_loading.visibility = if (state.inProgress) View.VISIBLE else View.GONE

        if (state.loadError !is Error.Empty) {
            //TODO show error view
            println(state.loadError)
        }

        if (state.loadSuccessful) {
            // TODO: Remove error view
        }
    }

    private fun init() {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.layout_measurement_log, this, true)

        if (!isInEditMode) {
            if (context is MeasurementLogInjectionProvider) {
                (context as MeasurementLogInjectionProvider)
                        .measurementLogProviderComponent().inject(this)
            } else {
                throw RuntimeException("Context should implement the MeasurementLogInjectionProvider interface")
            }
            rv_measurement.adapter = adapter
        }
    }
}