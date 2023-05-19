package com.group16.mytrips.screens

import android.Manifest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.security.Permission

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionAlert(permissionTitle: String, rationale: String, permission: String) {

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
                    onClick = { permissionState.launchPermissionRequest() }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog = false}) {
                    Text(text = "Deny")
                }
            })
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