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
    fun updateProfile(profile: Profile): Completable
    fun logWeight(weightLog: Log.Weight): Completable
    fun logMeasurement(bodyMeasurementLog: Log.Measurement): Completable
}
