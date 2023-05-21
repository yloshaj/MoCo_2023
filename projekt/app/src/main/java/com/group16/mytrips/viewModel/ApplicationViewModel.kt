package com.group16.mytrips.viewModel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.data.ModelClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlin.math.roundToInt

class ApplicationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationLiveData = LocationLiveData(application)
    private val modelClass = ModelClass()

    private var _defaultSightList = MutableStateFlow(modelClass.defaultSightList)
    val defaultSightList = _defaultSightList.asStateFlow()

    private var _sightList = MutableStateFlow(modelClass.sightList)
    val sightList = _sightList.asStateFlow()

    private var _avatar = MutableStateFlow(modelClass.listOfAvatars)
    val avatar = _avatar.asStateFlow()

    private var _xp = MutableStateFlow(230)
    val xp = _xp.asStateFlow()

    fun addXP (sightXP: Int) {
        _xp.value += sightXP
    }
    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

    private fun distanceInMeter(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Int {
        var results = FloatArray(1)
        Location.distanceBetween(startLat, startLon, endLat, endLon, results)
        return results[0].roundToInt()
    }

    fun getSortedList(): StateFlow<MutableList<DefaultSight>> {
        val observedLocationData = getLocationLiveData()
        val list = _defaultSightList.asStateFlow()
        list.value.forEach {
            val l = observedLocationData
            val location = getLocationLiveData().value
            if (location == null) it.distance = null
            else
                it.distance = distanceInMeter(
                    location.latitude,
                    location.longitude,
                    it.latitude,
                    it.longitude
                )
        }
        //list.value.sortedByDescending { it.distance }
        list.value.sortBy { it.distance }
        return list
    }




}