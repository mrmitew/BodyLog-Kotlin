package com.github.mrmitew.bodylog.adapter.weight.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.weight.intent.LogWeightIntent
import com.github.mrmitew.bodylog.domain.executor.PostExecutionThread
import com.github.mrmitew.bodylog.domain.executor.ThreadExecutor
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogWeightInteractor @Inject constructor(private val threadExecutor: ThreadExecutor,
                                              private val postExecutionThread: PostExecutionThread,
                                              private val repository: Repository) : ObservableTransformer<LogWeightIntent, LogWeightInteractor.State> {
    sealed class State : ResultState {
        class InProgress : State()
        data class Successful(val weightLog: Log.Weight) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<LogWeightIntent>): Observable<State> =
            upstream
                    .concatMap {
                        buildUseCaseObservable(it)
                                .map { State.Successful(it) as State }
                                .startWith(State.InProgress())
                    }
                    .onErrorReturn { State.Error(it) }
                    .observeOn(postExecutionThread.scheduler())

    internal fun getUseCaseObservable(weightLog: Log.Weight): Observable<Log.Weight> =
            repository.logWeight(weightLog)
                    .toObservable<Log.Weight>()
                    .concatWith(Observable.just(weightLog))

    private fun buildUseCaseObservable(intent: LogWeightIntent): Observable<Log.Weight> =
            getUseCaseObservable(intent.weightLog)
                    .subscribeOn(Schedulers.from(threadExecutor))
}