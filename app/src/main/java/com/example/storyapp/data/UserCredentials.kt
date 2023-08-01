package com.example.storyapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserCredentials (
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null,
) : Parcelable