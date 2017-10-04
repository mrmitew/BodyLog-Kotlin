package com.github.mrmitew.bodylog.framework.weight.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.weight.intent.LogWeightIntent
import com.github.mrmitew.bodylog.adapter.weight.presenter.LogWeightPresenter
import com.github.mrmitew.bodylog.adapter.weight.view.LogWeightView
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableDialogFragment
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_log_weight.*
import javax.inject.Inject


class LogWeightDialogFragment : BasePresentableDialogFragment<LogWeightView, LogWeightView.State>(), LogWeightView {
    class PresenterHolder(application: Application) : BasePresenterHolder<LogWeightView, LogWeightView.State>(application) {
        @Inject override lateinit var presenter: LogWeightPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    companion object {
        fun newInstance() = LogWeightDialogFragment()
        fun startFragment(fragmentManager: FragmentManager) =
                newInstance().show(fragmentManager, this::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_log_weight, container, false)

    override val view: LogWeightView = this

    override fun injectPresenterHolder(): PresenterHolder =
            ViewModelProviders.of(this).get(PresenterHolder::class.java)

    override fun logWeightIntent(): Observable<LogWeightIntent> =
            btn_log.clicks()
                    .map {
                        LogWeightIntent(Log.Weight(
                                weight = et_weight.text.toString().toFloat(),
                                fatPercentage = et_body_fat_percentage.text.toString().toFloat()
                        ))
                    }

    override fun render(state: LogWeightView.State) {
        pb_state_loading.visibility = if (state.inProgress) VISIBLE else GONE
        btn_log.visibility = if (!state.inProgress) VISIBLE else GONE

        if (state.saveError !is Error.Empty) {
            //TODO
            println(state.saveError)
        }

        if (state.saveSuccessful) {
            dismiss()
        }
    }

}
