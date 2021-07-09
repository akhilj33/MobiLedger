package com.example.mobiledger.common.utils

import android.content.Context
import java.io.File

object FileUtils {

    const val Temp_Cache_FolderName = "tempPics"

    fun getFile(pathname: String, fileName: String? = null): File {
        return if (fileName != null) File(pathname + File.separator.toString() + fileName)
        else File(pathname)
    }

    fun getCacheDirPath(context: Context, folderName: String? = null): String {
        return if (folderName != null) {
            createFolderIfNotExist(context, folderName)
            context.cacheDir.toString() + File.separator.toString() + folderName
        } else {
            context.cacheDir.toString()
        }
    }

    fun getExternalDirPath(context: Context, destination: String): String =
        context.getExternalFilesDir(destination).toString()

    private fun createFolderIfNotExist(context: Context, folderName: String) {
        val myDir = File(context.cacheDir, folderName)
        if (!myDir.exists()) myDir.mkdir()
    }

    fun getFileIfExists(context: Context, fileName: String): File? {
        val file = File(context.cacheDir, fileName)
        return if (file.exists()) file
        else null
    }

}