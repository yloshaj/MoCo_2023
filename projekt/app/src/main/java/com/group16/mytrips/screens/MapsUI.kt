package com.group16.mytrips.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.MapProperties
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.viewModel.NavigationViewModel


@Composable
fun MapsSDK(
    modifier: Modifier,
    sightList: State<MutableList<DefaultSightFB>>,
    cameraPosition: CameraPositionState,
    navigationViewModel: NavigationViewModel
) {

    val loc = navigationViewModel.getLocationLiveData().observeAsState()
    val radius = navigationViewModel.radius.collectAsState()
    val mapProperties = MapProperties(
        isMyLocationEnabled = loc.value != null,

        )

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPosition,
        properties = mapProperties,
        contentPadding = PaddingValues(top = 70.dp)
    ) {

        for (location in sightList.value) {
            var icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            if(!location.visited) {
                Circle(
                    center = LatLng(location.latitude, location.longitude),
                    radius = radius.value.toDouble(),
                    strokeColor = Color(59, 88, 145, 0),
                    strokeWidth = 2f,
                    fillColor = Color(11, 147, 41, 100)
                )
            }
            else icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            Marker(
                MarkerState(LatLng(location.latitude, location.longitude)),
                title = location.sightName,
                icon = icon
            )

        }


    }

}
