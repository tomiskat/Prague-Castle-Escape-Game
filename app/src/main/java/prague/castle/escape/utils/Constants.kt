package prague.castle.escape.utils

object Constants {
    const val LOCATION_PERMISSION_REQUEST_CODE = 123
    const val LOCATION_INTERVAL_UPDATE = 5000L
    const val TASKS_LOCATION = "tasks"
    const val DISTANCE_TO_TASK = 100_000L
    const val INCORRECT_ANSWER_PENALTY = 5 * 60_000L
    const val EMPTY_STRING = ""
    const val INCORRECT_ANSWER_MESSAGE = "Incorrect, +5 min"
    const val NAME_ALREADY_EXISTS = "Selected name is already in use, please choose another one"
    const val DATABASE_ERROR = "There was an error storing the result, please try again"
    const val DATABASE_NAME = "results"
}