package com.alx.findphone.claptofind.flashalert.utils

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    fun saveFileFromUri(uri: Uri, filePath: String, context: Context): File? {
        val contentResolver: ContentResolver = context.applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream != null) {
            val outputFile = File(filePath)

            try {
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(4 * 1024) // 4KB buffer size

                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()
                return outputFile
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

    fun getFileNameAudioFromUri(uri: Uri, context: Context, maxChar: Int): String? {
        var fileName = ".mp3"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
//        if (fileName.length > maxChar) {
//            fileName = fileName.substring(0, maxChar - 5) +"."+ fileName.substring(fileName.lastIndexOf("."), fileName.length)
//        }
        if (fileName.length > maxChar) {
            fileName = if (fileName.lastIndexOf(".") != -1) {
                fileName.substring(0, maxChar - 5) + "." + fileName.substring(fileName.lastIndexOf("."), fileName.length)
            } else {
                fileName.substring(0, maxChar)
            }
        }
        return fileName
    }

    fun getDurationFromAudioFile(path: String): Long? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}