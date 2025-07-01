package prague.castle.escape.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import prague.castle.escape.R
import prague.castle.escape.fragments.GameFragment
import prague.castle.escape.repositories.GameRepository
import prague.castle.escape.repositories.TaskRepository
import prague.castle.escape.utils.Constants
import prague.castle.escape.viewmodels.GameViewModel
import prague.castle.escape.viewmodels.GameViewModelFactory


class GameActivity : AppCompatActivity() {

    private var permissionDialog: AlertDialog? = null
    private val gameViewModel: GameViewModel by lazy {
        val factory = GameViewModelFactory(
            taskRepository = TaskRepository(applicationContext),
            gameRepository = GameRepository(applicationContext),
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        ViewModelProvider(this, factory)[GameViewModel::class.java]
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        initializeFirebaseAppCheck()
        addObservers()

        // Lock the screen orientation to portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun initializeFirebaseAppCheck() {
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
    }

    private fun addObservers() {
        gameViewModel.task.observe(this) { task ->
            updateFragment(task.id)
        }

        gameViewModel.toast.observe(this) { toast ->
            if (toast != getString(R.string.empty_string)) {
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
                gameViewModel.clearToast()
            }
        }
    }

    private fun updateFragment(id: Int) {
        val paddedId = id.toString().padStart(2, '0')
        val fragment = GameFragment.newInstance(paddedId)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE
            && grantResults.isNotEmpty()
            && grantResults[0] != PackageManager.PERMISSION_GRANTED
        ) {
            displayPermissionDialog()
        }
    }

    private fun displayPermissionDialog() {
        permissionDialog = AlertDialog.Builder(this)
            .setTitle(R.string.location_permission_title)
            .setMessage(R.string.location_permission_text)
            .setPositiveButton(R.string.to_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:$packageName".toUri()
                }
                startActivity(intent)
            }
            .setCancelable(false)
            .show()

        // Change color of the button to white
        permissionDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    override fun onStop() {
        super.onStop()
        permissionDialog?.dismiss()
    }
}
