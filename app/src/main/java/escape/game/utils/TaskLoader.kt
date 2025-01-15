package escape.game.utils

import android.content.Context
import escape.game.models.Task
import escape.game.utils.Constants.TASKS_LOCATION

object TaskLoader {

    fun loadTasks(context: Context): List<Task> {
        val tasks = mutableListOf<Task>()
        val assetManager = context.assets
        val files = assetManager.list(TASKS_LOCATION)

        files?.forEach { fileName ->
            val inputStream = assetManager.open("$TASKS_LOCATION/$fileName")
            val taskJson = inputStream.bufferedReader().use { it.readText() }
            val task = TaskSerializer.deserializeTask(taskJson)
            tasks.add(task)
        }
        return tasks
    }
}
