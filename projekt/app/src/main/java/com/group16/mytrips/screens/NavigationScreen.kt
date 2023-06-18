package com.group16.mytrips.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.group16.mytrips.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.viewModel.NavigationViewModel
import kotlin.math.roundToInt


@Composable
fun NavigationScreen(
    navigationViewModel: NavigationViewModel,
    navigate: () -> Unit
) {

    LaunchedEffect(Unit) {
        navigationViewModel.startListeningForData()
    }
    val loc = navigationViewModel.getLocationLiveData().observeAsState()

    var cameraPosition = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(LatLng(loc.value?.latitude ?: 51.0230970, loc.value?.longitude?:7.5643766), 14f)
    }
    var sightList = navigationViewModel.getSortedList().collectAsState()
    //var cameraPosition = navigationViewModel.getCameraPositionState()


    LaunchPermission(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        permissionTitle = "Location",
        rationale = "Location needed for navigation",
        navigate = navigate
    )
    LaunchedEffect(loc.value == null) {
        navigationViewModel.moveCameraPosition(cameraPosition, LatLng(loc.value?.latitude ?: 51.0230970, loc.value?.longitude ?: 7.5643766))
    }
    Box (modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Text(text = loc.value.toString(), color = Color.White)
        //Maps()
        Column {

            MapsSDK(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f), sightList, cameraPosition, navigationViewModel
            )
            Box(modifier = Modifier.weight(0.4f)) {

            }
        }

        SearchBar(navigationViewModel, cameraPosition)
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            )

            Box(modifier = Modifier.weight(0.4f)) {
                LocationColumn(navigationViewModel, sightList, cameraPosition)
            }

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(navigationViewModel: NavigationViewModel, cameraPosition: CameraPositionState) {
    val isSearching by navigationViewModel.isSearching.collectAsState()
    val searchText by navigationViewModel.searchtext.collectAsState()
    val sightListForQuery by navigationViewModel.sightListForQuery.collectAsState()

    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { focusManager.clearFocus(); navigationViewModel.setIsSearching(false) })
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 17.dp)
                .size(0.dp, 50.dp), shape = ShapeDefaults.Large,
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    value = searchText,
                    onValueChange = {
                        navigationViewModel.onSearchTextChange(it)
                        navigationViewModel.setIsSearching(true)
                    },
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxHeight(),
                    singleLine = true,

                    trailingIcon = {


                        IconButton(onClick = {
                            focusManager.clearFocus()
                            navigationViewModel.onSearchTextChange("")
                            navigationViewModel.setIsSearching(false)

                        }) {
                            Icon(
                                Icons.Rounded.Search, modifier = Modifier
                                    .weight(1f)
                                    .scale(1.2f)
                                    .alpha(0.6f),
                                contentDescription = null
                            )

                        }
                    })


            }
        }
        AnimatedVisibility(visible = isSearching && sightListForQuery.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 220.dp)
                    .padding(18.dp, 0.dp)
                    .background(color = Color.White)
            ) {

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sightListForQuery.size) {
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(40.dp)
                                .clickable {
                                    navigationViewModel.moveCameraPosition(
                                        cameraPosition,
                                        LatLng(
                                            sightListForQuery[it].latitude,
                                            sightListForQuery[it].longitude
                                        )
                                    ); focusManager.clearFocus(); navigationViewModel.setIsSearching(
                                    false
                                )
                                    navigationViewModel.onSearchTextChange("")
                                },
                            shape = ShapeDefaults.ExtraSmall,
                            colors = CardDefaults.cardColors(Color.White),
                            elevation = CardDefaults.cardElevation(11.dp)
                        ) {
                            Text(
                                text = sightListForQuery[it].sightName,
                                modifier = Modifier.fillMaxSize(),

                                )
                        }
                        Divider()

                    }
                }
            }

        }
    }
}

@Composable
fun LocationColumn(
    navigationViewModel: NavigationViewModel, sightList: State<MutableList<DefaultSightFB>>,
    cameraPosition: CameraPositionState
) {
    val moveCamera = navigationViewModel::moveCameraPosition
    Card(elevation = CardDefaults.cardElevation(10.dp)) {


        LazyColumn() {
            items(sightList.value) { location ->


                LocationCard(
                    locationDistance = location.distance ?: 0, location, cameraPosition, moveCamera

                )
                Divider(thickness = 1.dp)
            }
        }
    }
}

@Composable
fun LocationCard(
    locationDistance: Int,
    sight: DefaultSightFB,
    cameraPosition: CameraPositionState,
    moveCamera: (CameraPositionState, LatLng) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .heightIn(80.dp, 80.dp)
            .padding(0.dp, 0.dp)
            .background(Color.White)
            .clickable {
                val latLng = LatLng(sight.latitude, sight.longitude)
                moveCamera(cameraPosition, latLng)

            },
        shape = ShapeDefaults.ExtraSmall,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_locpoint),
                    contentDescription = "Blue Location Point",
                    tint = Color.Unspecified
                )
                Column(
                    modifier = Modifier
                        .heightIn(50.dp, 50.dp)
                        .widthIn(156.dp, 156.dp)
                ) {
                    var adjustedDistance = ""
                    if (locationDistance >= 1000) adjustedDistance =
                        ((locationDistance / 100f).roundToInt() / 10f).toString() + " km"
                    else adjustedDistance = "$locationDistance m"
                    Text(
                        text = sight.sightName,
                        fontSize = 16.sp, overflow = TextOverflow.Ellipsis, maxLines = 1
                    )
                    Text(text = adjustedDistance, fontSize = 14.sp)
                }
                SubcomposeAsyncImage(model = sight.thumbnail, contentDescription = null, loading = {
                    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.LightGray, modifier = Modifier.size(20.dp))
                    }
                }, modifier = Modifier.size(78.dp, 59.dp))


            }


        }
    }
}

