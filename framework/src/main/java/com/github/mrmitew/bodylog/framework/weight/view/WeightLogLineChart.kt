package com.github.mrmitew.bodylog.framework.weight.view

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mrmitew.bodylog.R
import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.dashboard.weight.intent.LoadWeightLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter.WeightLogPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.view.WeightLogView
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder
import com.github.mrmitew.bodylog.framework.common.view.BasePresentableLineChart
import com.github.mrmitew.bodylog.framework.di.presenter.PresenterHolderInjector
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WeightLogLineChart : BasePresentableLineChart<WeightLogView, WeightLogView.State>, WeightLogView {
    class PresenterHolder(application: Application) : BasePresenterHolder<WeightLogView, WeightLogView.State>(application) {
        @Inject override lateinit var presenter: WeightLogPresenter
        override fun injectMembers(injector: PresenterHolderInjector) = injector.inject(this)
    }

    override val view: WeightLogView = this

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setup()
    }

    override fun injectPresenterHolder(): BasePresenterHolder<WeightLogView, WeightLogView.State> =
            ViewModelProviders.of(context as AppCompatActivity).get(PresenterHolder::class.java)

    override fun loadWeightLogIntent(): Observable<LoadWeightLogIntent> =
            Observable.just(LoadWeightLogIntent())

    override fun render(state: WeightLogView.State) {
        println("TODO: not implemented $state")

        if(state.inProgress) {
            //TODO
        }

        if (state.loadError !is Error.Empty) {
            //TODO
            println(state.loadError)
        }

        if (state.weightLogList.isNotEmpty()) {
            addEntries(state.weightLogList.map { value -> Entry(value.timestamp.toFloat(), value.weight) })
        } else {
            data?.getDataSetByIndex(0)?.clear()
        }
    }

    private fun setup() {
        configureChart()
        if (isInEditMode) {
            // add data
            generateDummyData(100, 30f)
        }
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        legend.isEnabled = false
        invalidate()
    }

    private fun addEntries(entryList: List<Entry>) = entryList.forEach { addEntry(it) }

    private fun addEntry(entry: Entry) {
        if (data == null) {
            data = LineData()
            data.addDataSet(createDataSet())
            data.setDrawValues(true)
            data.setValueTextColor(resources.getColor(R.color.colorAccent))
            data.setValueFormatter { value, _, _, _ -> value.toString() }
            data.setValueTextSize(9f)
            data.setValueTextSize(12f)
            data.setDrawValues(true)
        }
        data.addEntry(entry, 0)
        setMaxVisibleValueCount(Integer.MAX_VALUE)
        isAutoScaleMinMaxEnabled = true
        data.notifyDataChanged()
        notifyDataSetChanged()
        moveViewToX(data.entryCount.toFloat())
    }

    private fun configureChart() {
        description.isEnabled = false

        // enable touch gestures
        setTouchEnabled(true)

        dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        isDragEnabled = true
        setScaleEnabled(true)
        setDrawGridBackground(false)
        isHighlightPerDragEnabled = true
        setNoDataTextTypeface(Typeface.SANS_SERIF)
        setNoDataTextColor(resources.getColor(R.color.colorAccent))

        // set an alternative background color
        setViewPortOffsets(0f, 20f, 0f, 0f)

        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.typeface = Typeface.SANS_SERIF
        xAxis.textSize = 10f
        xAxis.textColor = resources.getColor(R.color.colorAccent)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setCenterAxisLabels(true)
//        xAxis.granularity = 0.5f
        xAxis.valueFormatter = object : IAxisValueFormatter {
            private val simpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float, axis: AxisBase): String =
                    simpleDateFormat.format(Date(TimeUnit.HOURS.toMillis(value.toLong())))
        }
        axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        axisLeft.typeface = Typeface.SANS_SERIF
        axisLeft.textColor = resources.getColor(R.color.colorAccent)
        axisLeft.setDrawGridLines(false)
        axisLeft.isGranularityEnabled = true
        axisLeft.yOffset = -9f
        axisRight.isEnabled = false
    }

    private fun createDataSet(): ILineDataSet =
            LineDataSet(mutableListOf(), "")
                    .apply {
                        axisDependency = AxisDependency.LEFT
                        color = resources.getColor(R.color.colorPrimary)
                        valueTextColor = resources.getColor(R.color.colorPrimary)
                        valueTextSize = 12f
                        lineWidth = 2.5f
                        setCircleColor(resources.getColor(R.color.colorAccent))
                        setDrawCircles(true)
                        setDrawValues(true)
                        fillAlpha = 65
                        fillColor = resources.getColor(R.color.colorPrimary)
                        highLightColor = resources.getColor(R.color.colorPrimary)
                        setDrawCircleHole(true)
                    }

    private fun generateDummyData(count: Int, range: Float) {
        // now in hours
        val now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())

        val values = ArrayList<Entry>()

        val from = now.toFloat()

        // count = hours
        val to = (now + count).toFloat()

        // increment by 1 hour
        var x = from
        while (x < to) {
            val y = ((Math.random() * range) + 100F).toFloat()
            values.add(Entry(x, y)) // add one entry per hour
            x++
        }
        addEntries(values)
    }
}
