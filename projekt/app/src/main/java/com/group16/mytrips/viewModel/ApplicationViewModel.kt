package com.group16.mytrips.viewModel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.data.ModelClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private var _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    private val _searchtext = MutableStateFlow("")
    val searchtext = _searchtext.asStateFlow()

    val sightListForQuery : StateFlow<List<DefaultSight>> = searchtext
        .combine(_defaultSightList) {text, sightList ->
            if(text.isBlank()) {
                emptyList()
            } else {
                sightList.filter {
                    it.doesNatchSearchQuery(text)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            _defaultSightList.value
        )
    fun onSearchTextChange(text: String) {
        _searchtext.value = text
    }

    fun setIsSearching (boolean: Boolean) {
        _isSearching.value = boolean
    }

    private var _cameraPosition = MutableStateFlow(CameraPosition.fromLatLngZoom(LatLng(sightList.value[0].latitude,sightList.value[0].longitude), 14f))
    var cameraPosition = _cameraPosition.asStateFlow()

    private val viewModelCoroutineScope = CoroutineScope(viewModelScope.coroutineContext)
    fun moveCameraPosition (cameraPositionState: CameraPositionState, latLng: LatLng) {
        viewModelCoroutineScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        latLng,
                        cameraPositionState.position.zoom
                    )
                ), 500
            )
            _cameraPosition.value = cameraPositionState.position
        }
    }

    fun getCameraPositionState() : CameraPositionState {
        val loc = getLocationLiveData()
        return if (loc.value != null) CameraPositionState(CameraPosition(LatLng(loc.value!!.latitude, loc.value!!.longitude),14f,0f,0f))
        else CameraPositionState(CameraPosition(LatLng(defaultSightList.value[0].latitude, defaultSightList.value[0].longitude),14f,0f,0f))
    }


}