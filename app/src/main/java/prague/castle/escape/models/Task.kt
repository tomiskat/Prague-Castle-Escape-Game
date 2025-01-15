package prague.castle.escape.models

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val correctAnswer: String,
    val location: LocationData,
)
