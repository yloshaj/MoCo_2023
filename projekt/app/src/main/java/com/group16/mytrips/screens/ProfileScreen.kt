package com.group16.mytrips.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group16.mytrips.R
import com.group16.mytrips.data.Sight
import kotlin.math.roundToInt

val sight0 = Sight(sightId = "sight0", picture = R.drawable.ic_dummylocationpic, sightName = "Location 0", date = "25.04,2023", coordinates = "Koordinaten")
val sight1 = Sight(sightId = "sight1", picture = R.drawable.ic_dummylocationpic, sightName = "Location 1", date = "20.04.2023", coordinates = "Koordinaten")
val sight2 = Sight(sightId = "sight2", picture = R.drawable.ic_dummylocationpic, sightName = "Location 2", date = "14.04.2023", coordinates = "Koordinaten")
val sight3 = Sight(sightId = "sight3", picture = R.drawable.ic_dummylocationpic, sightName = "Location 3", date = "26.02.2023", coordinates = "Koordinaten")
val sight4 = Sight(sightId = "sight4", picture = R.drawable.ic_dummylocationpic, sightName = "Location 4", date = "25,02,2023", coordinates = "Koordinaten")
val sight5 = Sight(sightId = "sight5", picture = R.drawable.ic_dummylocationpic, sightName = "Location 5", date = "19.02.1023", coordinates = "Koordinaten")
val sight6 = Sight(sightId = "sight6", picture = R.drawable.ic_dummylocationpic, sightName = "Location 6", date = "05.01.2023", coordinates = "Koordinaten")
val sight7 = Sight(sightId = "sight7", picture = R.drawable.ic_dummylocationpic, sightName = "Location 7", date = "19.12.2022", coordinates = "Koordinaten")
val sight8 = Sight(sightId = "sight8", picture = R.drawable.ic_dummylocationpic, sightName = "Location 8", date = "04.12.2022", coordinates = "Koordinaten")

val listOfSight = listOf(sight0, sight1, sight2, sight3, sight4, sight5, sight6,sight7, sight8)
val listOfAvatars = listOf(R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic,
    R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic,
    R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic,
    R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic,R.drawable.ic_dummyprofilepic
)

@Composable
fun SightScreen (sightId: String?) {

    var currentSight by remember {
        mutableStateOf(Sight("",0,"","",""))
    }
    if (sightId != null) {
        for (sight in listOfSight) if (sightId == sight.sightId) currentSight = sight
    }
    Box (
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        Icon(
            painter = painterResource(id = currentSight.picture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = Color.Unspecified
        )
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            Text(text = currentSight.sightName, fontSize = 30.sp, color = Color.White)
            Text(text = currentSight.date, color = Color.White)
        }
    }

}
@Composable
fun ProfileScreen (profilbild: Painter, name: String, overAllXP: Int, listOfSight: List<Sight>, onItemClicked: (sightId: String) -> Unit) {
    Column {
        ProfileHeader(profilbild = profilbild , name = name, overAllXP = overAllXP)
        SightGrid(list = listOfSight, onItemClicked)
    }
}
@Composable
fun ProfileHeader (profilbild: Painter, name: String, overAllXP: Int) {
    var expanded  by remember {
        mutableStateOf(false)
    }
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = ShapeDefaults.ExtraSmall
    ) {
        Column(modifier = Modifier.background(Color.White)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,

                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = name, fontSize = 30.sp, modifier = Modifier.padding(0.dp, 8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        profilbild,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(0.dp)
                            .clickable { expanded = !expanded }
                    )
                    LevelBar(overAllXP = overAllXP)
                }
                AnimatedVisibility(visible = expanded) {
                    ProfileSelection1()
                }
                //if (extension) ProfileSelection1()




            }
            Text(text = "Gefundene Locations:", fontSize = 20.sp, modifier = Modifier.padding(0.dp, 8.dp))
        }
    }
}


