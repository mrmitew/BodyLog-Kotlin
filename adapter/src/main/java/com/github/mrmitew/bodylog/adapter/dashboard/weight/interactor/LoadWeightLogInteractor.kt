package com.github.mrmitew.bodylog.adapter.dashboard.weight.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.dashboard.weight.intent.LoadWeightLogIntent
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
class LoadWeightLogInteractor @Inject constructor(private val threadExecutor: ThreadExecutor,
                                                  private val postExecutionThread: PostExecutionThread,
                                                  private val repository: Repository) : ObservableTransformer<LoadWeightLogIntent, LoadWeightLogInteractor.State> {
    sealed class State : ResultState {
        class InProgress : State()
        data class Successful(val weightLogList: List<Log.Weight>) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<LoadWeightLogIntent>): Observable<State> =
            upstream
                    .concatMap { buildUseCaseObservable() }
                    .map { State.Successful(it) as State }
                    .onErrorReturn { State.Error(it) }
                    .startWith(State.InProgress())
                    .observeOn(postExecutionThread.scheduler())

    internal fun getUseCaseObservable(): Observable<List<Log.Weight>> =
            repository.weightLog()

    private fun buildUseCaseObservable(): Observable<List<Log.Weight>> =
            getUseCaseObservable()
                    .subscribeOn(Schedulers.from(threadExecutor))
}