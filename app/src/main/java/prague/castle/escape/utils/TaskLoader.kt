package prague.castle.escape.utils

import android.content.Context
import prague.castle.escape.models.Task

object TaskLoader {

    fun loadTasks(context: Context): List<Task> {
        val tasks = mutableListOf<Task>()
        val assetManager = context.assets
        val files = assetManager.list(Constants.TASKS_LOCATION)

        files?.forEach { fileName ->
            val inputStream = assetManager.open("${Constants.TASKS_LOCATION}/$fileName")
            val taskJson = inputStream.bufferedReader().use { it.readText() }
            val task = TaskSerializer.deserializeTask(taskJson)
            tasks.add(task)
        }
        return tasks
    }
}
