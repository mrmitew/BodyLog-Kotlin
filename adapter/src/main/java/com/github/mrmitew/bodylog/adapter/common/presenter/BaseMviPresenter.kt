package com.github.mrmitew.bodylog.adapter.common.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.adapter.common.view.BaseView
import com.github.mrmitew.bodylog.adapter.common.view.NoOpView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseMviPresenter<V : BaseView<S>, S : ViewState>(
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
    private var lastViewState: S? = null

    /**
     * Gateways from business logic to views
     */
    private val viewGateways: CompositeDisposable = CompositeDisposable()

    /**
     * Gateways from views to business logic
     */
    private val modelGateways: CompositeDisposable = CompositeDisposable()

    /**
     * Creates a new view/UI state, based on previous ui state
     * and current result state (normally, created by an interactor)
     */
    internal abstract fun viewState(previousState: S, resultState: ResultState): S

    /**
     * Transforms UI intent stream to result state stream
     */
    internal abstract fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState>

    /**
     * Represents a stream of all UI intent observables that have been merged into one
     */
    protected abstract fun viewIntentStream(): Observable<ViewIntent>

    /**
     * Represents an initial UI/view state
     */
    protected abstract val initialState: S

    /**
     * High-level function that binds View and Presenter intents with business logic.
     *
     * Presenter's intents are bound to business logic, so that it can receive updates
     * even when there is no view attached.
     *
     * View's intents are bound to presenter's internal logic, so that it can receive
     * view state updates, when the business logic changes or has changed while the View was detached.
     */
    override fun bindIntents() {
        check(!isDisposed, { "Presenter has already been disposed" })
        check(view !is NoOpView, { "View cannot be No-op" })
        bindInternalIntentsIfNotInit()
        viewGateways.add(bindViewStateWithView())
    }

    /**
     * Clears all subscriptions that refer to a view
     */
    override fun unbindIntents() = viewGateways.clear()

    /**
     * Disposes the presenter and makes it unusable
     */
    override fun dispose() {
        modelGateways.dispose()
        viewGateways.dispose()
        lastViewState = null
    }

    /**
     * Checks if presenter has already been disposed - i.e. if its still usable
     */
    override fun isDisposed(): Boolean = modelGateways.isDisposed || viewGateways.isDisposed

    /**
     * Returns an array of subscriptions/disposables that represent presenter's internal intents
     */
    protected open fun internalIntents(): Array<Disposable> = emptyArray()

    /**
     * Mechanism that allows presenter to bind its internal intents (if there are any)
     * to business logic only once when view binds for very first time
     */
    private fun bindInternalIntentsIfNotInit() {
        if (!isInit) {
            bindInternalIntents()
            isInit = true
        }
    }

    /**
     * Function meant for the presenter to bind its internal intents (if there are any)
     * to business logic
     */
    private fun bindInternalIntents() {
        modelGateways.addAll(*internalIntents())
    }

    /**
     * Binds view state stream to the attached view
     */
    private fun bindViewStateWithView() = viewStateStream()
            .doOnNext { println("[RENDER] $it") }
            .subscribe({ view.render(it) }, { throw RuntimeException(it) })

    /**
     * Creates a stream of "ViewState"s from the stream of view intents and their corresponding result states
     */
    private fun viewStateStream(): Observable<S> = reduce(viewIntentStream())

    /**
     * Creates a stream of view states that have been calculated using a state reducer.
     * New emissions are distinct from their immediate predecessors,
     * so that identical states will not be emitted in a row
     */
    private fun reduce(viewIntentStream: Observable<ViewIntent>): Observable<S> =
            viewIntentStream
                    // Transform the current view intent to a ResultState - normally using an Interactor
                    .compose<ResultState> { resultStateStream(it) }
                    // Reduce the previous state i.e. create a new state, based on previous state and currently emitted business logic result state
                    .scan<S>(lastViewState ?: initialState, { previousState, resultState -> viewState(previousState, resultState) })
                    // Filter duplicates
                    .distinctUntilChanged()
                    // Update last view state with current one
                    .doOnNext { lastViewState = it }
}