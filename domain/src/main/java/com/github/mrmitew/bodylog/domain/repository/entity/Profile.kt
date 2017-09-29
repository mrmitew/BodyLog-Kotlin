package com.github.mrmitew.bodylog.domain.repository.entity

import com.github.mrmitew.bodylog.domain.common.Empty

@Deprecated("Profile will contain only name and description. The rest of the properties will be represented by separate entities")
data class Profile(val name: String,
                   val description: String,
                   val weight: Float,
                   val bodyFatPercentage: Float,
                   val backSize: Float,
                   val chestSize: Float,
                   val armsSize: Float,
                   val waistSize: Float,
                   val timestamp: Long = System.currentTimeMillis(),
                   val empty: Boolean = false) {
    object Factory {
        val EMPTY = Profile(
                empty = true,
                name = "",
                description = "",
                weight = 0f,
                bodyFatPercentage = 0f,
                backSize = 0f,
                chestSize = 0f,
                armsSize = 0f,
                waistSize = 0f)
    }
}

open class UserProfile(val name: String, val description: String) {
    class Void : UserProfile(name = "", description = ""), Empty
}