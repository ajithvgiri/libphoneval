package com.ajithvgiri.phoneval.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

object PreferenceUtils {

    private val PREFS_FILE_NAME = "QkopyPreference"

    fun firstTimeAskingPermission(context: Context, permission: String, isFirstTime: Boolean) {
        val sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply()
    }

    fun isFirstTimeAskingPermission(context: Context, permission: String): Boolean {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true)
    }
}
