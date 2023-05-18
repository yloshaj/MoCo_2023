package com.group16.mytrips.viewModel

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.data.ModelClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavigationViewModel: ViewModel() {

    private val modelClass = ModelClass()

    private var _defaultSightList = MutableStateFlow(modelClass.defaultSightList)
    val sightList = _defaultSightList.asStateFlow()


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

}