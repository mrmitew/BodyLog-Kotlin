package com.github.mrmitew.bodylog.adapter.profile_details.last_updated.model

import com.github.mrmitew.bodylog.adapter.common.ViewState
import com.github.mrmitew.bodylog.adapter.common.model.StateError
import java.text.SimpleDateFormat
import java.util.*

data class LastUpdatedTextState(val lastUpdated: String,
                                val error: Throwable) : ViewState() {
    object Factory {
        const val DEFAULT_VALUE = "n/a"
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)

        fun idle(): LastUpdatedTextState {
            return LastUpdatedTextState(lastUpdated = DEFAULT_VALUE,
                    error = StateError.Empty.INSTANCE)
        }

        fun success(time: Long): LastUpdatedTextState {
            return LastUpdatedTextState(lastUpdated = DATE_FORMAT.format(time),
                    error = StateError.Empty.INSTANCE)
        }

        fun error(error: Throwable): LastUpdatedTextState {
            return LastUpdatedTextState(lastUpdated = DEFAULT_VALUE,
                    error = error)
        }
    }
}