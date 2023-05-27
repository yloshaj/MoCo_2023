package com.group16.mytrips.screens

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.articlecamera.Cam.CameraView
import com.group16.mytrips.viewModel.ApplicationViewModel
import com.group16.mytrips.viewModel.CameraViewModel
import com.group16.mytrips.viewModel.ProfileViewModel

sealed class NavigationRoute {
    object ProfileScreen {
        val route = "ProfileScreen"
    }

    object NavigationScreen {
        val route = "NavigationScreen"
    }

    object CameraScreen {
        val route = "CameraScreen"
    }

    object DetailedSightScreen {
        val route = "DetailedSightScreen"
    }

    object DetailedProfileScreen {
        val route = "DetailedProfileScreen"
    }


}

@Composable
fun Navigation(
    navController: NavHostController,
    appViewModel: ApplicationViewModel,
    profileViewModel: ProfileViewModel,
    cameraViewModel: CameraViewModel

) {
    NavHost(navController = navController, startDestination = NavigationRoute.ProfileScreen.route) {
        composable(NavigationRoute.ProfileScreen.route) {
            PreviewHeader ({ sightId ->
                navController.navigate(NavigationRoute.DetailedSightScreen.route + "/ $sightId") {
                    popUpTo(NavigationRoute.ProfileScreen.route)
                }
            }, profileViewModel)
        }
        composable(NavigationRoute.NavigationScreen.route) {
            NavigationScreen(appViewModel) {
                navController.navigate(
                    NavigationRoute.NavigationScreen.route
                ) {popUpTo(NavigationRoute.ProfileScreen.route)}
            }
        }
        composable(NavigationRoute.CameraScreen.route) {
            CameraView(cameraViewModel)
        }
        composable(NavigationRoute.DetailedSightScreen.route + "/ {sightId}") { navBackStackEntry ->
            SightScreen(navBackStackEntry.arguments?.getString("sightId"), profileViewModel)
        }

    }

}