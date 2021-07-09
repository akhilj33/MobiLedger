package com.example.mobiledger.common.extention

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.example.mobiledger.common.utils.FileUtils
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream


fun Uri.getFileName(context: Context): String? {
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}

fun Uri.getType(context: Context): String? {
    return context.contentResolver.getType(this)
}

fun Uri.getFileExtension(): String? {
    return path?.let { path ->
        val extension: String? = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(path)).toString())
        val mime = MimeTypeMap.getSingleton()
        return extension?.let {
            mime.getMimeTypeFromExtension(it)
        }
    }
}

fun Uri.getFileSizeBytes(context: Context): Int {
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        cursor.getInt(sizeIndex)
    } ?: 0
}

fun Uri.getFile(context: Context, fileName: String): File {
    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(this, "r", null)
    val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
    val file = FileUtils.getFile(FileUtils.getCacheDirPath(context, FileUtils.Temp_Cache_FolderName), fileName)
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    return file
}

fun Uri.convertImageUriToBitmap(context: Context): Bitmap {
    val contentResolver = context.contentResolver
    val parcelFileDescriptor: ParcelFileDescriptor? =
        contentResolver.openFileDescriptor(this, "r")
    val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image
}
