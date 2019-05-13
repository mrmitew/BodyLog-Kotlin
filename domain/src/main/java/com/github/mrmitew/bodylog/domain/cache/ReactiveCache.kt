package com.github.mrmitew.bodylog.domain.cache

import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.observables.ConnectableObservable
import java.util.*

/**
 * Caches observables that haven't terminated (completed or errored) yet, so that
 * multiple callers can hook into existing streams inflight.
 *
 * For instance, if at the same time two Views try to load user profile to display a
 * subset of the information, they would normally trigger the business logic twice
 * leading to making a disk/network operation twice. Having a in-flight cache mechanism
 * would prevent that. Second caller will get a cached observable and ultimately
 * receive user profile when it is ready.
 *
 * Note that cache is cleared for a given key as soon as result is ready, so that
 * for a potential subsequent request, a new observable will be created.
 */
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