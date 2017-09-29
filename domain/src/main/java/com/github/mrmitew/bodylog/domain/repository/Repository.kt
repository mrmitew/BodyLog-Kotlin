package com.github.mrmitew.bodylog.domain.repository

import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import com.github.mrmitew.bodylog.domain.repository.entity.UserProfile

import io.reactivex.Completable
import io.reactivex.Observable

interface Repository {
    @Deprecated("Use userProfile()")
    fun getProfile(): Observable<Profile>

    @Deprecated("Use userProfileRefreshing()")
    fun getProfileRefreshing(): Observable<Profile>

    fun userProfile(): Observable<UserProfile>
    fun userProfileRefreshing(): Observable<UserProfile>

    @Deprecated("Use updateUserProfile()")
    fun updateProfile(profile: Profile): Completable

    fun updateUserProfile(userProfile: UserProfile): Completable

    fun logWeight(weightLog: Log.Weight): Completable
    fun logMeasurement(bodyMeasurementLog: Log.Measurement): Completable

    fun weightLog(): Observable<List<Log.Weight>>
    fun weightLogRefreshing(): Observable<List<Log.Weight>>

    fun measurementLog(): Observable<List<Log.Measurement>>
    fun measurementLogRefreshing(): Observable<List<Log.Measurement>>
}
