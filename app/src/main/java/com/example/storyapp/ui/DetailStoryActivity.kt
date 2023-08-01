package com.example.storyapp.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.ui.model.StoriesViewModel
import com.example.storyapp.ui.model.StoriesViewModelFactory
import com.example.storyapp.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val storiesViewModel: StoriesViewModel by viewModels {
        StoriesViewModelFactory(this)
    }
    private var storyId: String = ""

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storyId = intent.getStringExtra(EXTRA_STORY).toString()

        storiesViewModel.getDetailStory(storyId)

        storiesViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        storiesViewModel.error.observe(this) {
            Toast.makeText(this@DetailStoryActivity, it, Toast.LENGTH_SHORT).show()
        }

        storiesViewModel.story.observe(this) {
            setData(it)
        }

        binding.btnExplore.setOnClickListener {
            finish()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun setData(story: Story) {
        Glide.with(binding.ivDetailPhoto.context).load(story.photoUrl).into(binding.ivDetailPhoto)
        binding.apply {
            tvDetailName.text = story.name
            tvDetailPostDate.text = story.createdAt?.withDateFormat() ?: "-"
            tvDetailDescription.text = story.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIndicator.root.visibility = View.VISIBLE
            binding.contentWrapper.visibility = View.INVISIBLE
        } else {
            binding.loadingIndicator.root.visibility = View.INVISIBLE
            binding.contentWrapper.visibility = View.VISIBLE
        }
    }
}