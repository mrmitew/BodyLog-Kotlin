package com.github.mrmitew.bodylog.adapter.measurement.intent

import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Log

data class LogMeasurementIntent(val measurementLog : Log.Measurement) : ViewIntent