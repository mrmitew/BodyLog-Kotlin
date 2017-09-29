package com.github.mrmitew.bodylog.domain.repository.entity

sealed class Log(val timestamp: Long) {
    class Weight(val weight: Float,
                 val fatPercentage: Float,
                 timestamp: Long = System.currentTimeMillis()) : Log(timestamp)

    class Measurement(val backSize: Float,
                      val chestSize: Float,
                      val armsSize: Float,
                      val waistSize: Float,
                      timestamp: Long = System.currentTimeMillis()) : Log(timestamp)
}