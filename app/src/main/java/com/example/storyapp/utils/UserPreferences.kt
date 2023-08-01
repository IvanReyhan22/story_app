package com.example.storyapp.utils

import android.content.Context
import com.example.storyapp.data.UserCredentials

internal class UserPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USER_ID = "user_id"
        private const val NAME = "name"
        private const val TOKEN = "token"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setAuthKey(model: UserCredentials) {
        val editor = preferences.edit()
        editor.apply() {
            putString(USER_ID, model.userId)
            putString(NAME, model.name)
            putString(TOKEN, model.token)
            apply()
        }
    }

    fun getAuthKey(): UserCredentials {
        val model = UserCredentials()
        model.apply {
            name = preferences.getString(NAME, "")
            userId = preferences.getString(USER_ID, "")
            token = preferences.getString(TOKEN, "")
        }
        return model
    }

    fun getAuthorizationToken(): String = preferences.getString(TOKEN, "") ?: ""

    fun deleteAuthKey() {
        val editor = preferences.edit()
        editor.apply() {
            remove(USER_ID)
            remove(NAME)
            remove(TOKEN)
            apply()
        }

    }

}