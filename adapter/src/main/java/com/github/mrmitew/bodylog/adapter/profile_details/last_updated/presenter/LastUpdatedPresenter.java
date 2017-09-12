package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.presenter;

import com.github.mrmitew.bodylog.adapter.common.model.ResultState;
import com.github.mrmitew.bodylog.adapter.common.model.StateError;
import com.github.mrmitew.bodylog.adapter.common.model.UIIntent;
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter;
import com.github.mrmitew.bodylog.adapter.profile_common.intent.LoadProfileIntent;
import com.github.mrmitew.bodylog.adapter.profile_common.interactor.LoadProfileInteractor;
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.intent.GetProfileLastUpdatedIntent;
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model.LastUpdatedTextState;
import com.github.mrmitew.bodylog.adapter.profile_details.last_updated.view.LastUpdatedView;
import com.jakewharton.rxrelay2.BehaviorRelay;

import javax.inject.Inject;

import io.reactivex.Observable;

public class LastUpdatedPresenter extends DetachableMviPresenter<LastUpdatedView, LastUpdatedTextState> {
    //
    // Interactors
    //

    /**
     * Loads a profile from the repository
     */
    private LoadProfileInteractor mLoadProfileInteractor;

    //
    // State relays
    //

    /*
     * State relays are subscribed to the business logic (model) and will cache (and perhaps* emit) the latest changes in the
     * business logic.
     *
     * * If the View is not attached, the relays will keep a cached state of a particular result, which
     * will be emitted as soon as the View attaches once again.
     */

    /**
     * Profile state relay
     */
    private final BehaviorRelay<ResultState> mProfileResultStateRelay;

    @Inject
    public LastUpdatedPresenter(final LoadProfileInteractor loadProfileInteractor,
                                final BehaviorRelay<ResultState> profileResultStateRelay) {
        super(new LastUpdatedView.Empty());
        mLoadProfileInteractor = loadProfileInteractor;
        mProfileResultStateRelay = profileResultStateRelay;
    }

    @Override
    protected void bindInternalIntents() {
        super.bindInternalIntents();
        getModelGateways().add(Observable.just(new LoadProfileIntent())
                .compose(mLoadProfileInteractor)
                .doOnNext(state -> System.out.println(String.format("[LAST_UPDATED] [PROFILE MODEL] (%s) : %s", state.hashCode(), state)))
                .subscribe(mProfileResultStateRelay));
    }

    @Override
    protected Observable<ResultState> createResultStateObservable(final Observable<UIIntent> uiIntentObservable) {
        return uiIntentObservable
                .publish(shared -> shared.ofType(GetProfileLastUpdatedIntent.class).flatMap(__ -> mProfileResultStateRelay));
    }

    @Override
    protected LastUpdatedTextState createViewState(final LastUpdatedTextState previousState, final ResultState resultState) {
        if (resultState instanceof LoadProfileInteractor.State) {
            if (resultState.isInProgress()) {
                // We are not going to indicate we are currently fetching new data,
                // nor will replace the old one with something, but we'll just clear the error field (if there was one)
                if (previousState.error() != StateError.Empty.Companion.getINSTANCE()) {
                    return previousState.toBuilder()
                            .error(StateError.Empty.Companion.getINSTANCE())
                            .build();
                }
                // No error? Then, just emit the old state. No view state changes needed to be done here.
                return previousState;
            } else if (resultState.isSuccessful()) {
                return previousState.toBuilder()
                        .lastUpdated(LastUpdatedTextState.DATE_FORMAT.format(((LoadProfileInteractor.State) resultState).getProfile().timestamp()))
                        .error(StateError.Empty.Companion.getINSTANCE())
                        .build();
            } else if (!(resultState.getError() instanceof StateError.Empty)) {
                return previousState.toBuilder()
                        .lastUpdated(LastUpdatedTextState.EMPTY)
                        .error(resultState.getError())
                        .build();
            }
        }

        throw new IllegalArgumentException("Unknown partial state: " + resultState);
    }

    @Override
    protected LastUpdatedTextState initialState() {
        return LastUpdatedTextState.Factory.idle();
    }

    @Override
    protected Observable<UIIntent> viewIntents() {
        return getView().getProfileLastUpdatedIntent()
                .cast(UIIntent.class);
    }
}
