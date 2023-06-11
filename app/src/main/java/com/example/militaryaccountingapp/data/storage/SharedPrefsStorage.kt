package com.example.militaryaccountingapp.data.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.militaryaccountingapp.domain.helper.Results
import javax.inject.Inject

class SharedPrefsStorage<Data> @Inject constructor(
    context: Context,
    private val defaultValue: Data
) : Storage<String, Data> {

    companion object {
        private const val SHARED_PREFS_STORAGE_NAME = "settings"
    }

    private val storage = context.getSharedPreferences(
        SHARED_PREFS_STORAGE_NAME + "_preferences", MODE_PRIVATE
    )

    override suspend fun save(key: String, data: Data): Results<Void?> {
        val edit = storage.edit()
        when (data) {
            is String -> edit.putString(key, data)
            is Boolean -> edit.putBoolean(key, data)
            is Long -> edit.putLong(key, data)
            is Int -> edit.putInt(key, data)
            is Float -> edit.putFloat(key, data)
            else -> return Results.Failure(IllegalArgumentException())
        }
        edit.apply()
        return Results.Success(null)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(key: String): Results<Data> {
        val res = when (defaultValue) {
            is Boolean -> storage.getBoolean(key, defaultValue)
            is Int -> storage.getInt(key, defaultValue)
            is Long -> storage.getLong(key, defaultValue)
            is Float -> storage.getFloat(key, defaultValue)
            is String -> storage.getString(key, defaultValue)
            else -> return Results.Failure(IllegalArgumentException())
        } as? Data
        return res?.let { Results.Success(it) } ?: Results.Failure(IllegalArgumentException())
    }
}