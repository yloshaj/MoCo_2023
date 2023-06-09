package com.group16.mytrips.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.data.Firebase
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.misc.distanceInMeter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData

    private val _defaultSightList = MutableStateFlow(emptyList<DefaultSightFB>().toMutableList())
    val defaultSightList = _defaultSightList.asStateFlow()


    private val _searchtext = MutableStateFlow("")
    val searchtext = _searchtext.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchtext.value = text
    }

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun setIsSearching(boolean: Boolean) {
        _isSearching.value = boolean
    }

    private val _radius = MutableStateFlow(0)
    val radius = _radius.asStateFlow()


    fun startListeningForDeFaultSightList() {
        Firebase.startListeningForDefaultSightList { defaultSights ->
            viewModelScope.launch(Dispatchers.IO) {
                _defaultSightList.value = defaultSights as MutableList<DefaultSightFB>
            }
        }
    }

    fun startListeningForRadius() {
        Firebase.startListeningForRadius { radius ->
            viewModelScope.launch(Dispatchers.IO) {
                _radius.value = radius
            }
        }
    }

    fun startListeningForData() {
        startListeningForDeFaultSightList()
        startListeningForRadius()
    }

    fun getSortedList(): StateFlow<MutableList<DefaultSightFB>> {
        val list = _defaultSightList.asStateFlow()
        val location = getLocationLiveData().value
        list.value.forEach {
            if (location == null) it.distance = null
            else
                it.distance = distanceInMeter(
                    location.latitude,
                    location.longitude,
                    it.latitude,
                    it.longitude
                )
        }
        if (!list.value.any { it.distance == null }) {
            val visitedList = list.value.filter { it.visited && it.pin != Firebase.likeIcon}.sortedBy { it.distance }
            list.value.removeAll(visitedList)
            list.value.sortBy { it.distance }
            list.value.addAll(visitedList)
        }
        return list
    }

    fun moveCameraPosition(cameraPositionState: CameraPositionState, latLng: LatLng) {
        viewModelScope.launch(Dispatchers.Main) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        latLng,
                        cameraPositionState.position.zoom
                    )
                ), 500
            )
        }
    }


    val sightListForQuery: StateFlow<List<DefaultSightFB>> = searchtext
        .flatMapLatest { query ->
            Firebase.searchDefaultSights(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2000),
            initialValue = emptyList()
        )


}