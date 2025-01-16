package prague.castle.escape.utils

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    fun saveResult(
        key: String,
        value: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Get the Firebase instance
        val firebase = FirebaseFirestore.getInstance()
        val collection = firebase.collection(Constants.DATABASE_NAME)

        // Start a transaction
        firebase.runTransaction { transaction ->
            val documentRef = collection.document(key)
            transaction.set(documentRef, value)
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
}
