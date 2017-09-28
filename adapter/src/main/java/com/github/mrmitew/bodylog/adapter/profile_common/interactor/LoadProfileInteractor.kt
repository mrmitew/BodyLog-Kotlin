package com.github.mrmitew.bodylog.adapter.profile_common.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.domain.executor.PostExecutionThread
import com.github.mrmitew.bodylog.domain.executor.ThreadExecutor
import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadProfileInteractor @Inject constructor(private val threadExecutor: ThreadExecutor,
                                                private val postExecutionThread: PostExecutionThread,
                                                private val repository: Repository) : ObservableTransformer<LoadProfileIntent, LoadProfileInteractor.State> {
    sealed class State : ResultState {
        class InProgress : State()
        data class Successful(val profile: Profile) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<LoadProfileIntent>): Observable<State> =
            upstream
                    .concatMap { buildUseCaseObservable() }
                    .map { State.Successful(it) as State }
                    .onErrorReturn { State.Error(it) }
                    .startWith(State.InProgress())
                    .observeOn(postExecutionThread.scheduler())

    fun getUseCaseObservable(): Observable<Profile> =
            repository.getProfileRefreshing()

    private fun buildUseCaseObservable(): Observable<Profile> =
            getUseCaseObservable()
                    .subscribeOn(Schedulers.from(threadExecutor))
}