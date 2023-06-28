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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.group16.mytrips.R
import com.group16.mytrips.data.Avatar
import com.group16.mytrips.data.SightFB
import com.group16.mytrips.viewModel.ProfileViewModel
import kotlinx.coroutines.Job
import kotlin.math.roundToInt


@Composable
fun SightScreen(sightId: String?, profileViewModel: ProfileViewModel) {
    val sights = profileViewModel.sightList.collectAsState()
    var currentSight by remember {
        mutableStateOf(
            SightFB(
                -1,
                sightName = "",
                date = "",
                latitude = 0.0,
                longitude = 0.0
            )
        )
    }
    if (sightId != null) {
        for (sight in sights.value) if (sightId == sight.sightId.toString()) currentSight = sight
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SubcomposeAsyncImage(
            model = currentSight.picture,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(), loading = {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = Color.LightGray,
                        modifier = Modifier.size(100.dp)
                    )
                }

            }

        )

        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            Text(text = currentSight.sightName, fontSize = 30.sp, color = Color.White)
            Text(text = currentSight.date, color = Color.White)
        }
    }

}

@Composable
fun Profile(
    profileViewModel: ProfileViewModel,
    onItemClicked: (sightId: String) -> Unit
) {
    val sights = profileViewModel.sightList.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    )
    Column {
        ProfileHeader(profileViewModel)
        SightGrid(list = sights, onItemClicked, profileViewModel::updateLiked)
    }
}

@Composable
fun ProfilePic(modifier: Modifier, id: Int) {
    Surface(shape = CircleShape, shadowElevation = 10.dp) {
        val image = ImageVector.vectorResource(id = id)

        Image(
            imageVector = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.size(90.dp),


            )
    }

}

@Composable
fun ProfileHeader(profileViewModel: ProfileViewModel) {
    val avatarList = profileViewModel.avatarList.collectAsState()
    val user by profileViewModel.user.collectAsState()


    var expanded by remember {
        mutableStateOf(false)
    }

    var profileId = user.avatar
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
                Text(
                    text = user.name,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(0.dp, 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfilePic(
                        modifier = Modifier
                            .padding(0.dp)
                            .clickable { expanded = !expanded }, id = profileId
                    )

                    LevelBar(overAllXP = user.overallxp)
                }
                AnimatedVisibility(visible = expanded) {
                    ProfileSelection1({ value ->
                        profileId = value
                        expanded = !expanded
                        profileViewModel.updateAvatar(value)
                    }, avatarList)
                }

            }
            Text(
                text = "Gefundene Locations:",
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp, 8.dp)
            )
        }
    }
}


@Composable
fun LevelBar(overAllXP: Int) {
    val level = overAllXP / 100f
    val discreteLevel = level.toInt()
    val percentage = ((level - discreteLevel) * 100).roundToInt() / 100f
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
fun SightGrid(
    list: State<List<SightFB>>,
    onItemClicked: (userId: String) -> Unit,
    updateLiked: (SightFB) -> Job
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(list.value.size) { it ->
            SightCard(
                sight = list.value[it],
                onItemClicked = onItemClicked,
                updateLiked = updateLiked
            )
        }
    }
}


@Composable
fun SightCard(
    sight: SightFB,
    onItemClicked: (sightId: String) -> Unit,
    updateLiked: (SightFB) -> Job
) {
    Card(
        modifier = Modifier
            .widthIn(0.dp, 156.dp)
            .padding(13.dp, 8.dp)
            .clickable { onItemClicked(sight.sightId.toString()) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = ShapeDefaults.ExtraSmall
    ) {
        Column(modifier = Modifier.padding(4.dp, 2.dp)) {
            Box(
                modifier = Modifier
                    .size(156.dp, 118.dp)
                    .padding(0.dp, 0.dp)
            ) {
                SubcomposeAsyncImage(model = sight.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color.LightGray,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                )
            }

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
            Text(text = sight.date.substring(0, 10), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            val context  = LocalContext.current
            val resourceId = context.resources.getIdentifier(sight.pin, "drawable", context.packageName)
            var xp = 30
            var icon = R.drawable.ic_heart_custom_gray
            var shouldUpdate = true

            if (resourceId == R.drawable.ic_special_pin) {
                xp = 50
                icon = R.drawable.ic_special_icon
                shouldUpdate = false
            } else if (resourceId == R.drawable.ic_liked_pin) icon = R.drawable.ic_heart_custom
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "+$xp xp", modifier = Modifier.weight(0.2f))
                Box(modifier = Modifier.weight(0.2f))
                Box(modifier = Modifier
                    .size(30.dp)
                    .weight(0.1f)) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = icon),
                        contentDescription = "heart",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxSize()
                            .clickable { if (shouldUpdate) updateLiked(sight) })
                }
            }

        }
    }
}


//@Preview(showBackground = true)
@Composable
fun Profile(
    onItemClicked: (sightId: String) -> Unit,
    profileViewModel: ProfileViewModel
) {

    LaunchedEffect(Unit) {
        profileViewModel.startListeningForData()

    }
    Profile(profileViewModel, onItemClicked)

}


@Composable
fun ProfileSelection1(onClick: (id: Int) -> Unit, avatarList: State<List<Avatar>>) {

    Column(
        modifier = Modifier
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
fun TierGrid(modifier: Modifier, onClick: (id: Int) -> Unit, avatarList: State<List<Avatar>>) {

    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), shape = RectangleShape
    ) {
        //Icon(imageVector = Icons.Rounded.Close, contentDescription = null, Modifier.alpha(0.6f).padding(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.1f)
                    .rotate(90f)
            )
            LazyHorizontalGrid(
                modifier = Modifier.weight(1f),
                rows = GridCells.Fixed(1), contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                items(avatarList.value.size) { it ->
                    val avatarId = avatarList.value[it].path
                    Icon(
                        painter = painterResource(avatarId),
                        contentDescription = null, tint = Color.Unspecified,
                        modifier = Modifier.clickable {
                            onClick(avatarId)
                        }
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.1f)
                    .rotate(-90f)
            )
        }
    }
}
