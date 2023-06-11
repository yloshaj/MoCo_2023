package com.group16.mytrips.viewModel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.maps.android.compose.CameraPositionState
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.data.LocationLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData = LocationLiveData(application)



    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("DefaultSightFB")

    private val _defaultSightList = MutableStateFlow(emptyList<DefaultSightFB>().toMutableList())
    val defaultSightList = _defaultSightList.asStateFlow()

    private lateinit var listenerRegistration: ListenerRegistration

    private var loc = listOf(0.0,0.0)
    fun startListeningForSightList() {
        listenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(DefaultSightFB::class.java)
            } ?: emptyList()

            _defaultSightList.value = sights.toMutableList()
        }
    }

    fun stopListeningForSightList() {
        listenerRegistration.remove()
    }



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


    fun getSortedList(): StateFlow<MutableList<DefaultSightFB>> {
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
        list.value.sortBy { it.distance }
        return list
    }

    private var _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    private val _searchtext = MutableStateFlow("")
    val searchtext = _searchtext.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchtext.value = text
    }

    val sightListForQuery : StateFlow<List<DefaultSightFB>> = searchtext
        .combine(_defaultSightList) { text, sightList ->
            if(text.isBlank() || text.length < 2) {
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


    fun setIsSearching (boolean: Boolean) {
        _isSearching.value = boolean
    }




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
        }
    }

    fun getCameraPositionState() : CameraPositionState {
        val loc = getLocationLiveData()
        return if (loc.value != null) CameraPositionState(CameraPosition(LatLng(loc.value!!.latitude, loc.value!!.longitude), 14f,0f,0f))
        else CameraPositionState(CameraPosition(LatLng(51.0230970, 7.5643766),14f,0f,0f))
    }


}