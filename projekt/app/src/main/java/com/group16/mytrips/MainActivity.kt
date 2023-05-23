package com.group16.mytrips

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.group16.mytrips.data.BottomNavigationItem
import com.group16.mytrips.screens.BottomNavigationBar
import com.group16.mytrips.screens.Navigation
import com.group16.mytrips.screens.NavigationRoute

import com.group16.mytrips.ui.theme.MyTripsTheme
import com.group16.mytrips.viewModel.ApplicationViewModel
import com.group16.mytrips.viewModel.CameraViewModel
import com.group16.mytrips.viewModel.ProfileViewModel


class MainActivity : ComponentActivity() {



        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) Log.i("ja", "Wurde erlaubt")
            else Log.i("ja", "Nicht erlaubt")
        }

        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @OptIn(ExperimentalMaterial3Api::class)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                val profileViewModel = ProfileViewModel()
                val cameraViewModel = CameraViewModel(this.application)
                val appViewModel = ApplicationViewModel(this.application)


                MyTripsTheme {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(
                                items = listOf(
                                    BottomNavigationItem(
                                        name = "Navigation",
                                        route = NavigationRoute.NavigationScreen.route,
                                        icon = Icons.Rounded.Place),
                                    BottomNavigationItem(
                                        name = "Profil",
                                        route = NavigationRoute.ProfileScreen.route,
                                        icon = Icons.Rounded.Person),
                                    BottomNavigationItem(
                                        name = "Kamera",
                                        route = NavigationRoute.CameraScreen.route,
                                        icon = ImageVector.vectorResource(id = R.drawable.ic_photo_camera))
                                ),
                                navController = navController,
                                onItemClick = {
                                    navController.navigate(it.route) {
                                        popUpTo(NavigationRoute.ProfileScreen.route)
                                    }
                                })
                        }
                    ) {
                        innerPadding ->
                        Box(modifier = Modifier
                            .padding(PaddingValues(0.dp,0.dp,0.dp,innerPadding.calculateBottomPadding()))) {
                            Navigation(navController = navController, appViewModel, profileViewModel)
                        }
                    }

                }

            }

        }

    private fun requestPermission(permission: String, title: String, activityResult: ActivityResultLauncher<String>) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            )
                    == PackageManager.PERMISSION_GRANTED -> {
                Log.i("ja", "$title zugriff bereits erlaubt")
                //shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            )
            -> Log.i("ja", "Zeige $title Permission Text")

            else -> activityResult.launch(permission)
        }
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyTripsTheme {
        //SightColumn()

    }
}