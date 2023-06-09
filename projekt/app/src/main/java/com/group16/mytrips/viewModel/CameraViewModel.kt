package com.group16.mytrips.viewModel

import android.app.Application
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.data.ModelClass
import com.group16.mytrips.data.SightFB
import com.group16.mytrips.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.math.roundToInt

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val diameter = 50



    private val storage = FirebaseStorage.getInstance()
    private val locationLiveData = LocationLiveData(application)
    private val modelClass = ModelClass()
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("SightFB")
    private val userRef = firestore.collection("User")
    private val userId = "7T9VijHoW9vQTKnLlDhp"
    private val defaultSightRef = firestore.collection("DefaultSightFB")

    private lateinit var listenerRegistrationUser: ListenerRegistration


    private var _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private var _defaultLocalSightList = MutableStateFlow(modelClass.defaultSightList)
    val defaultLocalSightList = _defaultLocalSightList.asStateFlow()

    private val _sightList = MutableStateFlow<MutableList<SightFB>>(emptyList<SightFB>().toMutableList())
    val sightList = _sightList.asStateFlow()

    fun uploadPicturesToFirebaseStorage(
        sightId: Int,
        imageUris: List<String>
    ) {
        val storageRef = storage.reference
        if (_sightList.value.none { it.sightId == sightId }) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val uploadTasks = imageUris.mapIndexed { index, uri ->
                        val imageRef = storageRef.child("images/${UUID.randomUUID()}_$index")
                        imageRef.putFile(Uri.parse(uri)).await()
                        imageRef.downloadUrl.await()
                    }

                    val downloadUrls = uploadTasks.map { task ->
                        task.toString()
                    }
                    Log.e("URL", downloadUrls[0])

                    val pictures = listOf(downloadUrls[0], downloadUrls[1])
                    uploadNewSight(sightId, pictures)
                } catch (e: Exception) {
                    Log.e("Firebase Storage", e.toString())
                }
            }
        }
    }

    fun uploadNewSight(sightId: Int, uris: List<String>) {
        val sight = _sightList.value.filter { it.sightId == sightId }
        if (sight.isEmpty()) {
            val defaultSight = _defaultSightList.value.first { it.sightId == sightId }
            val newSight = SightFB(
                sightId = sightId,
                picture = uris[0],
                thumbnail = uris[1],
                sightName = defaultSight.sightName,
                latitude = defaultSight.latitude,
                longitude = defaultSight.longitude,
                date = "30.05.2022"
            )
            collectionRef.add(newSight)
        }
    }

    fun startListeningForData() {
        startListeningForUser()
        startListeningForSightList()
        startListeningForDefaultSightList()
    }

    fun stopListeningForData() {
        stopListeningForUser()
        stopListeningForSightList()
        stopListeningForDefaultSightList()
    }

    fun startListeningForUser() {
        listenerRegistrationUser =
            userRef.document(userId).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val sight = snapshot?.toObject(User::class.java)

                _user.value = sight ?: User()
            }
    }

    fun stopListeningForUser() {
        listenerRegistrationUser.remove()
    }

    private val _defaultSightList =
        MutableStateFlow<MutableList<DefaultSightFB>>(emptyList<DefaultSightFB>().toMutableList())
    val defaultSightList = _defaultSightList.asStateFlow()

    private lateinit var listenerRegistration: ListenerRegistration

    fun startListeningForDefaultSightList() {
        listenerRegistration = defaultSightRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(DefaultSightFB::class.java)
            } ?: emptyList()

            _defaultSightList.value = sights.toMutableList()
        }
    }

    fun stopListeningForDefaultSightList() {
        listenerRegistration.remove()
    }


    private lateinit var listenerRegistrationSights: ListenerRegistration

    fun startListeningForSightList() {
        listenerRegistrationSights = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(SightFB::class.java)
            } ?: emptyList()

            _sightList.value = sights.toMutableList()
        }
    }

    fun stopListeningForSightList() {
        listenerRegistrationSights.remove()
    }

    fun getLocationLiveData() = locationLiveData
    fun startLocationUpdates() = locationLiveData.startLocationUpdates()
    private fun distanceInMeter(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Int {
        var results = FloatArray(1)
        Location.distanceBetween(startLat, startLon, endLat, endLon, results)
        return results[0].roundToInt()
    }

    fun updateXP(increment: Int) {
        val xp = "overallxp"
        val documentRef = userRef.document(userId)
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentValue = documentSnapshot.getLong(xp)
                    if (currentValue != null) {
                        val newValue = currentValue + increment
                        val updatedData = hashMapOf(
                            xp to newValue
                        )

                        documentRef.set(updatedData, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("Firebase", "XP Value Updated")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Following Exception occured $exception")
                            }
                    }
                } else {
                    Log.e("Firebase", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Following Exception occured $exception")
            }
    }

    fun getSortedList(): StateFlow<MutableList<DefaultSight>> {
        val observedLocationData = getLocationLiveData()
        val list = _defaultLocalSightList.asStateFlow()
        list.value.forEach {
            val l = observedLocationData
            val location = getLocationLiveData().value
            if (location == null) it.distance = null
            else
                it.distance = distanceInMeter(
                    location.latitude,
                    location.longitude,
                    it.latitude,
                    it.longitude
                )
        }
        //list.value.sortedByDescending { it.distance }
        list.value.sortBy { it.distance }
        list.value.retainAll { if (it.distance != null) it.distance!! <= diameter else false }
        return list
    }


}