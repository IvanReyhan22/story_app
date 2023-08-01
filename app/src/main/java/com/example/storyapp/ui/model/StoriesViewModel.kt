package com.example.storyapp.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepositories
import com.example.storyapp.data.local.entity.StoriesEntity
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.Result
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoriesViewModel(application: Application, storyRepositories: StoryRepositories) :
    AndroidViewModel(application) {

    val stories: LiveData<PagingData<StoriesEntity>> =
        storyRepositories.getStories().cachedIn(viewModelScope)

    private val _mapStory = MutableLiveData<List<Story>>()
    val mapStory: LiveData<List<Story>> = _mapStory

//    private val _stories = MutableLiveData<List<Story>>()
//    val stories: LiveData<List<Story>> = _stories

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _upload = MutableLiveData<Result>()
    val upload: LiveData<Result> = _upload

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _isLoading.value = false
        _isEmpty.value = false
    }

    fun getMapStories(location: Int? = null) {
        toggleLoading()
        val actualLoc = location ?: 0
        viewModelScope.launch {
            val response = ApiConfig.getApiService(getApplication<Application>().applicationContext)
                .getAllStories(actualLoc, 1, 10)
            if (response.error == false) {
                _mapStory.value = response.listStory.filterNotNull()
            } else {
                _error.value = response.message!!
            }
        }
    }

//    suspend fun getAllStories(location: Int? = null) {
//        toggleLoading()
//        val actualLoc = location ?: 0
//        val client =
//            ApiConfig.getApiService(getApplication<Application>().applicationContext)
//                .getAllStories(0,1,10)
//        client.enqueue(object : Callback<StoryResponse> {
//            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
//                toggleLoading()
//                if (response.isSuccessful) {
//                    val myResponse = response.body()
//                    if (myResponse?.error == false) {
//                        val result = response.body()?.listStory
//                        _stories.value = result?.filterNotNull()
//                        checkIfEmpty()
//                    } else {
//                        _error.value = myResponse?.message ?: "Error Occurred"
//                    }
//                } else {
//                    _error.value = response.message()
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                toggleLoading()
//                _error.value = t.message
//            }
//
//        })
//    }

    fun getDetailStory(id: String) {
        toggleLoading()
        val client = ApiConfig.getApiService(getApplication<Application>().applicationContext)
            .getDetailStory(id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                toggleLoading()
                if (response.isSuccessful) {
                    val myResponse = response.body()
                    if (myResponse?.error == false) {
                        _story.value = myResponse.story
                    } else {
                        _error.value = myResponse?.message ?: "Unable to retrieve story data"
                    }
                } else {
                    _error.value = response.message()
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                toggleLoading()
                _error.value = t.message
            }

        })
    }

    fun uploadStory(file: File, desc: String, lat: String? = null, lon: String? = null) {
        toggleLoading()
        val reducedSize = reduceFileImage(file)
        val requestDescription = desc.toRequestBody("text/plain".toMediaType())
        val postLat = lat?.toRequestBody("text/plain".toMediaType())
        val postLon = lon?.toRequestBody("text/plain".toMediaType())
        val requestImage = reducedSize.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImage
        )
        val client = ApiConfig.getApiService(getApplication<Application>().applicationContext)
            .postStory(imageMultipart, requestDescription, postLat, postLon)
        client.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                toggleLoading()
                if (response.isSuccessful) {
                    val myResponse = response.body()
                    if (myResponse != null && !myResponse.error!!) {
                        _upload.let {
                            it.value = myResponse
                        }
                    }
                } else {
                    _error.value = response.message()
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                toggleLoading()
                _error.value = t.message
            }

        })

    }


    private fun toggleLoading() {
        _isLoading.value = !isLoading.value!!
    }

//    private fun checkIfEmpty() {
//        _isEmpty.value = _stories.value?.size == 0
//    }

}