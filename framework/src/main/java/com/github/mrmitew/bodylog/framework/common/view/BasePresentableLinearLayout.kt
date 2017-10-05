package com.github.mrmitew.bodylog.framework.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.framework.common.presenter.BasePresenterHolder

abstract class BasePresentableLinearLayout<V : BaseView<S>, S : ViewState> : LinearLayout, Presentable<V, S> {
    private lateinit var presenterHolder: BasePresenterHolder<V, S>

    constructor(context: Context) : super(context) {
        presenterInit()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        presenterInit()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        presenterInit()
    }

    private fun presenterInit() {
        if (!isInEditMode) {
            setPresenterHolder()
        }
    }

    private fun setPresenterHolder() {
        presenterHolder = injectPresenterHolder()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            presenterHolder.onAttachedToWindow(view)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode) {
            presenterHolder.onDetachedFromWindow()
        }
    }
}