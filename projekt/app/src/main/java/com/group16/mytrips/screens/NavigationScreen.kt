package com.group16.mytrips.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.group16.mytrips.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

val location = Location(R.drawable.ic_dummylocationpic, "Location Name", 2300)
val locationList = listOf(location, location, location, location, location, location)


@Composable
fun SearchBar() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row() {

        }
    }
}

@Composable
fun LocationColumn (list: List<Location>) {
    LazyColumn() {
        items(list) {location ->
            LocationCard(locationDefaultPicture = painterResource(id = location.locationPicture),
                locationName = location.locationName, locationDistance = location.distance)
        }
    }
}

@Composable
fun LocationCard (locationDefaultPicture: Painter, locationName: String, locationDistance: Int) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .heightIn(0.dp, 80.dp)
            .padding(0.dp, 1.dp)
            .background(Color.White),
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
                Column(modifier = Modifier.heightIn(0.dp, 50.dp)) {
                    var adjustedDistance = ""
                    if (locationDistance >= 1000) adjustedDistance =
                        ((locationDistance / 100f).roundToInt() / 10f).toString() + " km"
                    else adjustedDistance = "$locationDistance m"
                    Text(text = locationName,
                        fontSize = 18.sp
                        )
                    Text(text = adjustedDistance)
                }
                Icon(
                    painter = locationDefaultPicture,
                    contentDescription = "Default Picture of Sight",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .scale(0.65f)
                        .offset(32.dp, 0.dp)
                    )

            }
            //Box(modifier = Modifier.size(120.dp, 5.dp).background(Color.Gray))
                Card() {
                    Icon (
                        Icons.Rounded.Search,
                        contentDescription = null
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
    SearchBar()
    //LocationColumn(list = locationList)
    //LocationCard(locationDefaultPicture = painterResource(id = R.drawable.ic_dummylocationpic),
    //    locationName ="Location Name" , locationDistance = locationDistance )
}