package com.example.activitylifecycle

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.activitylifecycle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var loginIsReady = false
    private var passwordIsReady = false
    private var agreeIsReady = false
    private val tag = "Activity lifecycle LOG"
    private var state: FormState = FormState(false, "", Color.BLACK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(tag, "onCreate ${hashCode()}")

        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable<FormState>(KEY_VALID)
                ?: error("Unexpected state")
            Log.d(tag, state.message)
            binding.textViewValid.setTextColor(state.color)
            binding.textViewValid.text = state.message
        }

        uploadImage()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart ${hashCode()}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume ${hashCode()}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause ${hashCode()}")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop ${hashCode()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy ${hashCode()}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_VALID, state)
    }

    private fun initListeners() {
        binding.checkBoxAgree.setOnCheckedChangeListener { _, b ->
            agreeIsReady = b
            tryToMakeLogInButtonActive()
        }

        binding.editTextLogin.addTextChangedListener {
            loginIsReady = it?.isNotEmpty()!!
            tryToMakeLogInButtonActive()
        }

        binding.editTextPassword.addTextChangedListener {
            passwordIsReady = it?.isNotEmpty()!!
            tryToMakeLogInButtonActive()
        }

        binding.buttonLogIn.setOnClickListener {
            showProgressBar()
        }
        binding.buttonSimulationANR.setOnClickListener {
            simulateANR()
        }
    }

    private fun checkEmailValid(str: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun checkPassValid(str: String): Boolean {
        return str.length > 6
    }

    private fun simulateANR() {
        Thread.sleep(10000)
    }

    private fun showProgressBar() {
        with(binding) {
/*            lifecycleScope.launch {
                resetEnabled(editTextLogin, editTextPassword, checkBoxAgree, buttonLogIn)
                val view = layoutInflater.inflate(R.layout.progress_bar, frameContainer, false)
                frameContainer.addView(view)
                delay(2000)
                frameContainer.removeView(view)
                showToast()
                resetEnabled(editTextLogin, editTextPassword, checkBoxAgree, buttonLogIn)
            }*/
            resetEnabled(false, editTextLogin, editTextPassword, checkBoxAgree, buttonLogIn)
            val view = layoutInflater.inflate(R.layout.progress_bar, constraintContainer, false)
            constraintContainer.addView(view)
            Handler(Looper.getMainLooper()).postDelayed({
                constraintContainer.removeView(view)
                updateValidText()
                resetEnabled(true, editTextLogin, editTextPassword, checkBoxAgree, buttonLogIn)
            }, 2000)
        }

    }

    private fun updateValidText() {
        if (checkEmailValid(binding.editTextLogin.text.toString())
            && checkPassValid(binding.editTextPassword.text.toString())
        ) {
            state = state.setValid()
            Log.d(tag, "updateValidText -> ${state.message}")
            binding.textViewValid.setTextColor(Color.GREEN)
            binding.textViewValid.text = getString(R.string.text_logged_correctly)
        } else {
            state = state.setNotValid()
            Log.d(tag, "updateValidText -> ${state.message}")
            binding.textViewValid.setTextColor(Color.RED)
            binding.textViewValid.text = getString(R.string.text_logged_wrong)
        }
    }

/*    private fun showToast() {
        Toast.makeText(
            applicationContext,
            getString(R.string.text_logged_correctly),
            Toast.LENGTH_SHORT
        ).show()
    }*/

    private fun resetEnabled(state: Boolean, vararg views: View) {
        views.forEach {
            it.isEnabled = state
        }
    }

    private fun tryToMakeLogInButtonActive() {
        binding.buttonLogIn.isEnabled = agreeIsReady && loginIsReady && passwordIsReady
    }

    private fun uploadImage() {
        Glide.with(this)
            .load(URL_PATH)
            .placeholder(R.drawable.png_download_arrow)
            .error(R.drawable.png_download_arrow)
            .into(binding.imageViewTopPicture)
    }

    companion object {
        private const val KEY_VALID = "valid"
        private const val URL_PATH =
            "https://www.pngkit.com/png/full/281-2812821_user-account-management-logo-user-icon-png.png"
    }
}