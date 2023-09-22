package com.mtg.tool.findmyphone.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * add common function here
 */
object Common {

    /**
     * get screen device
     */
    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun smartCheckColor(stringColor: String): Int {
        return try {
            Color.parseColor(stringColor)
        } catch (_: Exception) {
            return Color.TRANSPARENT
        }
    }
    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(android.util.Base64.encode(md.digest(), 0))

                Log.i("~~~", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("~~~", "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e("~~~", "printHashKey()", e)
        }
    }

}