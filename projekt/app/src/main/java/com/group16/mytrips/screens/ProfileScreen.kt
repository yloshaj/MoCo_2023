package com.group16.mytrips.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group16.mytrips.R
import kotlin.math.roundToInt

val sight = Sight(R.drawable.ic_dummylocationpic, sightName = "Location Name", date = "Datum", coordinates = "Koordinaten")
val listOfSight = listOf(sight, sight, sight, sight, sight, sight, sight,sight, sight)

@Composable
fun ProfileScreen (profilbild: Painter, name: String, overAllXP: Int, listOfSight: List<Sight>) {
    Column {
        ProfileHeader(profilbild = profilbild , name = name, overAllXP = overAllXP)
        SightGrid(list = listOfSight)
    }
}
@Composable
fun ProfileHeader (profilbild: Painter, name: String, overAllXP: Int) {
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
                Text(text = name, fontSize = 30.sp)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        profilbild,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(0.dp)
                    )
                    LevelBar(overAllXP = overAllXP)
                }

            }
            Text(text = "Gefundene Locations:", fontSize = 20.sp)
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
                Box(
                    modifier = Modifier
                        .offset(2.dp, 4.dp)
                        .blur(10.dp)
                        .alpha(0.5f)
                        .size(barMaxLength.dp, 20.dp)
                        .background(Color.LightGray)
                )
                Box(
                    modifier = Modifier
                        .size(barMaxLength.dp, 20.dp)
                        .background(Color.LightGray),
                )
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
fun SightGrid (list: List<Sight>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(list.size) { it ->
                SightCard(
                    painter = painterResource(id = list[it].picture),
                    titel = list[it].sightName,
                    date = list[it].date,
                    koordinaten = list[it].coordinates
                )
            }
        }
}



@Composable
fun SightCard (painter: Painter, titel: String, date: String, koordinaten: String) {
    Card(modifier = Modifier
        .widthIn(0.dp, 156.dp)
        .padding(13.dp, 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = ShapeDefaults.ExtraSmall
    ) {
        Column(modifier = Modifier.padding(4.dp,2.dp)) {


            Icon(
                painter,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.padding(0.dp, 7.dp)
            )
            Row(modifier = Modifier.heightIn(50.dp, 60.dp)) {

                Text(
                    text = titel,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(0.dp, 156.dp),
                    maxLines = 2,
                )
            }
            Text(text = date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = koordinaten,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(0.dp, 156.dp),
                maxLines = 1
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHeader() {
    var xp by remember {
        mutableStateOf(670)
    }
    //ProfileHeader(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP = xp)
    ProfileScreen(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP =xp, listOfSight)
}
