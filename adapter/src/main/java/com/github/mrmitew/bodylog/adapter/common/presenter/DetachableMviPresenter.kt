package com.github.mrmitew.bodylog.adapter.common.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView

abstract class DetachableMviPresenter<V : BaseView<S>, S : ViewState> protected constructor(view: V) :
        BaseMviPresenter<V, S>(view), HasDetachableView<V> {

    /**
     * Represents a no-op view (view whose methods do not do anything)
     */
    protected abstract val emptyView: V

    /**
     * Attaches a view
     */
    override fun attachView(view: V) {
        this.view = view
    }

    /**
     * Detaches the current view.
     * Internally, it will swap the current view with the empty (no-op) view
     */
    override fun detachView() {
        this.view = emptyView
        unbindIntents()
    }
}
