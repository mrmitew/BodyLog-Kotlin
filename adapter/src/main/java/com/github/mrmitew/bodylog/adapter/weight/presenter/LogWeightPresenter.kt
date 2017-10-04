package com.github.mrmitew.bodylog.adapter.weight.presenter

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ResultState
import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.adapter.common.presenter.DetachableMviPresenter
import com.github.mrmitew.bodylog.adapter.weight.intent.LogWeightIntent
import com.github.mrmitew.bodylog.adapter.weight.interactor.LogWeightInteractor
import com.github.mrmitew.bodylog.adapter.weight.view.LogWeightView
import io.reactivex.Observable
import javax.inject.Inject

class LogWeightPresenter @Inject constructor(
        private val logWeightInteractor: LogWeightInteractor,
        override val initialState: LogWeightView.State = LogWeightView.State.Factory.default(),
        override val emptyView: LogWeightView = LogWeightView.NoOp())
    : DetachableMviPresenter<LogWeightView, LogWeightView.State>(emptyView) {

    override fun viewIntentStream(): Observable<ViewIntent> =
            view.logWeightIntent().cast(ViewIntent::class.java)

    override fun resultStateStream(viewIntentStream: Observable<ViewIntent>): Observable<ResultState> =
            viewIntentStream.publish { shared ->
                shared.ofType(LogWeightIntent::class.java).compose(logWeightInteractor).cast(ResultState::class.java)
            }

    override fun viewState(previousState: LogWeightView.State, resultState: ResultState): LogWeightView.State {
        when (resultState) {
            is LogWeightInteractor.State ->
                return when (resultState) {
                    is LogWeightInteractor.State.InProgress -> previousState.copy(inProgress = true)
                    is LogWeightInteractor.State.Successful -> previousState.copy(inProgress = false, weightLog = resultState.weightLog, saveError = Error.Empty.INSTANCE)
                    is LogWeightInteractor.State.Error -> previousState.copy(inProgress = false, saveError = resultState.error)
                }
        }

        throw IllegalArgumentException("Unknown result state: $resultState")
    }
}