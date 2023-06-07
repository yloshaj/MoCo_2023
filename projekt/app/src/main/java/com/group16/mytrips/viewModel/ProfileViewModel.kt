package com.group16.mytrips.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.group16.mytrips.data.ModelClass
import com.group16.mytrips.data.SightFB
import com.group16.mytrips.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel: ViewModel() {
    val modelClass = ModelClass()

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("SightFB")
    private val userRef = firestore.collection("User")
    private val userId = "7T9VijHoW9vQTKnLlDhp"
    private lateinit var listenerRegistrationUser: ListenerRegistration


    private var _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    fun startListeningForData() {
        startListeningForUser()
        startListeningForSightList()
    }
    fun stopListeningForData() {
        stopListeningForUser()
        stopListeningForSightList()
    }
    fun startListeningForUser() {
        listenerRegistrationUser = userRef.document(userId).addSnapshotListener { snapshot, exception ->
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
    fun updateAvatar(localPath: Int) {
        val documentRef = userRef.document(userId)
        val data = hashMapOf(
            "avatar" to localPath
        )
        documentRef.set(data, SetOptions.merge())
            .addOnSuccessListener { Log.d("Firebase", "Avatar uploaded") }
            .addOnFailureListener { Log.e("Firebase", "$it") }
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

    private val _sightList = MutableStateFlow<List<SightFB>>(emptyList())
    val sightList = _sightList.asStateFlow()

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

            _sightList.value = sights
        }
    }

    fun stopListeningForSightList() {
        listenerRegistrationSights.remove()
    }


    private var _avatar = MutableStateFlow(modelClass.listOfAvatars)
    val avatar = _avatar.asStateFlow()


}