package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.LoginRequest
import com.example.storyapp.data.remote.RegisterRequest
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.Result
import com.example.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<Result>

    @Multipart
    @POST("stories")
    fun postStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<Result>

    @GET("stories")
    suspend fun getAllStories(
        @Query("location") location: Int? = null,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: String
    ): Call<DetailStoryResponse>


}