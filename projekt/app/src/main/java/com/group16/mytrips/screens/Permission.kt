package com.group16.mytrips.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.strictmode.UnbufferedIoViolation
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.android.awaitFrame
import okhttp3.internal.wait
import java.security.Permission



@Composable
fun LaunchPermission(
    permission: String,
    permissionTitle: String,
    rationale: String,
    navigate: () -> Unit = {}
) {
    var openDialog by remember {
        mutableStateOf(true)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) {
            isGranted -> if (isGranted) {
        Log.i("ja", "Wurde erlaubt")
        navigate()
        openDialog = false
    }
    else Log.i("ja", "Nicht erlaubt")
    }
    val activity = LocalContext.current as Activity

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
                    openDialog = false;
                    requestPermissionNav(permission,permissionTitle,launcher, activity)


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


private fun requestPermissionNav(permission: String, title: String, activityResult: ActivityResultLauncher<String>, activity: Activity) {
    when {
        ContextCompat.checkSelfPermission(
            activity,
            permission
        )
                == PackageManager.PERMISSION_GRANTED -> {
            Log.i("ja", "$title zugriff bereits erlaubt")
            //shouldShowCamera.value = true
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permission
        )
        -> Log.i("ja", "Zeige $title Permission Text")

        else -> activityResult.launch(permission)
    }
}

