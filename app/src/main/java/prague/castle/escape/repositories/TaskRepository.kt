package prague.castle.escape.repositories

import android.content.Context
import prague.castle.escape.models.Task
import prague.castle.escape.utils.TaskLoader

class TaskRepository(private val context: Context) {

    fun loadTasks(): List<Task> {
        return TaskLoader.loadTasks(context)
    }
}
