package prague.castle.escape.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

object FirebaseManager {

    fun storeResult(
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
            if (transaction.get(documentRef).exists()) {
                throw FirebaseFirestoreException(Constants.NAME_ALREADY_EXISTS, FirebaseFirestoreException.Code.ALREADY_EXISTS)
            }
            transaction.set(documentRef, value)
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
}
