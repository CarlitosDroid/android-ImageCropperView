package com.spidev.materialimagecropper

import android.util.Log

/**
 * Created by Carlos Leonardo Camilo Vargas Huamán on 11/13/17.
 */
class LogUtil {
    companion object {
        fun e(TAG: String, logMessage: String) {
            if (BuildConfig.DEBUG)
                Log.e("x-$TAG", "x-$logMessage")
        }
    }
}