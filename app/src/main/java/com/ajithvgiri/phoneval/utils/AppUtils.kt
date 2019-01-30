/*
 * Created by @ajithvgiri on 29/12/18 10:13 PM
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 23/11/18 1:01 AM
 */
package com.ajithvgiri.phoneval.utils

import android.support.design.widget.Snackbar
import android.util.Log.*
import android.view.View


enum class LogType {
    DEBUG,
    INFO,
    ERROR,
    WARNING,
    VERBOSE
}

object AppUtils {

    fun printLog(TAG: String, message: String? = "No log message found", type: LogType) {
        when (type) {
            LogType.DEBUG -> d(TAG, message)
            LogType.INFO -> i(TAG, message)
            LogType.ERROR -> e(TAG, message)
            LogType.WARNING -> w(TAG, message)
            else -> v(TAG, message)
        }
    }

    fun snackMessage(view: View,message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

}