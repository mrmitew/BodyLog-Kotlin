package com.github.mrmitew.bodylog.adapter.common.view

import com.github.mrmitew.bodylog.adapter.common.model.ViewState

interface BaseView<in S : ViewState> {
    fun render(state: S)
}