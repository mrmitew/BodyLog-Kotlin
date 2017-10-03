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
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableLinearLayout
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_measurement.view.*
import kotlinx.android.synthetic.main.measurement_content.view.*
import javax.inject.Inject

class MeasurementLogLayout : BasePresentableLinearLayout<MeasurementLogView, MeasurementLogView.State>, MeasurementLogView {
    class PresenterHolder(application: Application) : BasePresenterHolder<MeasurementLogView, MeasurementLogView.State>(application) {
        @Inject override lateinit var presenter: MeasurementLogPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    override val view: MeasurementLogView = this

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
        ll_measurements_result.visibility = if (!state.inProgress) View.VISIBLE else View.GONE
        pb_state_loading.visibility = if (state.inProgress) View.VISIBLE else View.GONE

        if (state.loadError !is Error.Empty) {
            //TODO
            println(state.loadError)
        }

        if (state.loadSuccessful) {
            inflateMeasurementLog(state.measurementLogList)
        }
    }

    private fun init() {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.layout_measurement, this, true)
    }

    private fun inflateMeasurementLog(measurementLogList: List<Log.Measurement>) {
        if (measurementLogList.isEmpty()) {
            // TODO: show a placeholder?
            println("No measurement taken yet")
        } else {
            val lastIndex = measurementLogList.lastIndex
            val beforeLastIndex = lastIndex - 1

            if (beforeLastIndex > 0) {
                measurementLogList[beforeLastIndex].apply {
                    ll_previous_measurement.apply {
                        tv_back_size.text = backSize.toString()
                        tv_chest_size.text = chestSize.toString()
                        tv_arms_size.text = armsSize.toString()
                        tv_waist_size.text = waistSize.toString()
                    }
                }
            } else {
                ll_previous_measurement.visibility = View.GONE
            }

            measurementLogList[lastIndex].apply {
                ll_current_measurement.apply {
                    tv_back_size.text = backSize.toString()
                    tv_chest_size.text = chestSize.toString()
                    tv_arms_size.text = armsSize.toString()
                    tv_waist_size.text = waistSize.toString()
                }
            }
        }
    }
}