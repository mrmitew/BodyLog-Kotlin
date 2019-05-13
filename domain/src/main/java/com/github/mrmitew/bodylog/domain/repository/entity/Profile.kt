package com.github.mrmitew.bodylog.domain.repository.entity

import com.github.mrmitew.bodylog.domain.common.Empty

open class Profile(val name: String, val description: String, val timestamp: Long = System.currentTimeMillis()) {
    // FIXME: Just because I didn't want to have nullable values. Could've been done much better in a different way.
    class Void : Profile(name = "", description = "", timestamp = 0), Empty
    object Factory {
        val EMPTY by lazy { Void() }
    }
}