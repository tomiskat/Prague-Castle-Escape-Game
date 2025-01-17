package prague.castle.escape.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object GameManager {

    private const val PREF_NAME = "encrypted_game_session"
    private const val KEY_TASK_ID = "current_task_id"
    private const val KEY_GAME_START_TIME = "game_start_time"
    private const val KEY_GAME_END_TIME = "game_end_time"
    private const val KEY_WRONG_ANSWERS_COUNT = "wrong_answers_count"

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun SharedPreferences.editChanges(action: SharedPreferences.Editor.() -> Unit) {
        val editor = edit()
        action(editor)
        editor.apply()
    }

    fun saveTaskId(context: Context, taskId: Int) {
        getEncryptedSharedPreferences(context).editChanges { putInt(KEY_TASK_ID, taskId) }
    }

    fun saveGameStartTime(context: Context, gameStartTime: Long) {
        getEncryptedSharedPreferences(context).editChanges {
            putLong(
                KEY_GAME_START_TIME,
                gameStartTime
            )
        }
    }

    fun saveGameEndTime(context: Context, gameEndTime: Long) {
        getEncryptedSharedPreferences(context).editChanges {
            putLong(
                KEY_GAME_END_TIME,
                gameEndTime
            )
        }
    }

    fun saveWrongAnswersCount(context: Context, wrongAnswersCount: Int) {
        getEncryptedSharedPreferences(context).editChanges {
            putInt(
                KEY_WRONG_ANSWERS_COUNT,
                wrongAnswersCount
            )
        }
    }

    fun getTaskId(context: Context): Int {
        return getEncryptedSharedPreferences(context).getInt(KEY_TASK_ID, 0)
    }

    fun getGameStartTime(context: Context): Long {
        return getEncryptedSharedPreferences(context).getLong(KEY_GAME_START_TIME, 0L)
    }

    fun getGameEndTime(context: Context): Long {
        return getEncryptedSharedPreferences(context).getLong(KEY_GAME_END_TIME, 0L)
    }

    fun getWrongAnswersCount(context: Context): Int {
        return getEncryptedSharedPreferences(context).getInt(KEY_WRONG_ANSWERS_COUNT, 0)
    }
}
