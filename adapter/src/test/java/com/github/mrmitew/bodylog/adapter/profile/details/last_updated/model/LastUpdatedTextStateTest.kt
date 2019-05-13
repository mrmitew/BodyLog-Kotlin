package com.github.mrmitew.bodylog.adapter.profile.details.last_updated.model

import com.github.mrmitew.bodylog.adapter.common.model.Error
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LastUpdatedTextStateTest {
    @Test
    fun shouldHaveDefaultValues_WhenIdle() {
        val state = LastUpdatedTextState.Factory.idle()
        assertTrue(state.lastUpdated == LastUpdatedTextState.Factory.DEFAULT_VALUE)
        assertTrue(state.error is Error.Empty)
    }

    @Test
    fun shouldHaveError_WhenError() {
        val error = mock(Throwable::class.java)
        val state = LastUpdatedTextState.Factory.error(error)
        assertTrue(state.error == error)
    }

    @Test
    fun shouldNotHaveError_WhenSuccessful() {
        val state = LastUpdatedTextState.Factory.success(0L)
        assertTrue(state.error is Error.Empty)
    }

    @Test
    fun shouldFormatTime_WhenSuccessful() {
        val state = LastUpdatedTextState.Factory.success(0L)
        assertTrue(state.lastUpdated == "1970-01-01 01:00:00")
    }
}