package com.bootcamp.santander.dio.businesscard.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bootcamp.santander.dio.businesscard.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

class Image {
    companion object {

        fun share(context: Context, view: View) {
            val bitmap = getScreenShotFromView(view)
            bitmap?.let {
                saveMediaToStorage(context, bitmap)
            }
        }

        private fun getScreenShotFromView(view: View): Bitmap? {
            var screenshot: Bitmap? = null
            try {
                screenshot =
                    Bitmap.createBitmap(
                        view.measuredWidth,
                        view.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                val canvas = Canvas(screenshot)
                view.draw(canvas)
            } catch (e: Exception) {
                Log.e("GFG", "Failed to capture screenshot because:" + e.message)
            }
            return screenshot
        }

        private fun saveMediaToStorage(context: Context, bitmap: Bitmap) {
            val filename = "${System.currentTimeMillis()}.jpg"
            var fileOutputStream: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    fileOutputStream = imageUri?.let {
                        shareIntent(context, imageUri)
                        resolver.openOutputStream(it)
                    }
                }
            } else {
                //Devices below Android Q
                val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                // shareIntent(context, Uri.fromFile(image))
                fileOutputStream = FileOutputStream(image)


                fileOutputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    Toast.makeText(context, R.string.message_image_saved, Toast.LENGTH_SHORT).show()
                }
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, image)
                    type = "image.jpeg"
                }
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        context.resources.getText(R.string.label_share_to)
                    )
                )
            }
        }

        private fun shareIntent(context: Context, image: Uri) {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, image)
                type = "image.jpeg"
            }
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.resources.getText(R.string.label_share_to)
                )
            )
        }
    }


}