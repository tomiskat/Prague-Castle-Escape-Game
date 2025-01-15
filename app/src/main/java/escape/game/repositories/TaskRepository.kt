package escape.game.repositories

import android.content.Context
import escape.game.models.Task
import escape.game.utils.TaskLoader

class TaskRepository(private val context: Context) {

    fun loadTasks(): List<Task> {
        return TaskLoader.loadTasks(context)
    }
}
