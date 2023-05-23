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
import kotlin.math.roundToInt

class CameraViewModel(application: Application): AndroidViewModel(application) {
    private val diameter = 50

    private val locationLiveData = LocationLiveData(application)
    private val modelClass = ModelClass()

    private var _defaultSightList = MutableStateFlow(modelClass.defaultSightList)
    val defaultSightList = _defaultSightList.asStateFlow()

    fun getLocationLiveData() = locationLiveData

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
        list.value.filter { if (it.distance != null) it.distance!! <= diameter else false }
        return list
    }



}