package com.example.storyapp.data.remote

data class LoginRequest(
    val email:String,
    val password:String
)

data class RegisterRequest(
    val name:String,
    val email:String,
    val password:String
)