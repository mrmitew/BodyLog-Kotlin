package com.github.mrmitew.bodylog.adapter.weight.intent

import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Log

data class LogWeightIntent(val weightLog : Log.Weight) : ViewIntent