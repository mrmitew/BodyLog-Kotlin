package com.github.mrmitew.bodylog.domain.executor

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestPostExecutionThread : PostExecutionThread {
    override fun getScheduler(): Scheduler = Schedulers.trampoline()
}