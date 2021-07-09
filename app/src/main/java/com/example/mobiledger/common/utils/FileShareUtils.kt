package com.example.mobiledger.common.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.mobiledger.BuildConfig
import com.example.mobiledger.common.utils.FileUtils.getCacheDirPath
import com.example.mobiledger.common.utils.FileUtils.getFile
import java.util.*

object FileShareUtils {

    const val IMAGE_JPEG = "image/jpeg"
    const val IMAGE_PNG = "image/png"
    const val IMAGE_JPG = "image/jpg"

    val SUPPORTED_MIME_TYPE = arrayOf(
        IMAGE_JPEG, IMAGE_JPG, IMAGE_PNG
    )

    val EXCLUDED_APPS = listOf("com.google.android.apps.docs")

    fun getFileChooserIntent(context: Context, cameraUri: Uri): Intent? {
        val allIntents: MutableList<Intent> = mutableListOf()

        getCameraIntents(context, cameraUri)?.let { allIntents.addAll(it) }

        var galleryIntents = getGalleryIntents(context, Intent.ACTION_GET_CONTENT)
        if (galleryIntents.isEmpty()) {
            galleryIntents = getGalleryIntents(context, Intent.ACTION_PICK)
        }
        allIntents.addAll(galleryIntents)

        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = allIntents[allIntents.size - 1]
            allIntents.removeAt(allIntents.size - 1)
        }

        val chooserIntent = Intent.createChooser(target, null)

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())
        return chooserIntent
    }

    private fun getCameraIntents(context: Context, uri: Uri?): List<Intent>? {
        val allIntents: MutableList<Intent> = ArrayList()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = context.packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            }
            allIntents.add(intent)
        }
        return allIntents
    }

    fun getCaptureImageOutputUri(context: Context, uid: String): Uri? {
        val file = getFile(getCacheDirPath(context, FileUtils.Temp_Cache_FolderName), "$uid _profile.jpg")
        return FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY, file)
    }

    private fun getGalleryIntents(context: Context, action: String): List<Intent> {
        val intents: MutableList<Intent> = ArrayList()
        val galleryIntent =
            if (action === Intent.ACTION_GET_CONTENT) Intent(action) else Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = IMAGE_JPG

        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, SUPPORTED_MIME_TYPE)
        val listGallery = context.packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            if (!EXCLUDED_APPS.contains(res.activityInfo.packageName)) {
                val intent = Intent(galleryIntent)
                intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.setPackage(res.activityInfo.packageName)
                intents.add(intent)
            }
        }
        return intents
    }
}