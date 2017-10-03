package com.github.mrmitew.bodylog.domain.repository

import com.github.mrmitew.bodylog.domain.repository.entity.Log
import com.github.mrmitew.bodylog.domain.repository.entity.Profile
import io.reactivex.Completable
import io.reactivex.Observable

interface Repository {
    fun userProfile(): Observable<Profile>
    fun userProfileRefreshing(): Observable<Profile>

    fun updateUserProfile(profile: Profile): Completable

    fun logWeight(weightLog: Log.Weight): Completable
    fun logMeasurement(bodyMeasurementLog: Log.Measurement): Completable

    fun weightLog(): Observable<List<Log.Weight>>
    fun weightLogRefreshing(): Observable<List<Log.Weight>>

    fun measurementLog(): Observable<List<Log.Measurement>>
    fun measurementLogRefreshing(): Observable<List<Log.Measurement>>
}
