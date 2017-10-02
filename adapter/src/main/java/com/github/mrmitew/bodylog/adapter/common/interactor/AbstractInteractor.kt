package com.github.mrmitew.bodylog.adapter.common.interactor

import com.github.mrmitew.bodylog.domain.executor.ThreadExecutor
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

abstract class AbstractInteractor<R>(private val threadExecutor: ThreadExecutor) {
    abstract internal fun getUseCaseObservable(): Observable<R>

    protected fun buildUseCaseObservable(): Observable<R> =
            getUseCaseObservable()
                    .subscribeOn(Schedulers.from(threadExecutor))
}