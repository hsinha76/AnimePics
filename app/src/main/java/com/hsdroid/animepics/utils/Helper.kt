package com.hsdroid.animepics.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

const val Dir = "AnimePics"

class Helper {
    companion object {

        fun capitalizeFirstLetter(str: String): String {
            return str.substring(0, 1).uppercase(Locale.ROOT) + str.substring(1)
        }

        fun checkPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: Context) {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }

        fun toPublicDirectory(context: Context, bitmap: Bitmap) {
            val filename = System.currentTimeMillis().toString()
            val resolver = context.contentResolver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$Dir")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                imageUri?.let {
                    try {
                        resolver.openOutputStream(it)?.use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("harish", e.message.toString())
                    }
                }
            } else {
                val imagesDir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/$Dir"
                val imagesFile = File(imagesDir)

                if (!imagesFile.exists()) imagesFile.mkdirs()
                val image = File(imagesDir, "$filename.jpg")

                try {
                    FileOutputStream(image).use { fos ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    }
                    MediaScannerConnection.scanFile(context, arrayOf(image.absolutePath), null, null)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("harish", e.message.toString())
                }
            }
        }
    }
}