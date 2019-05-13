package com.github.mrmitew.bodylog.adapter.common.model

sealed class Error : Throwable() {
    // Just because I didn't want to have a null value..oh..
    class Empty private constructor() : Error() {
        override fun toString() = "Empty{}"

        companion object {
            val INSTANCE by lazy { Empty() }
        }
    }

    override fun toString() = "Error{}"
}