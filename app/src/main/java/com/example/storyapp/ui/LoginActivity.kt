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
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.model.UserViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userViewModel.loginResponse.observe(this) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }

        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        userViewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        binding.btnLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.tvForgetPassword.setOnClickListener(this)

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

        binding.edLoginEmail.addTextChangedListener(textWatcher)
        binding.edLoginPassword.addTextChangedListener(textWatcher)
    }

    private fun enableButton() {
        binding.btnLogin.isEnabled = !checkIfInputValid()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> login()
            R.id.tv_register -> {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            R.id.tv_forget_password -> {}
        }
    }

    private fun checkIfInputValid(): Boolean {
        val editTextList = listOf(binding.edLoginEmail, binding.edLoginPassword)
        return editTextList.any { editText ->
            editText.text.toString().isEmpty() || editText.error != null
        }
    }

    private fun login() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        userViewModel.login(email, password)
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            binding.loadingIndicator.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.GONE
            binding.btnLogin.visibility = View.VISIBLE
        }
    }

    private fun playAnimation() {
        animateIlustrator()
        val duration = 300L
        val tvTitle = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 0f, 1f).setDuration(duration)
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 0f, 1f).setDuration(duration)
        val edPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 0f, 1f).setDuration(duration)
        val tvForgot = ObjectAnimator.ofFloat(binding.tvForgetPassword, View.ALPHA, 0f, 1f).setDuration(duration)
        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 0f, 1f).setDuration(duration)

        val btnSlide = ObjectAnimator.ofFloat(binding.btnLogin, View.TRANSLATION_Y, -80f, 0f).setDuration(duration)
        val btnFade = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f).setDuration(duration)

        val animatorSet1 = AnimatorSet().apply {
            playSequentially(tvTitle,edEmail,edPassword)
        }
        val animatorSet2 = AnimatorSet().apply {
            playTogether(btnSlide,btnFade)
        }
        val animatorSet3 = AnimatorSet().apply {
            playSequentially(tvForgot,tvRegister)
        }

        AnimatorSet().apply {
            playSequentially(animatorSet1,animatorSet2,animatorSet3)
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