package com.yama.orbitcare.data.database

import com.google.firebase.firestore.FirebaseFirestore
import com.yama.orbitcare.data.models.Organisation

class FirestoreDatabase {

    private val db = FirebaseFirestore.getInstance()

    fun addOrganisation(organisation: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .add(organisation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}