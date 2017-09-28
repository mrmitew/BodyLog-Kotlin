package com.github.mrmitew.bodylog.adapter.common.view

import com.github.mrmitew.bodylog.adapter.common.ViewState

interface BaseView<S : ViewState> {
    fun render(state: S)
}