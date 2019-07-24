package com.kuky.demo.wan.android.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * @author kuky.
 * @description
 */
object ImageSaveUtils {

    fun cropView(view: View, file: File): Boolean =
        try {
            saveImageToGallery(view, file)
            notifySystemGallery(view.context, file)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    private fun saveImageToGallery(view: View, file: File): File {
        try {
            val fos = FileOutputStream(file)
            createBitmap(view).compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    private fun notifySystemGallery(context: Context, file: File) {
        if (!file.exists()) throw IllegalStateException("file ${file.absolutePath} not exist")

        try {
            MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, null)
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("file ${file.absolutePath} not exist")
        }

        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
    }

    private fun createBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun isSDAvailable(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    private fun getFolderName(name: String): String {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name)
        return if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) ""
        else mediaStorageDir.absolutePath
    }

    fun getNewFile(context: Context, folderName: String, fileName: String): File? {

        val path = if (isSDAvailable()) {
            getFolderName(folderName) + File.separator + fileName + ".jpg"
        } else {
            context.filesDir.path + File.separator + fileName + ".jpg"
        }

        return if (TextUtils.isEmpty(path)) {
            null
        } else File(path)
    }
}