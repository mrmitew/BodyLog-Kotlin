package com.github.mrmitew.bodylog.data.repository

import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.github.mrmitew.bodylog.domain.repository.entity.UserProfile
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class InMemoryRepository : Repository {
    // TODO: Remove
    @Deprecated("Use userProfileBehaviorRelay")
    private val profileBehaviorRelay: BehaviorRelay<Profile> = BehaviorRelay.create()
    // TODO: Remove
    @Deprecated("Use cachedUserProfile")
    private var cachedProfile = Profile(
            name = "John Doe",
            description = "With hard work you can achieve your goals and you can become successful!",
            weight = 70f,
            bodyFatPercentage = 8f,
            backSize = 120f,
            chestSize = 100f,
            armsSize = 40f,
            waistSize = 74.5f)

    private val userProfileBehaviorRelay: BehaviorRelay<UserProfile> = BehaviorRelay.create()
    private val weightLogBehaviorRelay: BehaviorRelay<Log.Weight> = BehaviorRelay.create()
    private val measurementLogBehaviorRelay: BehaviorRelay<Log.Measurement> = BehaviorRelay.create()

    private var cachedUserProfile = UserProfile(name = "John Doe",
            description = "With hard work you can achieve your goals and you can become successful!")

    private val cachedWeightLogList = arrayListOf<Log.Weight>(
            Log.Weight(60f, 10.0f, 0),
            Log.Weight(62f, 10.1f, 1),
            Log.Weight(65f, 10.0f, 10),
            Log.Weight(70f, 11.0f, 20),
            Log.Weight(68f, 8.0f, 30)
    )

    private val cachedMeasurementLogList = arrayListOf<Log.Measurement>(
            Log.Measurement(backSize = 120f,
                    chestSize = 100f,
                    armsSize = 40f,
                    waistSize = 74.5f,
                    timestamp = 30)
    )
    // TODO: Remove
    override fun getProfile(): Observable<Profile> =
            Observable.just<Profile>(cachedProfile)
                    // Simulate a long process
                    .delay(1500, TimeUnit.MILLISECONDS)

    // TODO: Remove
    override fun getProfileRefreshing(): Observable<Profile> =
            profileBehaviorRelay.startWith(cachedProfile)
                    // Simulate a long process
                    .delay(1500, TimeUnit.MILLISECONDS)

    override fun userProfile(): Observable<UserProfile> =
            Observable.just<UserProfile>(cachedUserProfile)
                    // Simulate a long process
                    .delay(1500, TimeUnit.MILLISECONDS)

    override fun userProfileRefreshing(): Observable<UserProfile> =
            userProfileBehaviorRelay.startWith(cachedUserProfile)
                    // Simulate a long process
                    .delay(1500, TimeUnit.MILLISECONDS)

    override fun updateProfile(profile: Profile): Completable =
            Completable.fromAction { cachedProfile = profile }
                    .doOnComplete { profileBehaviorRelay.accept(profile) }

    override fun logWeight(weightLog: Log.Weight): Completable =
            Completable.fromAction { cachedWeightLogList.add(weightLog) }
                    .doOnComplete { weightLogBehaviorRelay.accept(weightLog) }

    override fun logMeasurement(bodyMeasurementLog: Log.Measurement): Completable =
            Completable.fromAction { cachedMeasurementLogList.add(bodyMeasurementLog) }
                    .doOnComplete { measurementLogBehaviorRelay.accept(bodyMeasurementLog) }
}
