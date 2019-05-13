package com.github.mrmitew.bodylog.adapter.common.view

import com.github.mrmitew.bodylog.adapter.common.model.ViewState

/**
 * View that knows how to render its state
 */
interface BaseView<in S : ViewState> {
    fun render(state: S)
}