package com.group16.mytrips.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.strictmode.UnbufferedIoViolation
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.security.Permission

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionAlert(
    permissionTitle: String,
    rationale: String,
    permission: String
) {

    val permissionState = rememberPermissionState(permission = permission)
    var openDialog by remember {
        mutableStateOf(true)
    }


    if (!permissionState.hasPermission && openDialog) {
        AlertDialog(onDismissRequest = {
            openDialog = false
        },
            title = { Text(text = permissionTitle) },
            text = { Text(text = rationale) },
            confirmButton = {
                Button(
                    onClick = { permissionState.launchPermissionRequest(); openDialog = false; }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog = false }) {
                    Text(text = "Deny")
                }
            })
    }

}

@Composable
fun LaunchPermission(
    permission: String,
    permissionTitle: String,
    rationale: String,
    requestPermission: (String, String, ActivityResultLauncher<String>) -> Unit,
    permissionLauncher: ActivityResultLauncher<String>
) {
    var openDialog by remember {
        mutableStateOf(true)
    }

    if (openDialog && (ContextCompat.checkSelfPermission(
            LocalContext.current,
            permission
        ) != PackageManager.PERMISSION_GRANTED)
    ) {
        AlertDialog(
            onDismissRequest = { openDialog = false }, title = { Text(text = permissionTitle) },
            text = { Text(text = rationale) },
            confirmButton = {
                Button(onClick = {
                    requestPermission(permission, permissionTitle, permissionLauncher)
                    openDialog = false;

                }) {
                    Text(text = "Ok")
                }
            }, dismissButton = {
                Button(onClick = { openDialog = false }) {
                    Text(text = "Nein")
                }
            }
        )
    }
}

@Preview
@Composable
fun PreViewPermission() {
    PermissionAlert(
        permissionTitle = "Location",
        rationale = "Location is needed for Navigation!",
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )


}