package com.github.mrmitew.bodylog.adapter.dashboard.measurement.interactor

import com.github.mrmitew.bodylog.adapter.common.interactor.AbstractInteractor
import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.dashboard.measurement.intent.LoadMeasurementLogIntent
import com.github.mrmitew.bodylog.domain.executor.PostExecutionThread
import com.github.mrmitew.bodylog.domain.executor.ThreadExecutor
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadMeasurementLogInteractor @Inject constructor(threadExecutor: ThreadExecutor,
                                                       private val postExecutionThread: PostExecutionThread,
                                                       private val repository: Repository)
    : AbstractInteractor<List<Log.Measurement>>(threadExecutor), ObservableTransformer<LoadMeasurementLogIntent, LoadMeasurementLogInteractor.State> {
    sealed class State : ResultState {
        class InProgress : State()
        data class Successful(val measurementLogList: List<Log.Measurement>) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<LoadMeasurementLogIntent>): Observable<State> =
            upstream
                    .concatMap { buildUseCaseObservable() }
                    .map { State.Successful(it) as State }
                    .onErrorReturn { State.Error(it) }
                    .startWith(State.InProgress())
                    .observeOn(postExecutionThread.scheduler())

    override fun getUseCaseObservable(): Observable<List<Log.Measurement>> =
            repository.measurementLogRefreshing()
}