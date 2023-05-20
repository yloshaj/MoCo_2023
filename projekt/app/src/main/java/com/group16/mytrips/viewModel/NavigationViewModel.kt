package com.group16.mytrips.viewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.data.ModelClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NavigationViewModel(context: Context): ViewModel() {

    private val modelClass = ModelClass()

    private var _defaultSightList = MutableStateFlow(modelClass.defaultSightList)
    val sightList = _defaultSightList.asStateFlow()



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

    private var _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchtext.value = text
    }

    fun setIsSearching (boolean: Boolean) {
        _isSearching.value = boolean
    }

    private var _cameraPosition = MutableStateFlow(CameraPosition.fromLatLngZoom(LatLng(sightList.value[0].latitude,sightList.value[0].longitude), 14f))
    var cameraPosition = _cameraPosition.asStateFlow()

    private val _cameraTest = MutableLiveData<MutableList<DefaultSight>>()

    fun newCameraPos (latLng: LatLng) {
        _cameraPosition.value = CameraPosition.fromLatLngZoom(latLng, 14f)
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
            _cameraPosition.value = cameraPositionState.position
        }
    }

    fun addDefaultSight (defaultSight: DefaultSight) {
        val newList = mutableListOf<DefaultSight>()
        newList.addAll(_defaultSightList.value)
        newList.add(defaultSight)
        _defaultSightList.value = newList
    }

    private var _number = MutableStateFlow(0)
    val number = _number.asStateFlow()
    fun addNumber() {
        _number.value++
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    val locationSource: LocationSource = object : LocationSource {
        override fun activate(listener: LocationSource.OnLocationChangedListener) {
            // Implement the logic to activate the location updates
            val locationRequest = LocationRequest.Builder(2000).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location: Location = locationResult.lastLocation ?: return
                    listener.onLocationChanged(location)
                }
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }



        override fun deactivate() {
            // Implement the logic to deactivate the location updates
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                currentLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}