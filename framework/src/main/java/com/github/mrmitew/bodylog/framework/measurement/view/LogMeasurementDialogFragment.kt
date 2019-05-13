package com.github.mrmitew.bodylog.framework.measurement.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.measurement.intent.LogMeasurementIntent
import com.github.mrmitew.bodylog.adapter.measurement.presenter.LogMeasurementPresenter
import com.github.mrmitew.bodylog.adapter.measurement.view.LogMeasurementView
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableDialogFragment
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_log_measurement.*
import javax.inject.Inject

class LogMeasurementDialogFragment : BasePresentableDialogFragment<LogMeasurementView, LogMeasurementView.State>(), LogMeasurementView {
    class PresenterHolder(application: Application) : BasePresenterHolder<LogMeasurementView, LogMeasurementView.State>(application) {
        @Inject override lateinit var presenter: LogMeasurementPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    companion object {
        fun newInstance() = LogMeasurementDialogFragment()
        fun startFragment(fragmentManager: FragmentManager) =
                newInstance().show(fragmentManager, this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_log_measurement, container, false)

    override val view: LogMeasurementView = this

    override fun injectPresenterHolder(): PresenterHolder =
            ViewModelProviders.of(this).get(PresenterHolder::class.java)

    override fun logMeasurementIntent(): Observable<LogMeasurementIntent> =
            btn_log.clicks()
                    .map {
                        LogMeasurementIntent(Log.Measurement(
                                backSize = et_back_size.text.toString().toFloat(),
                                chestSize = et_chest_size.text.toString().toFloat(),
                                armsSize = et_arms_size.text.toString().toFloat(),
                                waistSize = et_waist_size.text.toString().toFloat()))
                    }

    override fun render(state: LogMeasurementView.State) {
        pb_state_loading.visibility = if (state.inProgress) View.VISIBLE else View.GONE
        btn_log.visibility = if (!state.inProgress) View.VISIBLE else View.GONE

        if (state.saveError !is Error.Empty) {
            //TODO
            println(state.saveError)
        }

        if (state.saveSuccessful) {
            dismiss()
        }
    }

}