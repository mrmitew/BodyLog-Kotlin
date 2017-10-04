package com.github.mrmitew.bodylog.adapter.measurement.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.measurement.intent.LogMeasurementIntent
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
class LogMeasurementInteractor @Inject constructor(private val threadExecutor: ThreadExecutor,
                                                   private val postExecutionThread: PostExecutionThread,
                                                   private val repository: Repository) :
        ObservableTransformer<LogMeasurementIntent, LogMeasurementInteractor.State> {
    sealed class State : ResultState {
        class InProgress : State()
        data class Successful(val measurementLog: Log.Measurement) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<LogMeasurementIntent>): Observable<State> =
            upstream
                    .concatMap { buildUseCaseObservable(it) }
                    .map { State.Successful(it) as State }
                    .onErrorReturn { State.Error(it) }
                    .startWith(State.InProgress())
                    .observeOn(postExecutionThread.scheduler())

    internal fun getUseCaseObservable(measurementLog: Log.Measurement): Observable<Log.Measurement> =
            repository.logMeasurement(measurementLog)
                    .toObservable<Log.Measurement>()
                    .startWith(measurementLog)

    private fun buildUseCaseObservable(intent: LogMeasurementIntent): Observable<Log.Measurement> =
            getUseCaseObservable(intent.measurementLog)
                    .subscribeOn(Schedulers.from(threadExecutor))
}