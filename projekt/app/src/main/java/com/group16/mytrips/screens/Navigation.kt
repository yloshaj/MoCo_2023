package com.group16.mytrips.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class NavigationRoute {
    object ProfileScreen {val route = "ProfileScreen"}
    object NavigationScreen {val route = "NavigationScreen"}
    object CameraScreen {val route = "CameraScreen"}

}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationRoute.ProfileScreen.route) {
        composable(NavigationRoute.ProfileScreen.route) {
            PreviewHeader()
        }
        composable(NavigationRoute.NavigationScreen.route) {
            NavigationScreen()
        }
        composable(NavigationRoute.CameraScreen.route) {
            CamScreen()
        }
    }
    
}