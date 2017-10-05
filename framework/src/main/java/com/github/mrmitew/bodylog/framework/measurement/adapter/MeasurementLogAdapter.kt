package com.github.mrmitew.bodylog.framework.measurement.adapter

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.intent.LoadMeasurementLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.presenter.MeasurementLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.view.MeasurementLogView
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_measurement.view.*
import kotlinx.android.synthetic.main.layout_measurement_log_content.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MeasurementLogAdapter @Inject constructor(private val activity: AppCompatActivity) : RecyclerView.Adapter<MeasurementLogAdapter.ViewHolder>(), LifecycleObserver, MeasurementLogView {
    class PresenterHolder(application: Application) : BasePresenterHolder<MeasurementLogView, MeasurementLogView.State>(application) {
        @Inject override lateinit var presenter: MeasurementLogPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        private val simpleDateFormat = SimpleDateFormat("dd MMM (HH:mm)", Locale.ENGLISH)
        fun bind(measurement: Log.Measurement) {
            measurement.apply {
                itemView.vg_measurement_root.apply {
                    tv_date.text = simpleDateFormat.format(timestamp)
                    tv_back_size.text = backSize.toString()
                    tv_chest_size.text = chestSize.toString()
                    tv_arms_size.text = armsSize.toString()
                    tv_waist_size.text = waistSize.toString()
                }
            }
        }
    }

    private val view: MeasurementLogView

    init {
        view = this
        presenterInit()
        activity.lifecycle.addObserver(this)
    }

    private val measurementLogList: MutableList<Log.Measurement> = mutableListOf()
    private lateinit var presenterHolder: PresenterHolder

    override fun loadMeasurementLogIntent(): Observable<LoadMeasurementLogIntent> =
            Observable.just(LoadMeasurementLogIntent())

    override fun getItemCount(): Int = measurementLogList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_measurement, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (measurementLogList.isNotEmpty())
            holder?.bind(measurementLogList[position])
    }

    override fun render(state: MeasurementLogView.State) {
        if (state.loadError !is Error.Empty) {
            measurementLogList.clear()
            notifyDataSetChanged()
        }

        if (state.loadSuccessful) {
            val lastKnownIndex = measurementLogList.lastIndex
            measurementLogList.addAll(state.measurementLogList)
            notifyItemRangeInserted(lastKnownIndex + 1, state.measurementLogList.size)
        }
    }

    /**
     * Presenter and presenter holder code
     */
    private fun presenterInit() {
        setPresenterHolder()
    }

    private fun setPresenterHolder() {
        presenterHolder = injectPresenterHolder()
    }

    fun injectPresenterHolder(): PresenterHolder =
            ViewModelProviders.of(activity).get(PresenterHolder::class.java)


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAttachedToWindow() {
        presenterHolder.onAttachedToWindow(view)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onDetachedFromWindow() {
        presenterHolder.onDetachedFromWindow()
    }
}