package com.github.mrmitew.bodylog.adapter.common.interactor

import com.github.mrmitew.bodylog.domain.cache.ReactiveCache
import com.github.mrmitew.bodylog.domain.executor.ThreadExecutor
import io.reactivex.functions.Function

abstract class AbstractRequestCachableInteractor<in K, R>(threadExecutor: ThreadExecutor)
    : AbstractInteractor<R>(threadExecutor) {

    private val inflightRequests: ReactiveCache<K, R> =
            ReactiveCache(Function { buildUseCaseObservable() })

    fun cachedUseCaseObservable(key: K) = inflightRequests[key]
}