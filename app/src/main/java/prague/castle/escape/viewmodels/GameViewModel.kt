package prague.castle.escape.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import prague.castle.escape.managers.LocationManagerImpl
import prague.castle.escape.models.LocationData
import prague.castle.escape.models.Task
import prague.castle.escape.repositories.GameRepository
import prague.castle.escape.repositories.TaskRepository
import prague.castle.escape.utils.Constants
import prague.castle.escape.utils.FirebaseManager

class GameViewModel(
    private val taskRepository: TaskRepository,
    private val gameRepository: GameRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {


    private lateinit var _tasks : List<Task>
    private lateinit var _locationManagerImpl: LocationManagerImpl

    private var _taskID = 0
    private var _gameStartTime = 0L
    private var _gameEndTime = 0L
    private var _wrongAnswersCount = 0

    private val _task = MutableLiveData<Task>()
    private val _distance = MutableLiveData<Float>()
    private val _toast = MutableLiveData<String>()
    private val _storingError = MutableLiveData<String>()
    val task: LiveData<Task> = _task
    val distance: LiveData<Float> = _distance
    val toast: LiveData<String> = _toast
    val storingError: LiveData<String> = _storingError

    init {
        loadTasks()
        loadGame()
        loadLocationManager()
    }

    private fun updateDistance(location: Location) {
        val task = _task.value
        if (task != null) {
            val distanceInMeters = location.distanceTo(Location("Task").apply {
                latitude = task.location.latitude
                longitude = task.location.longitude
            })
            _distance.value = distanceInMeters
        }
    }

    private fun incrementWrongAnswersCount() {
        gameRepository.saveWrongAnswersCount(++_wrongAnswersCount)
    }

    private fun loadTasks() {
        _tasks = taskRepository.loadTasks()
    }

    private fun loadGame() {
        _taskID = gameRepository.getTaskId()
        _gameStartTime = gameRepository.getGameStartTime()
        _gameEndTime = gameRepository.getGameEndTime()
        _wrongAnswersCount = gameRepository.getWrongAnswersCount()
        _task.value = _tasks[_taskID]
    }

    fun startLocationUpdates() {
        _locationManagerImpl.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        _locationManagerImpl.stopLocationUpdates()
    }

    private fun loadLocationManager() {
        _locationManagerImpl = LocationManagerImpl(fusedLocationClient) { location -> updateDistance(location) }
    }

    fun checkAnswer(playerAnswer: String) {
        if (playerAnswer.equals(_task.value?.correctAnswer, ignoreCase = true)) {
            nextTask()
            return
        }
        incrementWrongAnswersCount()
        showIncorrectAnswerToast()
    }

    private fun showIncorrectAnswerToast() {
        _toast.postValue(Constants.INCORRECT_ANSWER_MESSAGE)
    }

    fun clearToast() {
        _toast.postValue(Constants.EMPTY_STRING)
    }

    fun nextTask() {
        if (_taskID == 0) { startGame() }
        if (_taskID == 23) { endGame() }
        gameRepository.saveTaskId(++_taskID)
        _task.value = _tasks[_taskID]
    }

    private fun startGame() {
        _gameStartTime = System.currentTimeMillis()
        gameRepository.saveGameStartTime(_gameStartTime)
    }

    private fun endGame() {
        _gameEndTime = System.currentTimeMillis()
        gameRepository.saveGameEndTime(_gameEndTime)
    }

    private fun getGameDuration(): Long {
        val time = _gameEndTime - _gameStartTime
        val penalty = _wrongAnswersCount * Constants.INCORRECT_ANSWER_PENALTY
        return (time + penalty) / 1000
    }

    fun isLocationTask(): Boolean {
        return _task.value?.location != LocationData(0.0, 0.0)
    }

    fun getGameDurationText(): CharSequence {
        assert(_gameEndTime != 0L)
        val duration = getGameDuration()
        val minutes = duration / 60
        val seconds = (duration % 60)
        return "Duration: $minutes min $seconds sec"
    }


    private fun getGameData(): Map<String, Any> {
        return mapOf("duration" to getGameDuration())
    }

    private fun onStoringSuccess() {
        _toast.postValue("Result stored")
        nextTask()
    }

    private fun onStoringFailure(exception: Exception) {
        val errorMessage = Constants.FIREBASE_ERROR_TO_USER_MESSAGE[exception.message] ?: Constants.UNSPECIFIED_STORAGE_ERROR
        _storingError.postValue(errorMessage)
    }

    fun saveResult(teamName: String) {
        FirebaseManager.saveResult(teamName, getGameData(), ::onStoringSuccess, ::onStoringFailure)
    }

    fun clearStoringError() {
        _storingError.postValue(Constants.EMPTY_STRING)
    }
}