package com.github.mrmitew.bodylog.domain.repository.entity

import com.github.mrmitew.bodylog.domain.common.Empty

open class Profile(val name: String, val description: String, val timestamp: Long = System.currentTimeMillis()) {
    class Void : Profile(name = "", description = "", timestamp = 0), Empty
    object Factory {
        val EMPTY by lazy { Void() }
    }
}