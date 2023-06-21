package com.group16.mytrips.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.data.Firebase
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.misc.distanceInMeter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(application: Application): AndroidViewModel(application) {

    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData
    fun startLocationUpdates() = {
        ->
        locationLiveData.startLocationUpdates()
    }

    private val _radius = MutableStateFlow(0)
    val radius = _radius.asStateFlow()

    private val _defaultSightList = MutableStateFlow(emptyList<DefaultSightFB>().toMutableList())
    val defaultSightList = _defaultSightList.asStateFlow()


    private var _currentSight = MutableStateFlow(DefaultSightFB())
    val currentSight = _currentSight.asStateFlow()

    fun setCurrentSight(sight: DefaultSightFB) {
        _currentSight.value = sight
    }

    private var _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()

    fun setAlert(shouldShow: Boolean) {
        if (_currentSight.value.sightId != -1) _alert.value = shouldShow
    }

    private var _sightVisitedAlert = MutableStateFlow(false)
    val sightVisitedAlert = _sightVisitedAlert.asStateFlow()

    fun setSightVisitedAlert(visited: Boolean) {
        _sightVisitedAlert.value = visited
    }

    private var _uriList = MutableStateFlow<List<String>>(emptyList())
    val uriList = _uriList.asStateFlow()

    fun setUriList(list: List<String>) {
        _uriList.value = list

    }
    fun alreadyVisited() = _currentSight.value.visited
    fun startListeningForDefaultSightList() {
        Firebase.startListeningForDefaultSightList { sights ->
            viewModelScope.launch(Dispatchers.Default) {
                _defaultSightList.value = sights as MutableList<DefaultSightFB>
            }
        }
    }


    fun startListeningForRadius() {
        Firebase.startListeningForRadius { radius ->
            viewModelScope.launch(Dispatchers.Default) {
                _radius.value = radius
            }
        }
    }

    fun startListeningForData() {
        startListeningForDefaultSightList()
        startListeningForRadius()
    }

    fun uploadNewSight(newPictures: Boolean) {
        if (!alreadyVisited()) {
            viewModelScope.launch(Dispatchers.IO) {
                _currentSight.value.visited = true
                try {
                    if (!newPictures) {
                        Firebase.uploadNewSight(null, null, _currentSight.value)
                    } else {
                        val downloadUrls = _uriList.value.map { uri ->
                            Firebase.uploadImage(uri)
                        }
                        Firebase.uploadNewSight(
                            downloadUrls.getOrNull(0),
                            downloadUrls.getOrNull(1),
                            _currentSight.value
                        )
                    }
                } catch (e: Exception) {
                    Log.e("com.group16.mytrips.data.Firebase Storage", e.toString())
                }
            }
        }

    }

    fun getSortedList(): StateFlow<MutableList<DefaultSightFB>> {
        val location = getLocationLiveData().value
        val list = _defaultSightList.asStateFlow()
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
        list.value.sortBy { it.distance }
        if (location != null) list.value.retainAll { it.distance!! <= _radius.value }

        return list
    }
}