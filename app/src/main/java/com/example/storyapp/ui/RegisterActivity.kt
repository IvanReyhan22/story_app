package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.model.UserViewModel

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userViewModel.registerResponse.observe(this) {
            Toast.makeText(this, getString(R.string.registration_complete), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        userViewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        binding.btnRegister.setOnClickListener(this)
        binding.tvSignin.setOnClickListener(this)

        enableButton()
        inputListener()
        playAnimation()
    }

    private fun inputListener() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                enableButton()
            }
        }

        binding.edRegisterName.addTextChangedListener(textWatcher)
        binding.edRegisterEmail.addTextChangedListener(textWatcher)
        binding.edRegisterPassword.addTextChangedListener(textWatcher)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> register()
            R.id.tv_signin -> {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun register() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        userViewModel.register(name, email, password)
    }

    private fun enableButton() {
        binding.btnRegister.isEnabled = !checkIfInputValid()
    }

    private fun checkIfInputValid(): Boolean {
        val editTextList =
            listOf(binding.edRegisterEmail, binding.edRegisterName, binding.edRegisterPassword)
        return editTextList.any { editText ->
            editText.text.toString().isEmpty() || editText.error != null
        }
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            binding.loadingIndicator.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }

    private fun playAnimation() {
        animateIlustrator()
        val duration = 300L

        val tvTitle =
            ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 0f, 1f).setDuration(duration)
        val edName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 0f, 1f).setDuration(duration)
        val edEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 0f, 1f)
            .setDuration(duration)
        val edPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 0f, 1f)
            .setDuration(duration)
        val tvSignin =
            ObjectAnimator.ofFloat(binding.tvSignin, View.ALPHA, 0f, 1f).setDuration(duration)

        val btnSlide = ObjectAnimator.ofFloat(binding.btnRegister, View.TRANSLATION_Y, -80f, 0f)
            .setDuration(duration)
        val btnFade =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f).setDuration(duration)

        val animatorSet1 = AnimatorSet().apply {
            playSequentially(tvTitle, edName, edEmail, edPassword)
        }
        val animatorSet2 = AnimatorSet().apply {
            playTogether(btnSlide, btnFade, tvSignin)
        }

        AnimatorSet().apply {
            playSequentially(animatorSet1, animatorSet2)
            start()
        }
    }

    private fun animateIlustrator() {
        val path = Path()
        val centerX = 50f
        val centerY = 60f
        val radius = 20f

        path.arcTo(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            180f,
            359f,
            true
        )

        ObjectAnimator.ofFloat(binding.ivIlustration, View.X, View.Y, path).apply {
            duration = 6500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }.start()
    }

}