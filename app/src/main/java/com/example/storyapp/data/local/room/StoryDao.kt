package com.example.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.local.entity.StoriesEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoriesEntity>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoriesEntity>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}