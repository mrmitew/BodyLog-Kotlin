package com.github.mrmitew.bodylog.adapter.common.presenter

import com.github.mrmitew.bodylog.adapter.common.UiState
import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.UIIntent
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseMviPresenter<V : BaseView<S>, S : UiState>(
        /**
         * View interface
         */
        protected var view: V) : BasePresenter, Disposable {

    /**
     * Have the model gateways been init already
     */
    private var isInit: Boolean = false

    /**
     * Last emitted state
     */
    private var lastState: S? = null

    /**
     * Gateways from views to business logic
     */
    protected val modelGateways: CompositeDisposable = CompositeDisposable()

    /**
     * Gateways from business logic to views
     */
    protected val viewGateways: CompositeDisposable = CompositeDisposable()

    internal abstract fun createViewState(previousState: S, resultState: ResultState): S
    protected abstract fun createResultStateObservable(uiIntentStream: Observable<UIIntent>): Observable<ResultState>
    protected abstract fun viewIntents(): Observable<UIIntent>
    protected abstract val initialState: S

    override fun bindIntents() {
        check(!isDisposed, { "Presenter has already been disposed" })
        check(view !is NoOpView, { "View cannot be No-op" })
        bindInternalIntentsIfNotInit()
        viewGateways.add(bindView())
    }

    private fun bindView() = getUiStateStream()
            .subscribe({ view.render(it) }, { throw RuntimeException(it) })

    private fun bindInternalIntentsIfNotInit() {
        if (!isInit) {
            bindInternalIntents()
            isInit = true
        }
    }

    private fun getUiStateStream(): Observable<S> = reduce(viewIntents())

    override fun unbindIntents() = viewGateways.clear()

    override fun dispose() {
        modelGateways.dispose()
        viewGateways.dispose()
        lastState = null
    }

    override fun isDisposed(): Boolean = modelGateways.isDisposed || viewGateways.isDisposed

    protected open fun bindInternalIntents() {}

    private fun reduce(uiIntentStream: Observable<UIIntent>): Observable<S> =
            uiIntentStream
                    .compose<ResultState> { this.createResultStateObservable(it) }
                    .scan<S>(lastState ?: initialState, { previousState, resultState -> createViewState(previousState, resultState) })
                    .distinctUntilChanged()
                    .doOnNext { state -> lastState = state }
                    .doOnNext { println("[RENDER] $it") }


}