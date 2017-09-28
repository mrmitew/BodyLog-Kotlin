package com.github.mrmitew.bodylog.adapter.profile_edit.main.interactor

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.profile_edit.main.intent.SaveProfileIntent
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
class SaveProfileInteractor @Inject constructor(private val threadExecutor: ThreadExecutor,
                                                private val postExecutionThread: PostExecutionThread,
                                                private val repository: Repository) : ObservableTransformer<SaveProfileIntent, SaveProfileInteractor.State> {
    sealed class State : ResultState {
        class InProgress : State()
        data class Successful(val profile: Profile) : State()
        data class Error(val error: Throwable) : State()
    }

    override fun apply(upstream: Observable<SaveProfileIntent>): Observable<State> =
            upstream
                    .concatMap { buildUseCaseObservable(it) }
                    .map { State.Successful(it) as State }
                    .onErrorReturn { State.Error(it) }
                    .startWith(State.InProgress())
                    .observeOn(postExecutionThread.getScheduler())

    internal fun getUseCaseObservable(profile: Profile): Observable<Profile> =
            repository.setProfile(profile)
                    .toObservable<Profile>()
                    .startWith(profile)

    private fun buildUseCaseObservable(intent: SaveProfileIntent): Observable<Profile> =
            getUseCaseObservable(intent.profile)
                    .subscribeOn(Schedulers.from(threadExecutor))
}