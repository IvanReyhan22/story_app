package com.example.storyapp.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.UserCredentials
import com.example.storyapp.data.remote.LoginRequest
import com.example.storyapp.data.remote.RegisterRequest
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.Result
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _registerResponse = MutableLiveData<Result>()
    val registerResponse: LiveData<Result> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _isLoading.value = false
    }

    fun saveAuthKey(userCredentials: UserCredentials) {
        val userPreference = UserPreference(getApplication<Application>().applicationContext)
        userPreference.setAuthKey(userCredentials)
    }

    fun getAuthKey(): UserCredentials {
        val userPreference = UserPreference(getApplication<Application>().applicationContext)
        return userPreference.getAuthKey()
    }

    fun logout() {
        val userPreference = UserPreference(getApplication<Application>().applicationContext)
        userPreference.deleteAuthKey()
    }

    fun login(email: String, password: String) {
        toggleLoading()
        val request = LoginRequest(email, password)
        val client = ApiConfig.getApiService(getApplication<Application>().applicationContext).login(request)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                toggleLoading()
                if (response.isSuccessful) {
                    val myResponse = response.body()
                    if (myResponse?.error == false) {
                        val result = response.body()?.loginResult
                        val model = UserCredentials(result?.userId, result?.name, result?.token)
                        saveAuthKey(model)
                        _loginResponse.let {
                            it.value = myResponse
                        }
                    } else {
                        _error.value = myResponse?.message ?: "Error Occurred"
                    }
                } else {
                    _error.value = response.message()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toggleLoading()
                _error.value = t.message
            }

        })
    }

    fun register(name: String, email: String, password: String) {
        toggleLoading()
        val request = RegisterRequest(name, email, password)
        val client = ApiConfig.getApiService(getApplication<Application>().applicationContext).register(request)
        client.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                toggleLoading()
                if (response.isSuccessful) {
                    val myResponse = response.body()
                    if (myResponse?.error == false) {
                        _registerResponse.let {
                            it.value = myResponse
                        }
                    } else {
                        _error.value = myResponse?.message ?: "Error Occurred"
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


}