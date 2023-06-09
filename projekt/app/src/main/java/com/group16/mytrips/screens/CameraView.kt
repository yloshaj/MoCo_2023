package com.example.articlecamera.Cam

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FlipCameraAndroid
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material.icons.sharp.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.group16.mytrips.R

import com.group16.mytrips.data.CameraUIAction
import com.group16.mytrips.data.getCameraProvider
import com.group16.mytrips.data.getOutputDirectory
import com.group16.mytrips.data.takePicture
import com.group16.mytrips.screens.LaunchPermission
import com.group16.mytrips.viewModel.CameraViewModel
import java.io.IOException

@Composable
fun CameraView(
    viewModel: CameraViewModel,
    onImageCaptured: (Uri, Uri, Boolean) -> Unit = { originalUri, croppedUri, fromGalery ->
        Log.d(ContentValues.TAG, "Oben")
    },
    onError: (ImageCaptureException) -> Unit = {
        Log.e("FehlerCapture", "Zeige Fehler", it)
    }
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.startListeningForData()
    }
    LaunchPermission(
        permission = android.Manifest.permission.CAMERA,
        permissionTitle = "Kamera",
        rationale = "Um Bilder zu machen, brauchen wir die Kamera!"
    )


    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val croppedUri = createCroppedImage(
                uri,
                context
            )
            onImageCaptured(uri, croppedUri, true)
            //viewModel.uploadPicturesToFirebaseStorage(8,listOf(uri.toString(),croppedUri.toString()))
        }
    }

    CameraPreviewView(
        viewModel,
        imageCapture,
        lensFacing
    ) { cameraUIAction ->
        when (cameraUIAction) {
            is CameraUIAction.OnCameraClick -> {
                imageCapture.takePicture(
                    context,
                    lensFacing,
                    { originalUri, true1 ->
                        val croppedUri = createCroppedImage(originalUri, context)
                        viewModel.uploadPicturesToFirebaseStorage(8,listOf(originalUri.toString(),croppedUri.toString()))
                    }, onError)
            }

            is CameraUIAction.OnSwitchCameraClick -> {
                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            }

            is CameraUIAction.OnGalleryViewClick -> {
                if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                    galleryLauncher.launch("image/*")
                }
            }
        }
    }
}



@Composable
private fun CameraPreviewView(viewModel: CameraViewModel,
                              imageCapture: ImageCapture,
                              lensFacing: Int = CameraSelector.LENS_FACING_BACK,
                              cameraUIAction: (CameraUIAction) -> Unit
) {

    //val locState = viewModel.sightList.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val previewView = remember { PreviewView(context) }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize()) {

        }
        /*Column(modifier = Modifier.align(Alignment.TopCenter)) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = locState.value[0].sightName,
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        } */

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            CameraControls(cameraUIAction)
        }
    }
}

@Composable
fun CameraControls(cameraUIAction: (CameraUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {


        CameraControl(
            Icons.Sharp.PhotoLibrary,
            R.string.icn_camera_view_view_gallery_content_description,
            modifier = Modifier.size(44.dp),
            onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) }
        )

        CameraControl(
            Icons.Sharp.Lens,
            R.string.icn_camera_view_camera_shutter_content_description,
            modifier = Modifier
                .size(64.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape),
            onClick = { cameraUIAction(CameraUIAction.OnCameraClick) }
        )

        CameraControl(
            Icons.Sharp.FlipCameraAndroid,
            R.string.icn_camera_view_switch_camera_content_description,
            modifier = Modifier.size(44.dp),
            onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) }
        )

    }
}


@Composable
fun CameraControl(
    imageVector: ImageVector,
    contentDescId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            contentDescription = stringResource(id = contentDescId),
            modifier = modifier,
            tint = Color.White
        )
    }
}

private fun handleImageCapture() {
    Log.i("ja", "Bild Gemacht:")
}
private fun createCroppedImage(
    originalUri: Uri,
    context: Context,
): Uri {
    val originalBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, originalUri)

    // Get the rotation angle from the image's Exif data
    val rotationAngle = getImageRotationAngle(originalUri, context)

    // Apply rotation to the original bitmap if necessary
    val rotatedBitmap = rotateBitmap(originalBitmap, rotationAngle)

    val croppedBitmap = Bitmap.createBitmap(156, 118, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(croppedBitmap)
    val scaleFactorX = croppedBitmap.width.toFloat() / rotatedBitmap.width
    val scaleFactorY = croppedBitmap.height.toFloat() / rotatedBitmap.height
    val scaleFactor = scaleFactorX.coerceAtLeast(scaleFactorY)
    val scaledWidth = rotatedBitmap.width * scaleFactor
    val scaledHeight = rotatedBitmap.height * scaleFactor
    val offsetX = (croppedBitmap.width - scaledWidth) / 2
    val offsetY = (croppedBitmap.height - scaledHeight) / 2
    val scaleMatrix = Matrix().apply {
        postScale(scaleFactor, scaleFactor)
        postTranslate(offsetX, offsetY)
    }
    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }
    canvas.drawBitmap(rotatedBitmap, scaleMatrix, paint)
    val croppedUri = saveBitmapToMediaStore(croppedBitmap, context)
    return croppedUri
}

private fun getImageRotationAngle(uri: Uri, context: Context): Int {
    var rotationAngle = 0
    try {
        val inputStream = context.contentResolver.openInputStream(uri)?:throw Exception("FehlerUnten")
        val exifInterface = ExifInterface(inputStream)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        inputStream?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return rotationAngle
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees.toFloat())
    return Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
}

private fun saveBitmapToMediaStore(bitmap: Bitmap, context: Context): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "cropped_image")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
    uri?.let { safeUri ->
        contentResolver.openOutputStream(safeUri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }
    }
    return uri ?: throw IOException("Failed to save cropped image")
}







