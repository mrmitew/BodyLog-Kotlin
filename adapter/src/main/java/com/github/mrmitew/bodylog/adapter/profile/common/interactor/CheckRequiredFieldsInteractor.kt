package com.github.mrmitew.bodylog.adapter.profile.common.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.profile.edit.intent.CheckRequiredFieldsIntent
import com.github.mrmitew.bodylog.domain.executor.PostExecutionThread
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CheckRequiredFieldsInteractor @Inject constructor(private val postExecutionThread: PostExecutionThread)
    : ObservableTransformer<CheckRequiredFieldsIntent, CheckRequiredFieldsInteractor.State> {
    sealed class Error : Throwable() {
        class FieldsNotFilledInThrowable : Error() {
            override fun toString() = "FieldsNotFilledInThrowable{}"
        }
    }

    sealed class State : ResultState {
        class Successful : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<CheckRequiredFieldsIntent>): Observable<State> =
            upstream
                    .concatMap { getUseCaseObservable(it) }
                    .map { State.Successful() as State }
                    .onErrorReturn({ State.Error(it) })
                    .observeOn(postExecutionThread.scheduler())

    internal fun getUseCaseObservable(intent: CheckRequiredFieldsIntent): Observable<State> =
            Observable.just(when (intent.isFilledIn) {
                true -> State.Successful()
                false -> State.Error(Error.FieldsNotFilledInThrowable())
            })
}