package com.group16.mytrips.screens


import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.MapProperties
import com.group16.mytrips.R
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.misc.makeTransparent
import com.group16.mytrips.viewModel.NavigationViewModel


@Composable
fun MapsSDK(
    modifier: Modifier,
    sightList: State<MutableList<DefaultSightFB>>,
    cameraPosition: CameraPositionState,
    navigationViewModel: NavigationViewModel
) {
    val context = LocalContext.current
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

            val resourceId = context.resources.getIdentifier(location.pin, "drawable", context.packageName)
            var icon = AppCompatResources.getDrawable(context, resourceId)?.toBitmap()?: throw Exception("Could Not find Resource!")
            var alpha = 100
            if(location.visited && resourceId != R.drawable.ic_liked_pin) {

                icon = makeTransparent( icon,alpha)
                alpha = 25

            } else if(resourceId == R.drawable.ic_liked_pin) alpha = 25

            Circle(
                center = LatLng(location.latitude, location.longitude),
                radius = radius.value.toDouble(),
                strokeColor = Color(59, 88, 145, 0),
                strokeWidth = 2f,
                fillColor = Color(11, 147, 41, alpha)
            )
            Marker(
                MarkerState(LatLng(location.latitude, location.longitude)),
                title = location.sightName,
                icon = BitmapDescriptorFactory.fromBitmap(icon)
            )

        }


    }

}
