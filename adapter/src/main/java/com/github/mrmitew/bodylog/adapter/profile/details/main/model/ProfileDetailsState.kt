package com.github.mrmitew.bodylog.adapter.profile.details.main.model

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.domain.repository.entity.Profile

data class ProfileDetailsState(val profile: Profile,
                               val inProgress: Boolean,
                               val loadSuccessful: Boolean,
                               val loadError: Throwable) : ViewState() {
    object Factory {
        fun inProgress(): ProfileDetailsState =
                ProfileDetailsState(profile = Profile.Factory.EMPTY,
                        inProgress = true,
                        loadSuccessful = false,
                        loadError = Error.Empty.INSTANCE)

        fun idle(): ProfileDetailsState =
                ProfileDetailsState(profile = Profile.Factory.EMPTY,
                        inProgress = false,
                        loadSuccessful = false,
                        loadError = Error.Empty.INSTANCE)
    }
}

