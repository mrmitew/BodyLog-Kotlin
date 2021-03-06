package com.github.mrmitew.bodylog.data.repository

import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class InMemoryRepository : Repository {
    private val profileBehaviorRelay: BehaviorRelay<Profile> = BehaviorRelay.create()
    private val weightLogBehaviorRelay: BehaviorRelay<List<Log.Weight>> = BehaviorRelay.create()
    private val measurementLogBehaviorRelay: BehaviorRelay<List<Log.Measurement>> = BehaviorRelay.create()
    private var cachedProfile = Profile(name = "John Doe",
            description = "With hard work you can achieve your goals and you can become successful!")
    private val cachedWeightLogList: MutableList<Log.Weight> = arrayListOf(
            Log.Weight(70f, 10.0f, TimeUnit.MILLISECONDS.toHours(1506780731000L)),
            Log.Weight(70.5f, 10.1f, TimeUnit.MILLISECONDS.toHours(1506902400000L)),
            Log.Weight(71f, 10.0f, TimeUnit.MILLISECONDS.toHours(1506988800000L)),
            Log.Weight(71.2f, 11.0f, TimeUnit.MILLISECONDS.toHours(1507075200000L)),
            Log.Weight(71.0f, 8.0f, TimeUnit.MILLISECONDS.toHours(1507161600000L)),
            Log.Weight(71.4f, 8.0f, TimeUnit.MILLISECONDS.toHours(1509372731000L)),
            Log.Weight(71.8f, 8.0f, TimeUnit.MILLISECONDS.toHours(1507625208000L)),
            Log.Weight(72.0f, 8.0f, TimeUnit.MILLISECONDS.toHours(1507798008000L)),
            Log.Weight(72.2f, 8.0f, TimeUnit.MILLISECONDS.toHours(1507884408000L)),
            Log.Weight(72.8f, 8.0f, TimeUnit.MILLISECONDS.toHours(1507970808000L)),
            Log.Weight(73.0f, 8.0f, TimeUnit.MILLISECONDS.toHours(1508316408000L)),
            Log.Weight(73.0f, 8.0f, TimeUnit.MILLISECONDS.toHours(1508402808000L))
    )
    private val cachedMeasurementLogList: MutableList<Log.Measurement> = arrayListOf(
            Log.Measurement(backSize = 115f,
                    chestSize = 95f,
                    armsSize = 38f,
                    waistSize = 74.0f,
                    timestamp = 1508316408000L),
            Log.Measurement(backSize = 120f,
                    chestSize = 100f,
                    armsSize = 40f,
                    waistSize = 74.0f,
                    timestamp = 1508316408000L),
            Log.Measurement(backSize = 125f,
                    chestSize = 105f,
                    armsSize = 45f,
                    waistSize = 74.0f,
                    timestamp = 1507970808000L)
    )

    override fun weightLog(): Observable<List<Log.Weight>> = Observable.just(cachedWeightLogList)

    override fun measurementLog(): Observable<List<Log.Measurement>> = Observable.just(cachedMeasurementLogList)

    override fun weightLogRefreshing(): Observable<List<Log.Weight>> =
            Observable.just(cachedWeightLogList.toList())
                    .mergeWith(weightLogBehaviorRelay)
                    // Simulate a long process
                    .delay(500, TimeUnit.MILLISECONDS)

    override fun measurementLogRefreshing(): Observable<List<Log.Measurement>> =
            Observable.just(cachedMeasurementLogList.toList())
                    .mergeWith(measurementLogBehaviorRelay)
                    // Simulate a long process
                    .delay(700, TimeUnit.MILLISECONDS)

    override fun userProfile(): Observable<Profile> =
            Observable.just<Profile>(cachedProfile)
                    // Simulate a long process
                    .delay(500, TimeUnit.MILLISECONDS)

    override fun userProfileRefreshing(): Observable<Profile> =
            profileBehaviorRelay.startWith(cachedProfile)
                    // Simulate a long process
                    .delay(500, TimeUnit.MILLISECONDS)

    override fun updateUserProfile(profile: Profile): Completable =
            Completable.fromAction { cachedProfile = profile }
                    .doOnComplete { profileBehaviorRelay.accept(profile) }

    override fun logWeight(weightLog: Log.Weight): Completable =
            Completable.fromAction { cachedWeightLogList.add(weightLog) }
                    // Simulate a long process
                    .delay(2000, TimeUnit.MILLISECONDS)
                    .doOnComplete { weightLogBehaviorRelay.accept(arrayListOf(weightLog)) }

    override fun logMeasurement(bodyMeasurementLog: Log.Measurement): Completable =
            Completable.fromAction { cachedMeasurementLogList.add(bodyMeasurementLog) }
                    // Simulate a long process
                    .delay(500, TimeUnit.MILLISECONDS)
                    .doOnComplete { measurementLogBehaviorRelay.accept(arrayListOf(bodyMeasurementLog)) }
}
