package com.github.mrmitew.bodylog.framework.main.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeableViewPager : ViewPager {
    var swipeEnabled: Boolean

    constructor(context: Context) : super(context) {
        swipeEnabled = false
    }

    constructor(context: Context, enabled: Boolean) : super(context) {
        swipeEnabled = enabled
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        swipeEnabled = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (swipeEnabled) {
            return super.onTouchEvent(event)
        }
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (swipeEnabled) {
            return super.onInterceptTouchEvent(event)
        }
        return false
    }
}