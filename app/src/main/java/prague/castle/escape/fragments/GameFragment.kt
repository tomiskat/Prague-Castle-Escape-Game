package prague.castle.escape.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chaos.view.PinView
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.razzaghimahdi78.dotsloading.linear.LoadingScaly
import prague.castle.escape.R
import prague.castle.escape.utils.Constants
import prague.castle.escape.utils.ImageSaver
import prague.castle.escape.viewmodels.GameViewModel
import prague.castle.escape.views.ClickableTextView

open class GameFragment : Fragment() {

    companion object {
        fun newInstance(fragmentNumber: String): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putString("fragmentNumber", fragmentNumber)
            fragment.arguments = args
            return fragment
        }
    }

    private var titleTextView: TextView? = null
    private var descriptionTextView: ClickableTextView? = null
    private var photoView: PhotoView? = null
    private var pinView: PinView? = null
    private var chipGroup: ChipGroup? = null
    private var choiceButton: Button? = null
    private var loadingScaly: LoadingScaly? = null
    private var distanceTextView: TextView? = null
    private var confirmButton: Button? = null
    private var resultTextView: TextView? = null
    private var editText: EditText? = null
    private var saveResultButton: Button? = null
    private var changeQRCodeButton: Button? = null
    private var saveQRCodeButton: Button? = null

    private var enabledColor: ColorStateList? = null
    private var disabledColor: ColorStateList? = null
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val fragmentNumber = arguments?.getString("fragmentNumber")
        val layoutId = fragmentNumber?.let { getLayoutId(it) }
        return layoutId?.let { inflater.inflate(it, container, false) }
    }

    // Reflection-based method to get the layout ID
    private fun getLayoutId(fragmentNumber: String): Int {
        val layoutName = "game_fragment$fragmentNumber"
        return R.layout::class.java.getField(layoutName).getInt(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeColors()
        initializeViews(view)
        setupTaskDetails()
        addObservers()
        addListeners()
    }

    private fun initializeColors() {
        enabledColor = ContextCompat.getColorStateList(requireContext(), R.color.button_background)
        disabledColor = ContextCompat.getColorStateList(requireContext(), R.color.card_background)
    }

    private fun initializeViews(view: View) {
        titleTextView = view.findViewById(R.id.title)
        descriptionTextView = view.findViewById(R.id.description)
        photoView = view.findViewById(R.id.image)
        pinView = view.findViewById(R.id.pin)
        chipGroup = view.findViewById(R.id.chip)
        choiceButton = view.findViewById(R.id.choice)
        loadingScaly = view.findViewById(R.id.loading)
        distanceTextView = view.findViewById(R.id.distance)
        confirmButton = view.findViewById(R.id.confirm)
        resultTextView = view.findViewById(R.id.result)
        editText = view.findViewById(R.id.edit)
        saveResultButton = view.findViewById(R.id.save_result)
        changeQRCodeButton = view.findViewById(R.id.change_QRCode)
        saveQRCodeButton = view.findViewById(R.id.save_QRCode)
    }

    private fun setupTaskDetails() {
        val currentTask = gameViewModel.task.value
        titleTextView?.text = currentTask?.title
        descriptionTextView?.text = currentTask?.description?.let {
            HtmlCompat.fromHtml(
                it,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        resultTextView?.text = gameViewModel.getGameDurationText()
    }


    private fun addObservers() {
        addDistanceObserver()
        addStoringErrorObserver()
    }

    private fun addDistanceObserver() {
        if (gameViewModel.isLocationTask()) {
            gameViewModel.distance.observe(viewLifecycleOwner) { distance ->
                // hide distance loading animation
                loadingScaly?.visibility = View.GONE

                // show distance text
                if (distance < Constants.DISTANCE_TO_TASK) {
                    distanceTextView?.setText(R.string.distance_reached)
                    confirmButton?.isEnabled = true
                    confirmButton?.backgroundTintList = enabledColor
                    return@observe
                }
                distanceTextView?.text = getString(R.string.distance, distance.toInt())
                confirmButton?.isEnabled = false
                confirmButton?.backgroundTintList = disabledColor
            }
        }
    }

    private fun addStoringErrorObserver() {
        gameViewModel.storingError.observe(viewLifecycleOwner) { storingError ->
            if (storingError != getString(R.string.empty_string)) {
                // enable storing team name
                saveResultButton?.isEnabled = true
                editText?.visibility = View.VISIBLE
                loadingScaly?.visibility = View.GONE

                if (storingError == Constants.NAME_ALREADY_EXISTS) {
                    editText?.setText(R.string.empty_string)
                    editText?.hint = getString(R.string.team_name)
                    saveResultButton?.isEnabled = false
                }

                // show storing error
                displayAlertDialog(storingError)
                gameViewModel.clearStoringError()
            }
        }
    }

    private fun displayAlertDialog(storingError: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.storing_error)
            .setMessage(storingError)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .setCancelable(false)
            .show()

        // Change color of the button to white
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun addListeners() {
        addConfirmButtonListener()
        addPinViewListener()
        addChipGroupListener()
        addChoiceButtonListener()
        addEditTextListener()
        addSaveResultButtonListener()
        addChangeQRCodeButtonListener()
        addSaveQRCodeButtonListener()
    }


    private fun addConfirmButtonListener() {
        confirmButton?.setOnClickListener {
            gameViewModel.nextTask()
        }
    }

    private fun addPinViewListener() {
        pinView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == pinView?.itemCount) {
                    gameViewModel.checkAnswer(s.toString())
                    pinView?.setText(R.string.empty_string)
                }
            }
        })
    }

    private fun addChipGroupListener() {
        chipGroup?.setOnCheckedStateChangeListener { _, checkedIds ->

            if (checkedIds.isEmpty()) {
                choiceButton?.isEnabled = false
                choiceButton?.backgroundTintList = disabledColor
                return@setOnCheckedStateChangeListener
            }
            choiceButton?.isEnabled = true
            choiceButton?.backgroundTintList = enabledColor
        }
    }

    private fun addChoiceButtonListener() {
        choiceButton?.setOnClickListener {
            val chipID = chipGroup?.checkedChipId
            val chipView = chipID?.let { it1 -> chipGroup?.findViewById<Chip>(it1) }
            gameViewModel.checkAnswer(chipView?.text.toString())
            chipGroup?.clearCheck()
        }
    }

    private fun addEditTextListener() {
        // hide hint when user starts typing
        editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editText?.text.isNullOrEmpty()) {
                editText?.hint = getString(R.string.empty_string)
            }
        }

        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable?) {}
            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                after: Int
            ) {
                saveResultButton?.isEnabled = !charSequence?.trim().isNullOrEmpty()
            }
        })
    }

    private fun addSaveResultButtonListener() {
        saveResultButton?.setOnClickListener {
            saveResultButton?.isEnabled = false
            editText?.visibility = View.GONE
            loadingScaly?.visibility = View.VISIBLE
            gameViewModel.saveResult(editText?.text.toString().trim())
        }
    }

    private fun addChangeQRCodeButtonListener() {
        changeQRCodeButton?.setOnClickListener {
            if (changeQRCodeButton?.text == getString(R.string.show_CZ_QRCode)) {
                photoView?.setImageResource(R.drawable.task26_cz)
                changeQRCodeButton?.text = getString(R.string.show_EU_QRCode)
                return@setOnClickListener
            }
            photoView?.setImageResource(R.drawable.task26_eu)
            changeQRCodeButton?.text = getString(R.string.show_CZ_QRCode)
        }
    }

    private fun addSaveQRCodeButtonListener() {
        saveQRCodeButton?.setOnClickListener {
            val bitmap = (photoView?.drawable as BitmapDrawable).bitmap
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "prague_castle_escape_qr.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            ImageSaver.saveImage(requireContext(), bitmap, contentValues)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onResume() {
        super.onResume()
        if (gameViewModel.isLocationTask() && hasLocationPermission()) {
            gameViewModel.startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (gameViewModel.isLocationTask()) {
            gameViewModel.stopLocationUpdates()
        }
    }

    override fun onStart() {
        super.onStart()
        if (gameViewModel.isLocationTask() && !hasLocationPermission()) {
            requestLocationPermission()
        }
    }
}