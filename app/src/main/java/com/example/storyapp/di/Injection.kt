package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepositories
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepositories {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(context)
        return StoryRepositories(database, apiService)
    }
}