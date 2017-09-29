package com.github.mrmitew.bodylog.adapter.profile.edit.model

import com.github.mrmitew.bodylog.adapter.common.model.Error
import com.github.mrmitew.bodylog.adapter.common.model.ViewState
import com.github.mrmitew.bodylog.domain.repository.entity.Profile

data class ProfileEditState(
        val isInProgress: Boolean,
        val isLoadSuccessful: Boolean,
        val loadError: Throwable,
        val isSaveSuccessful: Boolean,
        val saveError: Throwable,
        val requiredFieldsFilledIn: Boolean,
        val requiredFieldsError: Throwable,
        val profile: Profile) : ViewState() {

    object Factory {
        fun idle(): ProfileEditState = ProfileEditState(
                isInProgress = false,
                isLoadSuccessful = false,
                profile = Profile.Factory.EMPTY,
                loadError = Error.Empty.INSTANCE,
                requiredFieldsFilledIn = false,
                requiredFieldsError = Error.Empty.INSTANCE,
                isSaveSuccessful = false,
                saveError = Error.Empty.INSTANCE)


        fun empty(): ProfileEditState = ProfileEditState(
                isInProgress = false,
                isLoadSuccessful = false,
                profile = Profile.Factory.EMPTY,
                loadError = Throwable("Empty result"),
                requiredFieldsFilledIn = false,
                requiredFieldsError = Error.Empty.INSTANCE,
                isSaveSuccessful = false,
                saveError = Error.Empty.INSTANCE)


        fun isInProgress(): ProfileEditState = ProfileEditState(
                isInProgress = true,
                isLoadSuccessful = false,
                profile = Profile.Factory.EMPTY,
                loadError = Error.Empty.INSTANCE,
                requiredFieldsFilledIn = false,
                requiredFieldsError = Error.Empty.INSTANCE,
                isSaveSuccessful = false,
                saveError = Error.Empty.INSTANCE)


        fun successful(profile: Profile): ProfileEditState = ProfileEditState(
                isInProgress = false,
                isLoadSuccessful = true,
                profile = profile,
                loadError = Error.Empty.INSTANCE,
                requiredFieldsFilledIn = false,
                requiredFieldsError = Error.Empty.INSTANCE,
                isSaveSuccessful = false,
                saveError = Error.Empty.INSTANCE)


        fun error(throwable: Throwable): ProfileEditState = ProfileEditState(
                isInProgress = false,
                isLoadSuccessful = false,
                profile = Profile.Factory.EMPTY,
                loadError = throwable,
                requiredFieldsFilledIn = false,
                requiredFieldsError = Error.Empty.INSTANCE,
                isSaveSuccessful = false,
                saveError = Error.Empty.INSTANCE)
    }
}