package com.yama.orbitcare.data.database

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.yama.orbitcare.data.models.*

class FirestoreDatabase {

    private val db = FirebaseFirestore.getInstance()

    //Hinzufügen eines Dokuments in eine Sammlung

    fun addOrganisation(organisation: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .add(organisation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun addEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Employee")
            .add(employee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun addClient(client: Client, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Client")
            .add(client)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun addEvent(event: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Event")
            .add(event)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    //Laden aller Dokumente einer Sammlung -> mit Callback

    fun getAllOrganisations(onComplete: (MutableList<Organisation>?) -> Unit) {
        db.collection("Organisation")
            .get()
            .addOnSuccessListener { result ->
                if(result != null) {
                    val list = mutableListOf<Organisation>()
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val organisation = document.toObject(Organisation::class.java)
                        list.add(organisation)
                    }
                    onComplete(list)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Fehler beim laden der Organisation-Sammlung.", e)
                onComplete(null)
            }
    }


    fun getAllClients(onComplete: (MutableList<Client>?) -> Unit) {
        db.collection("Client")
            .get()
            .addOnSuccessListener { result ->
                if(result != null) {
                    val list = mutableListOf<Client>()
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val client = document.toObject(Client::class.java)
                        list.add(client)
                    }
                    onComplete(list)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Fehler beim laden der Client-Sammlung.", e)
                onComplete(null)
            }
    }

    fun getAllEmployees(onComplete: (MutableList<Employee>?) -> Unit) {
        db.collection("Employee")
            .get()
            .addOnSuccessListener { result ->
                if(result != null) {
                    val list = mutableListOf<Employee>()
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val employee = document.toObject(Employee::class.java)
                        list.add(employee)
                    }
                    onComplete(list)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Fehler beim laden der Employee-Sammlung.", e)
                onComplete(null)
            }
    }

    fun getAllEvents(onComplete: (MutableList<Event>?) -> Unit) {
        db.collection("Event")
            .get()
            .addOnSuccessListener { result ->
                if(result != null) {
                    val list = mutableListOf<Event>()
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val event = document.toObject(Event::class.java)
                        list.add(event)
                    }
                    onComplete(list)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Fehler beim laden der Event-Sammlung.", e)
                onComplete(null)
            }
    }

    //Laden von Dokumente einer Sammlung mittels ID und Callback

    fun getOrganisation(documentId: String, onComplete: (Organisation?) -> Unit) {
        val doc = db.collection("Organisation").document(documentId)
            doc.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val organisation = document.toObject(Organisation::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        onComplete(organisation)
                    } else {
                        Log.d(TAG, "Kein Dokument mit dieser ID gefunden.")
                        onComplete(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Fehler beim Laden des Organisation-Dokuments.", e)
                    onComplete(null)
                }

    }

    fun getEmployee(documentId: String, onComplete: (Employee?) -> Unit) {
        val doc = db.collection("Employee").document(documentId)
        doc.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val employee = document.toObject(Employee::class.java)
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    onComplete(employee)
                } else {
                    Log.d(TAG, "Kein Dokument mit dieser ID gefunden.")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Fehler beim Laden des Employee-Dokuments.", e)
                onComplete(null)
            }
    }

    fun getClient(documentId: String, onComplete: (Client?) -> Unit) {
        val doc = db.collection("Client").document(documentId)
        doc.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val client = document.toObject(Client::class.java)
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    onComplete(client)
                } else {
                    Log.d(TAG, "Kein Dokument mit dieser ID gefunden.")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Fehler beim Laden des Client-Dokuments.", e)
                onComplete(null)
            }
    }

    fun getEvent(documentId: String, onComplete: (Event?) -> Unit) {
        val doc = db.collection("Event").document(documentId)
        doc.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val event = document.toObject(Event::class.java)
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    onComplete(event)
                } else {
                    Log.d(TAG, "Kein Dokument mit dieser ID gefunden.")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Fehler beim Laden des Event-Dokuments.", e)
                onComplete(null)
            }
    }

    //Ändern eines Dokuments mit der ID

    fun updateOrganisation(organisationID: String, updateOrganisation: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .document(organisationID)
            .set(updateOrganisation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateEmployee(employeeID: String, updateEmployee: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .document(employeeID)
            .set(updateEmployee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateClient(clientID: String, updateClient: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .document(clientID)
            .set(updateClient)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateEvent(eventID: String, updateEvent: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .document(eventID)
            .set(updateEvent)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}