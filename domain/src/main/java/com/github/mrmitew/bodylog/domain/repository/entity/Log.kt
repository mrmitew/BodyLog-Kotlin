package com.github.mrmitew.bodylog.domain.repository.entity

sealed class Log {
    data class Weight(val weight: Float,
                      val fatPercentage: Float,
                      val timestamp: Long = System.currentTimeMillis()) : Log()

    data class Measurement(val backSize: Float,
                           val chestSize: Float,
                           val armsSize: Float,
                           val waistSize: Float,
                           val timestamp: Long = System.currentTimeMillis()) : Log()
}