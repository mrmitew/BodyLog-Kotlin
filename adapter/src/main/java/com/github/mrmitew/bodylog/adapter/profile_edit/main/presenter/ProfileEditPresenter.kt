package com.github.mrmitew.bodylog.adapter.profile_edit.main.presenter

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.CheckRequiredFieldsInteractor
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.LoadProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile_edit.main.intent.CheckRequiredFieldsIntent
import com.github.mrmitew.bodylog.adapter.profile_edit.main.intent.SaveProfileIntent
import com.github.mrmitew.bodylog.adapter.profile_edit.main.interactor.SaveProfileInteractor
import com.github.mrmitew.bodylog.adapter.profile_edit.main.model.ProfileEditState
import com.github.mrmitew.bodylog.adapter.profile_edit.main.view.ProfileEditView
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ProfileEditPresenter
@Inject constructor(private val loadProfileInteractor: LoadProfileInteractor,
                    private val saveProfileInteractor: SaveProfileInteractor,
                    private val checkRequiredFieldsInteractor: CheckRequiredFieldsInteractor,
                    private val profileResultStateRelay: BehaviorRelay<ResultState>,
                    override val initialState: ProfileEditState,
                    override val emptyView: ProfileEditView = ProfileEditView.NoOp())
    : DetachableMviPresenter<ProfileEditView, ProfileEditState>(emptyView) {

    override fun internalIntents(): Array<Disposable> =
            arrayOf(Observable.just(LoadProfileIntent())
                    .compose(loadProfileInteractor)
                    .doOnNext { println("[EDIT] [PROFILE MODEL] (${it.hashCode()} : $it") }
                    .subscribe(profileResultStateRelay))

    override fun viewIntentStream(): Observable<ViewIntent> = Observable.merge(view.requiredFieldsFilledInIntent(),
            view.saveIntent(), view.loadProfileIntent())

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared ->
                Observable.merge(shared.ofType(LoadProfileIntent::class.java).flatMap { profileResultStateRelay },
                        shared.ofType(CheckRequiredFieldsIntent::class.java).compose(checkRequiredFieldsInteractor),
                        shared.ofType(SaveProfileIntent::class.java).compose(saveProfileInteractor))
            }

    override fun viewState(previousState: ProfileEditState, resultState: ResultState): ProfileEditState {
        when (resultState) {
            is LoadProfileInteractor.State ->
                return when (resultState) {
                    is LoadProfileInteractor.State.InProgress -> previousState.copy(
                            isInProgress = true,
                            isLoadSuccessful = false,
                            loadError = Error.Empty.INSTANCE)

                    is LoadProfileInteractor.State.Successful -> previousState.copy(
                            profile = resultState.profile,
                            isInProgress = false,
                            isLoadSuccessful = true)

                    is LoadProfileInteractor.State.Error -> previousState.copy(
                            isInProgress = false,
                            isLoadSuccessful = false,
                            loadError = resultState.error)
                }
            is SaveProfileInteractor.State ->
                return when (resultState) {
                    is SaveProfileInteractor.State.InProgress -> previousState.copy(
                            isInProgress = true,
                            isSaveSuccessful = false,
                            saveError = Error.Empty.INSTANCE)
                    is SaveProfileInteractor.State.Successful -> previousState.copy(
                            isInProgress = false,
                            isSaveSuccessful = true,
                            profile = resultState.profile)
                    is SaveProfileInteractor.State.Error -> previousState.copy(
                            isInProgress = false,
                            isSaveSuccessful = false,
                            saveError = resultState.error)
                }
            is CheckRequiredFieldsInteractor.State ->
                return when (resultState) {
                    is CheckRequiredFieldsInteractor.State.Successful -> previousState.copy(
                            requiredFieldsFilledIn = true,
                            requiredFieldsError = Error.Empty.INSTANCE)
                    is CheckRequiredFieldsInteractor.State.Error -> previousState.copy(
                            requiredFieldsFilledIn = false,
                            requiredFieldsError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown partial state: $resultState")
    }
}