package com.ajithvgiri.phoneval.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat


enum class CheckPermissionResult {
    PermissionAsk,
    PermissionPreviouslyDenied,
    PermissionDisabled,
    PermissionGranted
}

typealias PermissionCheckCompletion = (CheckPermissionResult) -> Unit


object PermissionHandler {

    private fun shouldAskPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    fun checkPermission(context: Context, permission: String, completion: PermissionCheckCompletion) {
        // If permission is not granted
        if (shouldAskPermission(context, permission)) {
            //If permission denied previously
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (context as Activity).shouldShowRequestPermissionRationale(permission)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) {
                completion(CheckPermissionResult.PermissionPreviouslyDenied)
            } else {
                // Permission denied or first time requested
                if (PreferenceUtils.isFirstTimeAskingPermission(context, permission)) {
                    PreferenceUtils.firstTimeAskingPermission(context, permission, false)
                    completion(CheckPermissionResult.PermissionAsk)
                } else {
                    // Handle the feature without permission or ask user to manually allow permission
                    completion(CheckPermissionResult.PermissionDisabled)
                }
            }
        } else {
            completion(CheckPermissionResult.PermissionGranted)
        }
    }
}