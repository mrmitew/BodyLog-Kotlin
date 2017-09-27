package com.github.mrmitew.bodylog.adapter.profile_edit.main.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.StateError
import com.github.mrmitew.bodylog.adapter.common.model.UIIntent
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
import javax.inject.Inject

class ProfileEditPresenter
@Inject constructor(private val loadProfileInteractor: LoadProfileInteractor,
                    private val saveProfileInteractor: SaveProfileInteractor,
                    private val checkRequiredFieldsInteractor: CheckRequiredFieldsInteractor,
                    private val profileResultStateRelay: BehaviorRelay<ResultState>)
    : DetachableMviPresenter<ProfileEditView, ProfileEditState>(emptyView) {

    companion object {
        val emptyView = ProfileEditView.NoOp()
    }

    override fun getEmptyView(): ProfileEditView = emptyView

    override fun viewIntents(): Observable<UIIntent> = Observable.merge(view.getRequiredFieldsFilledInIntent(),
            view.getSaveIntent(), view.getLoadProfileIntent())

    override fun initialState(): ProfileEditState = ProfileEditState.Factory.idle()

    override fun bindInternalIntents() {
        super.bindInternalIntents()
        modelGateways.addAll(Observable.just(LoadProfileIntent())
                .compose(loadProfileInteractor)
                .doOnNext { println("[EDIT] [PROFILE MODEL] (${it.hashCode()} : $it") }
                .subscribe(profileResultStateRelay))
    }

    override fun createResultStateObservable(uiIntentStream: Observable<UIIntent>): Observable<ResultState> =
            uiIntentStream.publish { shared ->
                Observable.merge(shared.ofType(LoadProfileIntent::class.java).flatMap { profileResultStateRelay },
                        shared.ofType(CheckRequiredFieldsIntent::class.java).compose(checkRequiredFieldsInteractor),
                        shared.ofType(SaveProfileIntent::class.java).compose(saveProfileInteractor))
            }

    override fun createViewState(previousState: ProfileEditState, resultState: ResultState): ProfileEditState {
        when (resultState) {
            is LoadProfileInteractor.State ->
                return when (resultState) {
                    is LoadProfileInteractor.State.InProgress -> previousState.copy(
                            isInProgress = true,
                            isLoadSuccessful = false,
                            loadError = StateError.Empty.INSTANCE)

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
                            saveError = StateError.Empty.INSTANCE)
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
                            requiredFieldsError = StateError.Empty.INSTANCE)
                    is CheckRequiredFieldsInteractor.State.Error -> previousState.copy(
                            requiredFieldsFilledIn = false,
                            requiredFieldsError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown partial state: $resultState")
    }
}