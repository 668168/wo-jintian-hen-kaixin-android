package com.happy.today.data

import android.content.Context
import android.content.SharedPreferences

/** Small, testable boundary around the app's SharedPreferences storage. */
class LocalStorage(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun getString(key: String): String? = preferences.getString(key, null)

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getStringSet(key: String): Set<String> =
        preferences.getStringSet(key, emptySet())?.toSet().orEmpty()

    fun putStringSet(key: String, value: Set<String>) {
        preferences.edit().putStringSet(key, value).apply()
    }

    private companion object {
        const val FILE_NAME = "happy_today"
    }
}
