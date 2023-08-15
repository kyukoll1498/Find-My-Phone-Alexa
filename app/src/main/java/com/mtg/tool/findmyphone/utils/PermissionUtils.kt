package com.mtg.tool.findmyphone.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.REQUEST_PERMISSION_CODE
import com.mtg.tool.findmyphone.REQUEST_READ_AUDIO_PERMISSION_CODE
import com.mtg.tool.findmyphone.REQUEST_READ_PERMISSION_CODE

object PermissionUtils {

    fun checkReadAudioPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            checkReadPermission(context)
        } else {
            checkPermission(context, Manifest.permission.READ_MEDIA_AUDIO)
        }
    }

    private fun checkReadPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun checkMicroPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.RECORD_AUDIO)
    }


    private fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission(activity: Activity, permission: String) {
        var requestCode = when (permission) {
            Manifest.permission.RECORD_AUDIO -> {
                REQUEST_MICRO_PERMISSION_CODE
            }

            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                REQUEST_READ_PERMISSION_CODE
            }

            Manifest.permission.READ_MEDIA_AUDIO -> {
                REQUEST_READ_AUDIO_PERMISSION_CODE
            }

            else -> {
                REQUEST_PERMISSION_CODE
            }
        }
        activity.requestPermissions(
            arrayOf(permission),
            requestCode
        )
    }

    private fun requestReadPermission(activity: Activity) {
        requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun requestMicroPermission(activity: Activity) {
        requestPermission(activity, Manifest.permission.RECORD_AUDIO)
    }
    fun requestReadAudioPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            requestReadPermission(activity)
        } else {
            requestPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
        }
    }
}