@Composable
fun LevelBar (overAllXP: Int) {
    val level = overAllXP/100f
    val discreteLevel = level.toInt()
    val percentage = ((level-discreteLevel) * 100).roundToInt() / 100f
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(0.dp)
        ) {
            Text(text = discreteLevel.toString(), fontSize = 30.sp)
            Box() {
                val barMaxLength = 180
                val barLength = barMaxLength * percentage
                Surface(shadowElevation = 10.dp) {
                    Box(
                        modifier = Modifier
                            .size(barMaxLength.dp, 20.dp)
                            .background(Color.LightGray),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(barLength.dp, 20.dp)
                        .background(Color(11, 147, 41))
                )
            }
            Text(text = (discreteLevel + 1).toString(), fontSize = 30.sp)
        }
        val nextLevelXP = (discreteLevel + 1) * 100
        Text(text = "$overAllXP/$nextLevelXP")
    }
}

@Composable
fun SightGrid (list: List<Sight>, onItemClicked: (userId: String) -> Unit) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(list.size) { it ->
                SightCard(
                    sight = list[it],
                    onItemClicked = onItemClicked
                )
            }
        }
}



@Composable
fun SightCard (sight: Sight, onItemClicked: (sightId: String) -> Unit) {
    Card(modifier = Modifier
        .widthIn(0.dp, 156.dp)
        .padding(13.dp, 4.dp)
        .clickable { onItemClicked(sight.sightId) },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = ShapeDefaults.ExtraSmall
    ) {
        Column(modifier = Modifier.padding(4.dp,2.dp)) {


            Icon(
                painterResource(id = sight.picture),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.padding(0.dp, 7.dp)
            )
            Row(modifier = Modifier.heightIn(50.dp, 60.dp)) {

                Text(
                    text = sight.sightName,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(0.dp, 156.dp),
                    maxLines = 2,
                )
            }
            Text(text = sight.date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = sight.coordinates,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(0.dp, 156.dp),
                maxLines = 1
            )
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun PreviewHeader(onItemClicked: (sightId: String) -> Unit) {
    var xp by remember {
        mutableStateOf(670)
    }
    //ProfileHeader(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP = xp)
    ProfileScreen(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP =xp, listOfSight, onItemClicked)
}
@Preview(showBackground = true)
@Composable
fun ActualPreview() {
    //ProfileHeader(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP = 130 )
    ProfileSelection1()
}
@Composable
fun ProfileSelection (){
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            modifier =  Modifier.weight(1f) , contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            items(listOfAvatars.size) { it ->
                androidx.compose.material3.Icon(painter = painterResource(id = listOfAvatars[it]), contentDescription = null, tint = Color.Unspecified )
            }
        }
        Divider()
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            modifier =  Modifier , contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            items(listOfAvatars.size) { it ->
                androidx.compose.material3.Icon(painter = painterResource(id = listOfAvatars[it]), contentDescription = null, tint = Color.Unspecified )
            }
        }
    }

}


@Composable
fun ProfileSelection1 (){

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
         ) {
        val gridModifier = Modifier
            .weight(1f)
            .padding(0.dp, 1.dp)
        TierGrid(modifier = gridModifier)
        TierGrid(modifier = gridModifier)

    }

}

@Composable
fun TierGrid (modifier: Modifier){

    Card(modifier = modifier, elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), shape = ShapeDefaults.ExtraSmall) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null, modifier = Modifier
                .weight(0.1f)
                .rotate(90f))
            LazyHorizontalGrid(
                modifier = Modifier.weight(1f),
                rows = GridCells.Fixed(1), contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                items(listOfAvatars.size) { it ->
                    Icon(
                        painter = painterResource(id = listOfAvatars[it]),
                        contentDescription = null, tint = Color.Unspecified
                    )
                }
            }
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null, modifier = Modifier
                .weight(0.1f)
                .rotate(-90f)
            )
        }
    }
}