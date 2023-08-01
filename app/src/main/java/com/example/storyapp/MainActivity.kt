package com.example.storyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.HomeActivity
import com.example.storyapp.ui.LoginActivity
import com.example.storyapp.ui.model.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        val isAuthKeyExist: String? = userViewModel.getAuthKey().userId

        if (isAuthKeyExist?.isNotEmpty() == true) {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }

        playAnimation()
    }

    private fun playAnimation() {
        val path = Path()
        val centerX = 50f
        val centerY = 60f
        val radius = 24f

        path.arcTo(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 180f, 359f, true)

        ObjectAnimator.ofFloat(binding.ivIlustration, View.X, View.Y, path).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle,View.ALPHA,1f).setDuration(500)
        val subtitle = ObjectAnimator.ofFloat(binding.tvSubtitle,View.ALPHA,1f).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.btnStart,View.ALPHA,1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title,subtitle,btn)
            start()
        }
    }
}