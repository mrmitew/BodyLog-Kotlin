package com.github.mrmitew.bodylog.adapter.dashboard.weight.presenter

import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.dashboard.weight.intent.LoadWeightLogIntent
import com.github.mrmitew.bodylog.adapter.dashboard.weight.interactor.LoadWeightLogInteractor
import com.github.mrmitew.bodylog.adapter.dashboard.weight.view.WeightLogView
import io.reactivex.Observable
import javax.inject.Inject

class WeightLogPresenter @Inject constructor(
        // Loads a profile from the repository
        private val loadWeightLogInteractor: LoadWeightLogInteractor,
        override val initialState: WeightLogView.State = WeightLogView.State.Factory.idle(),
        override val emptyView: WeightLogView = WeightLogView.NoOp())
    : DetachableMviPresenter<WeightLogView, WeightLogView.State>(emptyView) {

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.loadWeightLogIntent().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared ->
                shared.ofType(LoadWeightLogIntent::class.java).compose(loadWeightLogInteractor).cast(ResultState::class.java)
            }

    override fun viewState(previousState: WeightLogView.State, resultState: ResultState): WeightLogView.State {
        when (resultState) {
        // TODO
        }

        throw IllegalArgumentException("Unknown partial state: $resultState")
    }
}