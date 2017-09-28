package com.github.mrmitew.bodylog.data.repository

import com.github.mrmitew.bodylog.domain.repository.Repository
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ProfileRepository : Repository {
    private val profileBehaviorRelay: BehaviorRelay<Profile> = BehaviorRelay.create()
    private var cachedProfile = Profile(
            name = "John Doe",
            description = "With hard work you can achieve your goals and you can become successful!",
            weight = 70f,
            bodyFatPercentage = 8f,
            backSize = 120f,
            chestSize = 100f,
            armsSize = 40f,
            waistSize = 74.5f)

    override fun getProfile(): Observable<Profile> =
            Observable.just<Profile>(cachedProfile)
                    // Simulate a long process
                    .delay(1500, TimeUnit.MILLISECONDS)

    override fun getProfileRefreshing(): Observable<Profile> =
            profileBehaviorRelay.startWith(cachedProfile)
                    // Simulate a long process
                    .delay(1500, TimeUnit.MILLISECONDS)

    override fun setProfile(profile: Profile): Completable =
            Completable.fromAction { cachedProfile = profile }
                    .doOnComplete { profileBehaviorRelay.accept(profile) }
}
