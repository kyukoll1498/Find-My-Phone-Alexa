package com.alx.findphone.claptofind.flashalert.utils

import android.content.Context
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

//import linc.com.library.AudioTool
//import java.io.File


object AudioUtil {
    fun cutAudio(
        context: Context,
        start: Int,
        duration: Int,
        path: String,
        callback: (outputPath: String) -> Unit
    ) {
//        val bytesToSkip: Long = getByteNoFromSecNo(
//            0,
//            15,
//            File(path).length()
//        )
//        val lastByteNo: Long = getByteNoFromSecNo(
//            15,
//            150,
//            File(path).length()
//        )
//        var bytesToRead = lastByteNo - bytesToSkip
//        try {
//            val pathNewFile: String = path
//            val bufferedInputStream = BufferedInputStream(FileInputStream(File(path)))
//            bufferedInputStream.skip(bytesToSkip)
//            val newFile = File(pathNewFile)
//            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(newFile))
//            while (bytesToRead-- > 0) {
//                bufferedOutputStream.write(bufferedInputStream.read())
//            }
//            bufferedInputStream.close()
//            bufferedOutputStream.close()
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }

    fun getByteNoFromSecNo(startSec: Long, totalDuration: Long, noOfBytes: Long): Long {
        return startSec * noOfBytes / totalDuration
    }

    fun cutAudio(
        context: Context,
        start: Long,
        end: Long,
        totalDuration: Long,
        path: String,
        isOverride: Boolean,
        callback: (outputPath: String) -> Unit
    ) {
        val bytesToSkip: Long = getByteNoFromSecNo(
            start,
            totalDuration,
            File(path).length()
        )
        val lastByteNo: Long = getByteNoFromSecNo(
            end,
            totalDuration,
            File(path).length()
        )
        var bytesToRead = lastByteNo - bytesToSkip
        try {
            val pathNewFile: String = CacheUtils.getNewNameFileAudio(context)
            val bufferedInputStream = BufferedInputStream(FileInputStream(File(path)))
            bufferedInputStream.skip(bytesToSkip)
            val newFile = File(pathNewFile)
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(newFile))
            while (bytesToRead-- > 0) {
                bufferedOutputStream.write(bufferedInputStream.read())
            }
//            if (isOverride) {
//                File(path).delete()
//            }
            bufferedInputStream.close()
            bufferedOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}