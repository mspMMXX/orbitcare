package com.yama.orbitcare.data.database

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.yama.orbitcare.data.models.*

class FirestoreDatabase {

    // Firebase Firestore instance for database operations
    private val db = FirebaseFirestore.getInstance()

    // ---------- Add Documents to Firestore ----------

    // Adds an Organisation object to the Organisation Collection
    fun addOrganisation(organisation: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .add(organisation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Adds an Employee object to the Employee Collection
    fun addEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Employee")
            .add(employee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Adds an Client object to the Client Collection
    fun addClient(client: Client, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Client")
            .add(client)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Adds an Event object to the Event Collection
    fun addEvent(event: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Event")
            .document(event.id)
            .set(event)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // ---------- Get All Documents from Firestore ----------

    /**
     * Retrieves all Organisation documents from the Organisation collection.
     * Calls the onComplete callback with the list of Organisations or null failure.
     */
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


    /**
     * Retrieves all Client documents from the Client collection.
     * Calls the onComplete callback with the list of Clients or null failure.
     */
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

    /**
     * Retrieves all Employee documents from the Employee collection.
     * Calls the onComplete callback with the list of Employees or null failure.
     */
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

    /**
     * Retrieves all Event documents from the Event collection.
     * Calls the onComplete callback with the list of Events or null failure.
     */
    fun getAllEvents(onComplete: (MutableList<Event>?) -> Unit) {
        db.collection("Event")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Event>()
                for (document in result) {
                    try {
                        val event = document.toObject(Event::class.java)
                        Log.d("Debugg", "Event: ${event.title} wurde geladen")
                        list.add(event)
                    } catch (e: Exception) {
                        Log.d("Debugg", "Convert Failure Doc")
                    }
                }
                onComplete(list)
            }
            .addOnFailureListener { e ->
                Log.d("Debugg", "Load Events failure")
                onComplete(null)
            }
    }

    // ---------- Get Document by ID from Firestore ----------

    /**
     * Retrieves a specific Organisation document by its ID.
     * Calls the onComplete callback with the Organisation object or null if not found.
     */
    fun getOrganisation(documentId: String, onComplete: (Organisation?) -> Unit) {
        db.collection("Organisation").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val organisation = document.toObject(Organisation::class.java)
                    onComplete(organisation)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Organisation", "Loading organisation-doc failed", e)
                onComplete(null)
            }

    }

    /**
     * Retrieves a specific Employee document by its ID.
     * Calls the onComplete callback with the Employee object or null if not found.
     */
    fun getEmployee(documentId: String, onComplete: (Employee?) -> Unit) {
        db.collection("Employee").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val employee = document.toObject(Employee::class.java)
                    onComplete(employee)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Employee", "Loading employee-doc failed.", e)
                onComplete(null)
            }
    }

    /**
     * Retrieves a specific Vlient document by its ID.
     * Calls the onComplete callback with the Client object or null if not found.
     */
    fun getClient(documentId: String, onComplete: (Client?) -> Unit) {
        db.collection("Client").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val client = document.toObject(Client::class.java)
                    onComplete(client)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Client", "Loading client-doc failed.", e)
                onComplete(null)
            }
    }

    /**
     * Retrieves a specific Event document by its ID.
     * Calls the onComplete callback with the Event object or null if not found.
     */
    fun getEvent(documentId: String, onComplete: (Event?) -> Unit) {
        db.collection("Event").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val event = document.toObject(Event::class.java)
                    onComplete(event)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Event", "Loading event-doc failed.", e)
                onComplete(null)
            }
    }

    // ---------- Get Document by Field Value from Firestore ----------

    /**
     * Retrieves the first Organisation document that matches a specific field and value.
     * Calls the onComplete callback with the Organisation object or null if not found.
     */
    fun getOrganisationWithFieldValue(fieldName: String, fieldValue: String, onComplete: (Organisation?) -> Unit) {
        db.collection("Organisation")
            .whereEqualTo(fieldName, fieldValue)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val documentMatch = document.documents.firstOrNull {
                        it.getString(fieldName) == fieldValue
                    }
                    if (documentMatch != null) {
                        val organisation = document.documents.first().toObject(Organisation::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.documents.first().data}")
                        onComplete(organisation)
                    } else {
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Organisation", "Loading organisation failed", e)
                onComplete(null)
            }
    }

    /**
     * Retrieves the first Employee document that matches a specific field and value.
     * Calls the onComplete callback with the Employee object or null if not found.
     */
    fun getEmployeeWithFieldValue(fieldName: String, fieldValue: String, onComplete: (Employee?) -> Unit) {
        db.collection("Employee")
            .whereEqualTo(fieldName, fieldValue)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val documentMatch = document.documents.firstOrNull {
                        it.getString(fieldName) == fieldValue
                    }
                    if (documentMatch != null) {
                        val employee = document.documents.first().toObject(Employee::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.documents.first().data}")
                        onComplete(employee)
                    } else {
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Employee", "Loading Employee failed.", e)
                onComplete(null)
            }
    }

    /**
     * Retrieves the first Client document that matches a specific field and value.
     * Calls the onComplete callback with the Client object or null if not found.
     */
    fun getClientWithFieldValue(fieldName: String, fieldValue: String, onComplete: (Client?) -> Unit) {
        db.collection("Client")
            .whereEqualTo(fieldName, fieldValue)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val documentMatch = document.documents.firstOrNull {
                        it.getString(fieldName) == fieldValue
                    }
                    if (documentMatch != null) {
                        val client = document.documents.first().toObject(Client::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.documents.first().data}")
                        onComplete(client)
                    } else {
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Client", "Loading client failed.", e)
                onComplete(null)
            }
    }

    /**
     * Retrieves the first Event document that matches a specific field and value.
     * Calls the onComplete callback with the Event object or null if not found.
     */
    fun getEventWithFieldValue(fieldName: String, fieldValue: String, onComplete: (Event?) -> Unit) {
        db.collection("Event")
            .whereEqualTo(fieldName, fieldValue)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val documentMatch = document.documents.firstOrNull {
                        it.getString(fieldName) == fieldValue
                    }
                    if (documentMatch != null) {
                        val event = document.documents.first().toObject(Event::class.java)
                        Log.d(TAG, "DocumentSnapshot data: ${document.documents.first().data}")
                        onComplete(event)
                    } else {
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Event", "Loading event failed.", e)
                onComplete(null)
            }
    }

    // ---------- Update Documents in Firestore ----------

    /**
     * Updates an Organisation document by its ID with the new Organisation data.
     */
    fun updateOrganisation(organisationID: String, updateOrganisation: Organisation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Organisation")
            .document(organisationID)
            .set(updateOrganisation)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    /**
     * Updates an Employee document by its ID with the new Employee data.
     */
    fun updateEmployee(employeeID: String, updateEmployee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Employee")
            .document(employeeID)
            .set(updateEmployee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    /**
     * Updates an Client document by its ID with the new Client data.
     */
    fun updateClient(clientID: String, updateClient: Client, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Client")
            .document(clientID)
            .set(updateClient)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    /**
     * Updates an Event document by its ID with the new Event data.
     */
    fun updateEvent(id: String, updateEvent: Event, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Event")
            .document(id)
            .set(updateEvent, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // ---------- Delete Documents from Firestore ----------

    /**
     * Deletes an Event document by its ID.
     */
    fun deleteEvent(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Event")
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}