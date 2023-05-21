package com.group16.mytrips.screens

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.articlecamera.Cam.CameraView
import com.group16.mytrips.viewModel.ApplicationViewModel
import com.group16.mytrips.viewModel.MapViewModel
import com.group16.mytrips.viewModel.NavigationViewModel

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
    navViewModel: NavigationViewModel,
    requestPermission: (String, String, ActivityResultLauncher<String>) -> Unit,
    permissionLauncher: ActivityResultLauncher<String>,
    appViewModel: ApplicationViewModel
) {
    NavHost(navController = navController, startDestination = NavigationRoute.ProfileScreen.route) {
        composable(NavigationRoute.ProfileScreen.route) {
            PreviewHeader ({ sightId ->
                navController.navigate(NavigationRoute.DetailedSightScreen.route + "/ $sightId") {
                    popUpTo(NavigationRoute.ProfileScreen.route)
                }
            }, appViewModel)
        }
        composable(NavigationRoute.NavigationScreen.route) {
            NavigationScreen(navViewModel, appViewModel, requestPermission, permissionLauncher)
        }
        composable(NavigationRoute.CameraScreen.route) {
            CameraView(
                requestPermission = requestPermission,
                permissionLauncher = permissionLauncher
            )
        }
        composable(NavigationRoute.DetailedSightScreen.route + "/ {sightId}") { navBackStackEntry ->
            SightScreen(navBackStackEntry.arguments?.getString("sightId"), appViewModel)
        }

    }

}