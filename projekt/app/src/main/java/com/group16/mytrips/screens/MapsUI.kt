package com.group16.mytrips.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.group16.mytrips.data.DefaultSight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MapProperties
import com.group16.mytrips.viewModel.MapViewModel


@Composable
fun MapsSDK(modifier: Modifier, sightList: State<MutableList<DefaultSight>>, cameraPosition: CameraPositionState, addDefualtLocation: (sight:DefaultSight) -> Unit, mapViewModel: MapViewModel) {

    LaunchedEffect(true) {
        mapViewModel.locationSource.activate { location ->
            location.let {
                mapViewModel.currentLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }
    val loc by mapViewModel.currentLocation.observeAsState()


    
    val mapProperties = MapProperties(
        isMyLocationEnabled = loc != null
    )
    

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPosition,
        locationSource = mapViewModel.locationSource,
        properties = mapProperties,
        contentPadding = PaddingValues(top = 70.dp)


    ) {

        for (location in sightList.value) Marker(
            MarkerState(LatLng(location.latitude, location.longitude)),
            title = location.sightName,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )


    }

}
@Composable
fun MapsSDK1(modifier: Modifier) {
    val location = LatLng(51.0230345, 7.5654156)
    val locationState = MarkerState(position = location)
    val location2 = LatLng(51.0243073, 7.5662209)
    val locationState2 = MarkerState(position = location2)
    val location3 = LatLng(51.0263193, 7.5634976)
    val locationState3 = MarkerState(position = location3)
    val cameraPositionState = rememberCameraPositionState{
            position = CameraPosition.fromLatLngZoom(location, 14f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(state = locationState, title = "Sehenswürdigkeit",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        Marker(state = locationState2, title = "Marker 2", visible = true,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        Marker(state = locationState3, title = "Marker 3", visible = true,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
    }
}

@Preview
@Composable
fun Preview() {

}