package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.ListStoriesAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.data.local.entity.StoriesEntity
import com.example.storyapp.databinding.ActivityHomeBinding
import com.example.storyapp.ui.model.StoriesViewModel
import com.example.storyapp.ui.model.StoriesViewModelFactory
import com.example.storyapp.ui.model.UserViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var storiesAdapter: ListStoriesAdapter
    private val userViewModel: UserViewModel by viewModels()
    private val storiesViewModel: StoriesViewModel by viewModels {
        StoriesViewModelFactory(this)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                userViewModel.logout()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_map -> {
                startActivity(Intent(this@HomeActivity, StoryMapActivity::class.java))
                true
            }
            else -> true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storiesViewModel.error.observe(this) {
            Toast.makeText(this@HomeActivity, it, Toast.LENGTH_SHORT).show()
        }

        binding.rvStories.layoutManager = LinearLayoutManager(this)
        storiesAdapter = ListStoriesAdapter()
        setStoriesDataList()

        binding.postContainer.setOnClickListener {
            startActivity(Intent(this@HomeActivity, PostActivity::class.java))
        }
    }

    private fun setStoriesDataList() {

        binding.rvStories.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storiesAdapter.retry()
            }
        )

        storiesViewModel.stories.observe(this) {
            storiesAdapter.submitData(lifecycle, it)
            binding.rvStories.apply {
                if (storiesAdapter.itemCount > 0) {
                    smoothScrollToPosition(0)
                }
            }
        }

        storiesAdapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClick(data: StoriesEntity) {
                val intent = Intent(this@HomeActivity, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_STORY, data.id)
                startActivity(intent)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        storiesAdapter.refresh()
    }

}