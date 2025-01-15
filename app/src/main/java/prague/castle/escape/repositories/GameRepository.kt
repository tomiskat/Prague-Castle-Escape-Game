package prague.castle.escape.repositories

import android.content.Context
import prague.castle.escape.utils.GameManager

class GameRepository(private val context: Context) {

    fun getTaskId(): Int = GameManager.getTaskId(context)
    fun saveTaskId(taskId: Int) = GameManager.saveTaskId(context, taskId)

    fun getGameStartTime(): Long = GameManager.getGameStartTime(context)
    fun saveGameStartTime(startTime: Long) = GameManager.saveGameStartTime(context, startTime)

    fun getGameEndTime(): Long = GameManager.getGameEndTime(context)
    fun saveGameEndTime(endTime: Long) = GameManager.saveGameEndTime(context, endTime)

    fun getWrongAnswersCount(): Int = GameManager.getWrongAnswersCount(context)
    fun saveWrongAnswersCount(count: Int) = GameManager.saveWrongAnswersCount(context, count)
}
