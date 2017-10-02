package com.github.mrmitew.bodylog.adapter.common.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.domain.executor.PostExecutionThread
import com.github.mrmitew.bodylog.domain.executor.ThreadExecutor
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

abstract class AbstractSimpleInteractor<I : ViewIntent, R>(threadExecutor: ThreadExecutor,
                                                           private val postExecutionThread: PostExecutionThread)
    : AbstractInteractor<R>(threadExecutor), ObservableTransformer<I, AbstractSimpleInteractor.State> {

    sealed class State : ResultState {
        class InProgress : State()
        data class Successful<out R>(val value: R) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<I>): Observable<State> =
            upstream
                    .concatMap { buildUseCaseObservable() }
                    .map { State.Successful(it) as State }
                    .onErrorReturn { State.Error(it) }
                    .startWith(State.InProgress())
                    .observeOn(postExecutionThread.scheduler())
}