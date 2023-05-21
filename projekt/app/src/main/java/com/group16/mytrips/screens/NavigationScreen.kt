package com.group16.mytrips.screens

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.group16.mytrips.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.viewModel.ApplicationViewModel
import com.group16.mytrips.viewModel.MapViewModel
import com.group16.mytrips.viewModel.NavigationViewModel
import kotlin.math.roundToInt


@Composable
fun NavigationScreen(
    navViewModel: NavigationViewModel,
    applicationViewModel: ApplicationViewModel,
    requestPermission: (String, String, ActivityResultLauncher<String>) -> Unit,
    permissionLauncher: ActivityResultLauncher<String>
) {
    LaunchPermission(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        permissionTitle = "Location",
        rationale = "Location needed for navigation",
        requestPermission = requestPermission,
        permissionLauncher = permissionLauncher
    )
    val loc = applicationViewModel.getLocationLiveData().observeAsState()
    var sightList = applicationViewModel.getSortedList().collectAsState()
    var cameraPosition = rememberCameraPositionState {
        if (loc.value != null)
            position = CameraPosition.fromLatLngZoom(LatLng(loc.value!!.latitude, loc.value!!.longitude), 14f)
        else
            position = CameraPosition.fromLatLngZoom(LatLng(sightList.value[0].latitude, sightList.value[0].longitude), 14f)
    }




    Box {
        Text(text = loc.value.toString())
        //Maps()
        Column {

            MapsSDK(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f), sightList, cameraPosition,loc, applicationViewModel
            )
            Box(modifier = Modifier.weight(0.4f)) {

            }
        }

        SearchBar(navViewModel)
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            )

            Box(modifier = Modifier.weight(0.4f)) {
                LocationColumn(navViewModel, sightList, cameraPosition)
            }

        }
    }
}

@Composable
fun Maps() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.ic_fullmap), contentDescription = null)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(navViewModel: NavigationViewModel) {
    val isSearching by navViewModel.isSearching.collectAsState()
    val searchText by navViewModel.searchtext.collectAsState()
    val sightListForQuery by navViewModel.sightListForQuery.collectAsState()

    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { focusManager.clearFocus(); navViewModel.setIsSearching(false) })
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
                        navViewModel.onSearchTextChange(it)
                        navViewModel.setIsSearching(true)
                    },
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxHeight(),
                    singleLine = true,

                    trailingIcon = {


                        IconButton(onClick = {
                            focusManager.clearFocus()
                            navViewModel.onSearchTextChange("")
                            navViewModel.setIsSearching(false)

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
            Card(modifier = Modifier.fillMaxWidth()) {
                LazyColumn() {
                    items(sightListForQuery.size) {
                        Text(text = sightListForQuery[it].sightName)
                    }
                }
            }

        }
    }
}

@Composable
fun LocationColumn(
    navViewModel: NavigationViewModel, sightList: State<MutableList<DefaultSight>>,
    cameraPosition: CameraPositionState
) {
    val moveCamera = navViewModel::moveCameraPosition
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
    sight: DefaultSight,
    cameraPosition: CameraPositionState,
    moveCamera: (CameraPositionState, LatLng) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .heightIn(0.dp, 80.dp)
            .padding(0.dp, 0.dp)
            .background(Color.White)
            .clickable {
                val latLng = LatLng(sight.latitude, sight.longitude)
                moveCamera(cameraPosition, latLng)

            },
        shape = ShapeDefaults.ExtraSmall,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_locpoint),
                    contentDescription = "Blue Location Point",
                    tint = Color.Unspecified
                )
                Column(
                    modifier = Modifier
                        .heightIn(0.dp, 50.dp)
                        .widthIn(156.dp, 156.dp)
                ) {
                    var adjustedDistance = ""
                    if (locationDistance >= 1000) adjustedDistance =
                        ((locationDistance / 100f).roundToInt() / 10f).toString() + " km"
                    else adjustedDistance = "$locationDistance m"
                    Text(
                        text = sight.sightName,
                        fontSize = 18.sp, overflow = TextOverflow.Ellipsis, maxLines = 1
                    )
                    Text(text = adjustedDistance)
                }
                Icon(
                    painter = painterResource(id = sight.defualtPicture),
                    contentDescription = "Default Picture of Sight",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .scale(0.65f)
                        .offset(32.dp, 0.dp)
                )

            }


        }
    }
}


@Preview
@Composable
fun PreviewNavScreen() {
    var locationDistance by remember {
        mutableStateOf(1345)
    }
    //Maps()
    //LocationColumn(list = locationList)
    //LocationCard(locationDefaultPicture = painterResource(id = R.drawable.ic_dummylocationpic),
    //locationName ="Location Name" , locationDistance = locationDistance )
    //NavigationScreen()
    //SearchBar()
}