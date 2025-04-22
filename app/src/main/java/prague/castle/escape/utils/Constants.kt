package prague.castle.escape.utils

object Constants {
    const val LOCATION_PERMISSION_REQUEST_CODE = 123
    const val LOCATION_INTERVAL_UPDATE = 5000L
    const val TASKS_LOCATION = "tasks"
    const val DISTANCE_TO_TASK = 10L
    const val INCORRECT_ANSWER_PENALTY = 5 * 60_000L
    const val EMPTY_STRING = ""
    const val INCORRECT_ANSWER_MESSAGE = "Incorrect, +5 min"
    const val DATABASE_NAME = "results"

    // User-facing error messages
    const val NAME_ALREADY_EXISTS = "The selected name is already in use. Please choose another one."
    const val NO_INTERNET_CONNECTION = "No internet connection available. Please check your connection."
    const val UNSPECIFIED_STORAGE_ERROR = "There was an error storing the result. Please try again."

    // Firebase error messages
    const val FIREBASE_ERROR_NAME_ALREADY_EXISTS = "PERMISSION_DENIED: Missing or insufficient permissions."
    const val FIREBASE_ERROR_NO_INTERNET_CONNECTION = "UNAVAILABLE: Unable to resolve host firestore.googleapis.com"

    // Mapping
    val FIREBASE_ERROR_TO_USER_MESSAGE  = mapOf(
        FIREBASE_ERROR_NAME_ALREADY_EXISTS to NAME_ALREADY_EXISTS,
        FIREBASE_ERROR_NO_INTERNET_CONNECTION to NO_INTERNET_CONNECTION,
    )
}