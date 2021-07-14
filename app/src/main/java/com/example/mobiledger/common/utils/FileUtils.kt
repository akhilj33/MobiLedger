package com.example.mobiledger.common.utils

import android.content.Context
import com.example.mobiledger.common.utils.FileShareUtils.IMAGE_JPEG
import com.example.mobiledger.common.utils.FileShareUtils.IMAGE_JPG
import com.example.mobiledger.common.utils.FileShareUtils.IMAGE_PNG
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

    fun getType(mimeType: String?): String? {
        return when (mimeType) {
            IMAGE_PNG -> "png"
            IMAGE_JPEG -> "jpeg"
            IMAGE_JPG -> "jpg"
            else -> null
        }
    }

    fun deleteFileDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list() ?: return false
            for (i in children.indices) {
                val success = deleteFileDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

}