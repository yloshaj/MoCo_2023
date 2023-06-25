package com.group16.mytrips.data

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.group16.mytrips.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.group16.mytrips.misc.getDate
import com.group16.mytrips.misc.sortByDate
import kotlinx.coroutines.tasks.await
import java.util.UUID

object Firebase {
    private val storage = FirebaseStorage.getInstance()
    private var userId: String = "7T9VijHoW9vQTKnLlDhp"

    fun setUserId(id: String) {
        userId = id
    }
    val likeIcon = "ic_liked_pin"
    private val dataId = "lHHoUXSufghEL9ln6l77"

    suspend fun uploadImage(uri: String): String {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(Uri.parse(uri))
        uploadTask.await()
        return imageRef.downloadUrl.await().toString()
    }

    private fun getFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun uploadNewSight(picture: String?, thumbnail: String?, defaultSight: DefaultSightFB) {

        val newSight = SightFB(
            sightId = defaultSight.sightId,
            userId = userId,
            picture = picture ?: defaultSight.defualtPicture,
            thumbnail = thumbnail ?: defaultSight.thumbnail,
            sightName = defaultSight.sightName,
            latitude = defaultSight.latitude,
            longitude = defaultSight.longitude,
            date = getDate(),
            pin = defaultSight.pin
        )
        var xp = 30
        if (defaultSight.pin  == "ic_special_pin") xp =  50
        addSight(newSight)
        updateXP(xp)

    }

    fun addSight(sight: SightFB) {
        val firestore = getFirestoreInstance()
        val collectionRef = firestore.collection("SightFB")
        collectionRef.add(sight)
    }

    fun updateIcon(sight: SightFB) {
        val firestore = getFirestoreInstance()
        val sightRef = firestore.collection("SightFB")
        val sightQuery = sightRef.whereEqualTo("sightId", sight.sightId)
        var value = "ic_liked_pin"

        if (sight.pin == value) value = "ic_standard_pin"
        sightQuery.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference
                        .update("pin", value)
                        .addOnSuccessListener {
                            println("Pin value updated successfully!")
                        }
                        .addOnFailureListener { e ->
                            println("Error updating pin value: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error retrieving document: $e")
            }
    }


    fun updateXP(increment: Int) {
        val firestore = getFirestoreInstance()
        val userRef = firestore.collection("User")
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
                                Log.d("com.group16.mytrips.data.Firebase", "XP Value Updated")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("com.group16.mytrips.data.Firebase", "Following Exception occurred $exception")
                            }
                    }
                } else {
                    Log.e("com.group16.mytrips.data.Firebase", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("com.group16.mytrips.data.Firebase", "Following Exception occurred $exception")
            }
    }

    fun updateAvatar(localPath: Int) {
        val firestore = getFirestoreInstance()
        val userRef = firestore.collection("User")
        val avatar = "avatar"
        val documentRef = userRef.document(userId)
        val data = hashMapOf(
            avatar to localPath
        )
        documentRef.set(data, SetOptions.merge())
            .addOnSuccessListener { Log.d("com.group16.mytrips.data.Firebase", "Avatar uploaded") }
            .addOnFailureListener { Log.e("com.group16.mytrips.data.Firebase", "$it") }
    }

    private fun Query.asFlow(): Flow<List<DefaultSightFB>> = callbackFlow {
        val snapshotListener = addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(DefaultSightFB::class.java)
            } ?: emptyList()

            try {
                trySend(sights).isSuccess
            } catch (e: Exception) {
                Log.e("Firestore Query", "$e")
            }
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun searchDefaultSights(query: String): Flow<List<DefaultSightFB>> {
        val firestore = getFirestoreInstance()
        val collectionRef = firestore.collection("DefaultSightFB")


        val searchQuery = if (query.isBlank()) {

            collectionRef.whereEqualTo("sightName", "")
        } else {
            collectionRef.whereGreaterThanOrEqualTo("sightName", query)
                .whereLessThanOrEqualTo("sightName", query + "\uF7FF")
        }

        return searchQuery.asFlow()
    }


    fun startListeningForUserList(
        listener: (List<User>) -> Unit
    ): ListenerRegistration {
        val firestore = getFirestoreInstance()
        val userRef = firestore.collection("User")
        return userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val user = snapshot?.documents?.mapNotNull { document ->
                document.toObject(User::class.java)
            } ?: emptyList()

            listener(user)
        }
    }

    fun startListeningForAvatarList(
        listener: (List<Avatar>) -> Unit
    ): ListenerRegistration {
        val firestore = getFirestoreInstance()
        val avatarRef = firestore.collection("avatar")

        val userListener = startListeningForUser { user ->
            avatarRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val avatarList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Avatar::class.java)
                } ?: emptyList()

                // Filter the avatar list based on user XP
                val filteredAvatarList = avatarList.filter { avatar ->
                    avatar.level  <= user.overallxp / 100
                }

                listener(filteredAvatarList)
            }
        }

        return userListener
    }


    fun startListeningForUser(
        listener: (User) -> Unit
    ): ListenerRegistration {
        val firestore = getFirestoreInstance()
        val userRef = firestore.collection("User")
        return userRef.document(userId).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                return@addSnapshotListener
            }

            val user = snapshot?.toObject(User::class.java)

            listener(user ?: User())
        }
    }

    fun startListeningForRadius(
        listener: (Int) -> Unit
    ): ListenerRegistration {
        val firestore = getFirestoreInstance()
        val dataRef = firestore.collection("DefaultSightInfo")
        return dataRef.document(dataId).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                return@addSnapshotListener
            }

            val radius = snapshot?.getLong("radius")?.toInt() ?:  0
            listener(radius)
        }
    }

    fun startListeningForDefaultSightList(
        listener: (List<DefaultSightFB>) -> Unit
    ): ListenerRegistration {
        val firestore = getFirestoreInstance()
        val defaultSightRef = firestore.collection("DefaultSightFB")

        val sightListener = startListeningForSightList {sightList->
            defaultSightRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val sights = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(DefaultSightFB::class.java)
                } ?: emptyList()
                for (sight in sights) {
                    if (sightList.any { it.sightId == sight.sightId }){
                        sight.visited = true
                        val lsight = sightList.first { it.sightId == sight.sightId }
                        if (lsight.pin == "ic_liked_pin") sight.pin = "ic_liked_pin"
                    }

                }
                listener(sights)
            }
        }
        return sightListener
    }


    fun startListeningForSightList(
        listener: (List<SightFB>) -> Unit
    ): ListenerRegistration {
        val firestore = getFirestoreInstance()
        val collectionRef = firestore.collection("SightFB")
        return collectionRef.whereEqualTo("userId", userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(SightFB::class.java)
            } ?: emptyList()

            listener(sortByDate(sights))
        }
    }


}