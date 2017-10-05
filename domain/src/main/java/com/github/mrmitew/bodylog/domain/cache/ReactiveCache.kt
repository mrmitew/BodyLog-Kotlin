package com.github.mrmitew.bodylog.domain.cache

import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.observables.ConnectableObservable
import java.util.*

class ReactiveCache<in K, V>(private val generator: Function<K, Observable<V>>) {
    private val requests: MutableMap<K, ConnectableObservable<V>> = HashMap()

    @Throws(Exception::class)
    operator fun get(key: K): Observable<V> {
        var result: ConnectableObservable<V> = Observable.empty<V>().replay()
        synchronized(requests) {
            val current = requests[key]
            if (current != null) {
                return current
            }

            result = generator.apply(key)
                    .doOnTerminate {
                        synchronized(requests) {
                            requests.remove(key)
                        }
                    }
                    .replay()

            requests.put(key, result)
        }
        return result.autoConnect(0)
    }

    fun clear() {
        synchronized(requests) {
            requests.clear()
        }
    }

    fun clear(key: K) {
        synchronized(requests) {
            requests.remove(key)
        }
    }
}