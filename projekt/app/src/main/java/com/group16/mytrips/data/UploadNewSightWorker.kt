package com.group16.mytrips.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.group16.mytrips.misc.toJson


class UploadNewSightWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

        val newPictures = inputData.getBoolean(KEY_NEW_PICTURES, false)
        val uriList = inputData.getStringArray(KEY_URI_LIST)
        val currentSightJson = inputData.getString(KEY_CURRENT_SIGHT)

        val currentSight = currentSightJson.let {
            val gson = Gson()
            gson.fromJson(it, DefaultSightFB::class.java)
        }

        try {
            if (!newPictures) {
                Firebase.uploadNewSight(null, null, currentSight)
            } else {
                val downloadUrls = uriList?.map { uri ->

                    Firebase.uploadImage(uri)
                }
                Firebase.uploadNewSight(
                    downloadUrls?.getOrNull(0),
                    downloadUrls?.getOrNull(1),
                    currentSight
                )
            }
        } catch (e: Exception) {
            Log.e("Firebase Storage", e.toString())
            return Result.failure()
        }

        return Result.success()
    }



    companion object {
        private const val KEY_NEW_PICTURES = "new_pictures"
        private const val KEY_CURRENT_SIGHT = "current_sight"
        private const val KEY_URI_LIST = "uri_List"

        fun createWorkRequest(
            newPictures: Boolean,
            currentSight: DefaultSightFB,
            uriList: List<String>
        ): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putBoolean(KEY_NEW_PICTURES, newPictures)
                .putString(KEY_CURRENT_SIGHT, toJson(currentSight))
                .putStringArray(KEY_URI_LIST, uriList.toTypedArray())
                .build()

            return OneTimeWorkRequestBuilder<UploadNewSightWorker>()
                .setInputData(inputData)
                .build()
        }
    }
}