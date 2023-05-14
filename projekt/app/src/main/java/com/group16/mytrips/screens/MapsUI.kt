package com.group16.mytrips.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.createBitmap
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapsSDK(modifier: Modifier) {
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
    MapsSDK(modifier = Modifier.fillMaxSize())
}