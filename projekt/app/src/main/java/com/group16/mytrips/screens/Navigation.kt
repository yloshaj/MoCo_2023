package com.group16.mytrips.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.articlecamera.Cam.CameraView
import com.group16.mytrips.viewModel.CameraViewModel
import com.group16.mytrips.viewModel.LoginViewModel
import com.group16.mytrips.viewModel.NavigationViewModel
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

    object LoginScreen {
        val route = "LoginScreen"
    }
    
}

@Composable
fun Navigation(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
    profileViewModel: ProfileViewModel,
    cameraViewModel: CameraViewModel,
    loginViewModel: LoginViewModel,
    showBottomBar: MutableState<Boolean>

) {
    NavHost(navController = navController, startDestination = NavigationRoute.LoginScreen.route) {
        composable(NavigationRoute.ProfileScreen.route) {
            PreviewHeader ({ sightId ->
                navController.navigate(NavigationRoute.DetailedSightScreen.route + "/ $sightId") {
                    popUpTo(NavigationRoute.ProfileScreen.route)
                }
            }, profileViewModel)
        }
        composable(NavigationRoute.NavigationScreen.route) {
            NavigationScreen(navigationViewModel) {
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
        composable(NavigationRoute.LoginScreen.route) {
            Login(viewModel = loginViewModel, showBottomBar) {
                navController.navigate(
                    NavigationRoute.ProfileScreen.route
                ) {popUpTo(NavigationRoute.ProfileScreen.route)}
            }
        }

    }

}