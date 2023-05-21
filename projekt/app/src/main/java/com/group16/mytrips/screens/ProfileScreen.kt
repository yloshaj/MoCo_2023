package com.group16.mytrips.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group16.mytrips.R
import com.group16.mytrips.data.Sight
import com.group16.mytrips.viewModel.ApplicationViewModel
import kotlin.math.roundToInt






@Composable
fun SightScreen (sightId: String?, applicationViewModel: ApplicationViewModel) {
    val sights = applicationViewModel.sightList.collectAsState()
    var currentSight by remember {
        mutableStateOf(Sight(-1,R.drawable.ic_dummylocationpic,R.drawable.ic_dummylocationpic, sightName = "", date = "", latitude = 0.0, longitude = 0.0))
    }
    if (sightId != null) {
        for (sight in sights.value) if (sightId == sight.sightId.toString()) currentSight = sight
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
fun ProfileScreen (applicationViewModel: ApplicationViewModel, onItemClicked: (sightId: String) -> Unit) {
    val sights = applicationViewModel.sightList.collectAsState()


    Column {
        ProfileHeader(applicationViewModel)
        SightGrid(list = sights, onItemClicked)
    }
}
@Composable
fun ProfilePic(modifier: Modifier, id: Int) {
    Surface(shape = CircleShape, shadowElevation = 10.dp) {
        val image = ImageVector.vectorResource(id = id)

        Image(imageVector = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.size(90.dp),


        )
    }

}
@Composable
fun ProfileHeader (applicationViewModel: ApplicationViewModel) {
    val avatarList = applicationViewModel.avatar.collectAsState()
    val xp by applicationViewModel.xp.collectAsState()
    val pic = avatarList.value[0]
    var expanded  by remember {
        mutableStateOf(false)
    }

    var profileId by remember {
        mutableStateOf(pic)
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
                Text(text = "Max Mustermann", fontSize = 30.sp, modifier = Modifier.padding(0.dp, 8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfilePic(modifier = Modifier
                        .padding(0.dp)
                        .clickable { expanded = !expanded }, id = profileId)

                    LevelBar(overAllXP = xp)
                }
                AnimatedVisibility(visible = expanded) {
                    ProfileSelection1 ({ value ->
                        profileId = value
                        expanded = !expanded
                    }, avatarList)
                }

            }
            Text(text = "Gefundene Locations:", fontSize = 20.sp, modifier = Modifier.padding(10.dp, 8.dp))
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
fun SightGrid (list: State<MutableList<Sight>>, onItemClicked: (userId: String) -> Unit) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(list.value.size) { it ->
                SightCard(
                    sight = list.value[it],
                    onItemClicked = onItemClicked
                )
            }
        }
}



@Composable
fun SightCard (sight: Sight, onItemClicked: (sightId: String) -> Unit) {
    Card(modifier = Modifier
        .widthIn(0.dp, 156.dp)
        .padding(13.dp, 8.dp)
        .clickable { onItemClicked(sight.sightId.toString()) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = ShapeDefaults.ExtraSmall
    ) {
        Column(modifier = Modifier.padding(4.dp,2.dp)) {
            Box(modifier = Modifier.size(156.dp, 118.dp).padding(0.dp, 0.dp)) {Icon(
                painterResource(id = sight.pictureThumbnail),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxSize()

            )}
            /*
            Image(painterResource(id = sight.pictureThumbnail),
                contentDescription = null, modifier = Modifier
                    .scale(3f)
                    .padding(0.dp, 20.dp))
            Icon(
                painterResource(id = sight.pictureThumbnail),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .scale(3f)
                    .padding(0.dp, 7.dp)
            )*/
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
                text = "${sight.latitude}, ${sight.longitude}",
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
fun PreviewHeader(onItemClicked: (sightId: String) -> Unit, applicationViewModel: ApplicationViewModel) {
    var xp by remember {
        mutableStateOf(670)
    }

    //ProfileHeader(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP = xp)
    ProfileScreen(applicationViewModel, onItemClicked)
}



@Preview(showBackground = true)
@Composable
fun ActualPreview() {
    //ProfileHeader(profilbild = painterResource(id = R.drawable.ic_dummyprofilepic), name = "Max Mustermann", overAllXP = 130 )
    //ProfileSelection1()
    //SlideCard(onClick = {})
}


@Composable
fun ProfileSelection1 (onClick: (id: Int) -> Unit, avatarList: State<List<Int>>){

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
         ) {
        val gridModifier = Modifier
            .weight(1f)
            .padding(0.dp, 10.dp)
        TierGrid(modifier = gridModifier, onClick, avatarList)

    }

}

@Composable
fun TierGrid (modifier: Modifier, onClick: (id: Int) -> Unit, avatarList: State<List<Int>>){

    Card(modifier = modifier, elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), shape = RectangleShape) {
        //Icon(imageVector = Icons.Rounded.Close, contentDescription = null, Modifier.alpha(0.6f).padding(4.dp))

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
                items(avatarList.value.size) { it ->
                    val avatarId = avatarList.value[it]
                    Icon(
                        painter = painterResource(avatarId),
                        contentDescription = null, tint = Color.Unspecified,
                        modifier = Modifier.clickable {
                            onClick(avatarId)
                        }
                    )
                }
            }
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null, modifier = Modifier
                .weight(0.1f)
                .rotate(-90f)
            )
        }
        //SlideCard(onClick = onClick)
    }
}
/*
@Composable
fun SlideCard (onClick: (id: Int) -> Unit) {
    Box(modifier = Modifier.height(110.dp), contentAlignment = Alignment.Center) {
        LazyHorizontalGrid(
            modifier = Modifier,
            rows = GridCells.Fixed(1), contentPadding = PaddingValues(25.dp,10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            items(listOfAvatars.size) { it ->
                val avatarId = listOfAvatars[it]
                Icon(
                    painter = painterResource(avatarId),
                    contentDescription = null, tint = Color.Unspecified,
                    modifier = Modifier.clickable {
                        onClick(avatarId)
                    }
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()) {
            val brush1 = Brush.horizontalGradient(listOf(Color.Gray, Color(0f,0f,0f,0f)))
            val brush2 = Brush.horizontalGradient(listOf(Color(0f,0f,0f,0f), Color.Gray))
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color(1f,1f,1f,0.7f),
                modifier = Modifier
                    .fillMaxHeight()
                    .background(brush1)
                    .rotate(90f)
            )
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color(1f,1f,1f,0.7f) ,
                modifier = Modifier
                    .fillMaxHeight()
                    .background(brush2)
                    .rotate(-90f))
        }
    }

}

 */