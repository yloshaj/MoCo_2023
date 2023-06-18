package com.group16.mytrips.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group16.mytrips.R
import com.group16.mytrips.data.User
import com.group16.mytrips.viewModel.LoginViewModel


@Composable
fun LoginAlert(viewModel: LoginViewModel,showBottomBar: MutableState<Boolean>, navigate: () -> Unit) {
    val users by viewModel.userList.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.setUser(User(avatar = R.drawable.ic_dummyprofilepic, name = "Nutzer wählen..."))
    }
    var expandedList by remember {
        mutableStateOf(false)
    }

    var userSelected by remember {
        mutableStateOf(false)
    }
    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .size(250.dp, 310.dp)
                .background(Color.LightGray),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }

        if (userSelected) {
            Button(
                onClick = { viewModel.setUserId(); navigate(); showBottomBar.value = true },
                modifier = Modifier.offset(0.dp, 108.dp),
                colors = ButtonDefaults.buttonColors(Color.White),
                elevation = ButtonDefaults.buttonElevation(5.dp)
            ) {
                Text(text = "OK", color = Color.Black)
            }
        }


        Column(
            modifier = Modifier
                .size(250.dp, 300.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.heightIn(25.dp))
            Text(
                text = "MyTrips",
                fontSize = 25.sp,
                modifier = Modifier.padding(0.dp, 5.dp)
            )

            Surface(shape = CircleShape, shadowElevation = 10.dp, modifier = Modifier
                .padding(0.dp, 15.dp)
                .clickable { expandedList = true }) {
                val image = ImageVector.vectorResource(id = currentUser.avatar)

                Image(
                    imageVector = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(90.dp),


                    )
            }
            Card(elevation = CardDefaults.cardElevation(5.dp), modifier = Modifier.padding(0.dp, 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(140.dp, 30.dp)
                        .padding(0.dp)
                        .background(Color.White)
                        .clickable { expandedList = !expandedList },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row() {
                        Text(text = currentUser.name)
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = "ArrowDropDown"
                        )
                    }
                }
            }



            AnimatedVisibility(visible = expandedList) {
                LazyColumn {
                    items(users) {
                        Box(modifier = Modifier
                            .background(Color.LightGray)
                            .padding(2.dp)
                            .width(110.dp)
                            .clickable {
                                viewModel.setUser(it); expandedList = false; userSelected =
                                true
                            }) {
                            Text(text = it.name)

                        }


                    }
                }

            }


        }
    }


}


@Composable
fun Login(viewModel: LoginViewModel, showBottomBar: MutableState<Boolean>,navigate: () -> Unit = {}) {
    LaunchedEffect(Unit) {
        viewModel.startListeningUserList()
        showBottomBar.value = false
    }
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(Color.LightGray), contentAlignment = Alignment.Center
        ) {
            LoginAlert(viewModel = viewModel,showBottomBar, navigate)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .background(Color.LightGray)
        )
    }


}

@Preview
@Composable
fun TempPreview() {
    val view = LoginViewModel()
    val r = remember {
        mutableStateOf(false)
    }
    Login(viewModel = view, r)
}