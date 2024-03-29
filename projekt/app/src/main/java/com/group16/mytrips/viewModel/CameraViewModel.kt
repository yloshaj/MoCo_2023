package com.group16.mytrips.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.data.Firebase
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.data.UploadNewSightWorker
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
            viewModelScope.launch(Dispatchers.IO) {
                _defaultSightList.value = sights as MutableList<DefaultSightFB>
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
        startListeningForDefaultSightList()
        startListeningForRadius()
    }

    fun uploadNewSightUsingWorkManager(newPictures: Boolean) {
        if (!alreadyVisited()) {
            _currentSight.value.visited = true
            val workRequest = UploadNewSightWorker.createWorkRequest(newPictures, _currentSight.value, _uriList.value)
            val workManager = WorkManager.getInstance(getApplication())
            workManager.enqueue(workRequest)
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
        else list.value.removeAll { true }

        return list
    }
}