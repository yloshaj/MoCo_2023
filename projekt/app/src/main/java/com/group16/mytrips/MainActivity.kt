package com.group16.mytrips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.group16.mytrips.data.BottomNavigationItem
import com.group16.mytrips.screens.BottomNavigationBar
import com.group16.mytrips.screens.Navigation
import com.group16.mytrips.screens.NavigationRoute

import com.group16.mytrips.ui.theme.MyTripsTheme
import com.group16.mytrips.viewModel.NavigationViewModel
import com.group16.mytrips.viewModel.CameraViewModel
import com.group16.mytrips.viewModel.ProfileViewModel


class MainActivity : ComponentActivity() {



    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val profileViewModel = ProfileViewModel()
            val cameraViewModel = CameraViewModel(this.application)
            val appViewModel = NavigationViewModel(this.application)


            MyTripsTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            items = listOf(
                                BottomNavigationItem(
                                    name = "Navigation",
                                    route = NavigationRoute.NavigationScreen.route,
                                    icon = Icons.Rounded.Place
                                ),
                                BottomNavigationItem(
                                    name = "Profil",
                                    route = NavigationRoute.ProfileScreen.route,
                                    icon = Icons.Rounded.Person
                                ),
                                BottomNavigationItem(
                                    name = "Kamera",
                                    route = NavigationRoute.CameraScreen.route,
                                    icon = ImageVector.vectorResource(id = R.drawable.ic_photo_camera)
                                )
                            ),
                            navController = navController,
                            onItemClick = {
                                navController.navigate(it.route) {
                                    popUpTo(NavigationRoute.ProfileScreen.route)
                                }
                            })
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    0.dp,
                                    0.dp,
                                    0.dp,
                                    innerPadding.calculateBottomPadding()
                                )
                            )
                    ) {
                        Navigation(navController = navController, appViewModel, profileViewModel, cameraViewModel)
                    }
                }

            }

        }


    }

}

