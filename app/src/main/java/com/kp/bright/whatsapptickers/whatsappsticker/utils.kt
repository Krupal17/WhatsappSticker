package com.kp.bright.whatsapptickers.whatsappsticker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kp.bright.whatsapptickers.whatsappsticker.StickerContentProvider
import com.kp.bright.whatsapptickers.stickersmanage.StickerPackUtils
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStream


var ADD_PACK = 200
fun loadAllStickerPacks(context: Context): List<StickerPackMetadata> {
    var result = ArrayList<StickerPackMetadata>()
    val metadataFile: File = File(context.getExternalFilesDir(null), "stickers/sticker_packs.json")
    if (!metadataFile.exists()) {
        return result
    }
    try {
        FileReader(metadataFile).use { reader ->
            val gson = Gson()
            val listType = object : TypeToken<List<StickerPackMetadata?>?>() {}.type
            val stickerPacks = gson.fromJson<List<StickerPackMetadata>>(reader, listType)
            result.addAll(stickerPacks)
            return result
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return result
    }
}

<<<<<<< Updated upstream
fun copyAssetsToExternalStorage(context: Context, identifier: String) {
=======
fun loadAllStickerPacks(context: Context): ArrayList<StickerPackMetadata> {
    var result = ArrayList<StickerPackMetadata>()
    val metadataFile: File = File(context.getExternalFilesDir(null)?.absolutePath)
    Log.e("TAG-", "loadAllStickerPacks: --> ${metadataFile.exists()}")
    if (!metadataFile.exists()) {
        return result
    }
    metadataFile?.listFiles()?.forEach { pack ->
        if (!pack.name.equals("stickers")) {
            if (pack.exists()) {
                var stickerPaths = ArrayList<String>()
                var iconPath = ""
                CoroutineScope(Dispatchers.IO).launch {
                    var metadata = loadStickerPack(context, identifier = pack.name)
//                        createStickerPack(
//                        pack.name,
//                        pack.name,
//                        "kpStickers",
//                        iconPath,
//                        stickerPaths,
//                        context,
//                        isJsonRetrival = false
//                    )
                    metadata?.let { result.add(it) }
                }
            }
        }
    }
    return result
}

fun loadStickerPack(context: Context, identifier: String): StickerPackMetadata? {
    var result: StickerPackMetadata? = null
    val metadataFile: File = File(context.getExternalFilesDir(null), identifier)
    Log.e("TAG-", "loadAllStickerPacks: --> ${metadataFile.exists()}")
    if (!metadataFile.exists()) {
        return result
    }

    var stickerPaths = ArrayList<String>()
    var iconPath = ""
    var isAnim = false
    metadataFile.listFiles().forEach {
        if (it.name.contains("trayicon")) {
            iconPath = it.name
        } else {
            isAnim = it.name.contains("anim_")
            stickerPaths.add(it.name)
        }
    }
    val stickers = stickerPaths.map { Sticker(it, ArrayList()) }
    var pack = StickerPackMetadata(
        identifier,
        identifier,
        "kpStickers",
        iconPath,
        isAnim,
        stickers = stickers
    )
    result = pack
    return result
}

private fun convertGifToWebp(gifPath: String, webpPath: String) {
    val bitmap = BitmapFactory.decodeFile(gifPath)
    if (bitmap != null) {
        try {
            FileOutputStream(webpPath).use { out ->
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)
                Log.i("Bitmap to WebP", "Conversion successful")
            }
        } catch (e: IOException) {
            Log.e("Bitmap to WebP", "Error saving WebP", e)
        }
    } else {
        Log.e("Bitmap to WebP", "Failed to decode GIF")
    }
}

private fun convertGifToWebpForAssets(context: Context, gifAssetPath: String, packFile: String) {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null

    try {
        // Open the GIF from the assets
        inputStream = context.assets.open(gifAssetPath)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        if (bitmap != null) {

            // Define the output WebP file path
            val outputFile = File(packFile)
            outputStream = FileOutputStream(outputFile)

            // Compress and save the WebP file
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
            Log.i("Bitmap to WebP", "Conversion successful: " + outputFile.absolutePath)
        } else {
            Log.e("Bitmap to WebP", "Failed to decode GIF")
        }
    } catch (e: IOException) {
        Log.e("Bitmap to WebP", "Error during conversion", e)
    } finally {
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
            Log.e("Bitmap to WebP", "Error closing streams", e)
        }
    }
}

fun copyAllAssetsToExternalStorage(context: Context) {
    val assetManager = context.assets
    val assetsDir = "sticker" // Folder name in the assets directory
    val asset_packs_names = assetManager.list(assetsDir)
    Log.d("TAG-", "sff asset_packs: " + asset_packs_names?.size)
    if (asset_packs_names != null) {
        for (packname in asset_packs_names) {
            val pack = assetManager.list("$assetsDir/$packname")
            if (pack != null) {
                var newFile = File(
                    context.getExternalFilesDir(packname)?.absolutePath
                )
                Log.e(TAG, "copyAllAssetsToExternalStorage: ${newFile.absolutePath}")
                if (!newFile.exists()) {
                    newFile.mkdirs()
                }

                for (packfile in pack) {
                    var packFileAssetDir = "$assetsDir/$packname/$packfile"
                    val inputStream: InputStream = assetManager.open(packFileAssetDir)
                    var outFile: File

                    if (packfile.endsWith(".gif")) {
                        outFile = File(
                            context.getExternalFilesDir(null), // Using getExternalFilesDir for better compatibility
                            "$packname/anim_$packfile"
                        )
                    } else {
                        outFile = File(
                            context.getExternalFilesDir(null), // Using getExternalFilesDir for better compatibility
                            "$packname/$packfile"
                        )
                    }

                    saveFile(inputStream, outFile)

                    // Log file sizes for debugging
                    val assetFileSize = assetManager.openFd(packFileAssetDir).length
                    val externalFileSize = outFile.length()
                    Log.d(
                        "TAG-",
                        "File copied: $packfile, Asset size: $assetFileSize, External size: $externalFileSize"
                    )

//                    if (packfile.endsWith(".gif")) {
//                        var webpFile = packfile.replace(".gif", ".webp")
//                        var newFile = File(
//                            context.getExternalFilesDir(null), // Using getExternalFilesDir for better compatibility
//                            "$packname/anim_$webpFile"
//                        )
//                        convertGifToWebpForAssets(context, packFileAssetDir, newFile.absolutePath)
//                    } else {
//
//                        val inputStream: InputStream = assetManager.open(packFileAssetDir)
//                        val outFile = File(
//                            context.getExternalFilesDir(null), // Using getExternalFilesDir for better compatibility
//                            "$packname/$packfile"
//                        )
//
//                        saveFile(inputStream, outFile)
//
//                        // Log file sizes for debugging
//                        val assetFileSize = assetManager.openFd(packFileAssetDir).length
//                        val externalFileSize = outFile.length()
//                        Log.d(
//                            "TAG-",
//                            "File copied: $packfile, Asset size: $assetFileSize, External size: $externalFileSize"
//                        )
//                        // Compare image properties
////                        val assetBitmap =
////                            BitmapFactory.decodeStream(assetManager.open(packFileAssetDir))
////                        val externalBitmap = BitmapFactory.decodeFile(outFile.absolutePath)
////                        val assetHeight = assetBitmap.height
////                        val externalHeight = externalBitmap.height
////                        Log.d(
////                            "TAG-",
////                            "File: $packfile, Asset height: $assetHeight, External height: $externalHeight"
////                        )
//                    }
                }
                loadStickerPack(context, packname)?.let {
                    StickerPackUtils.saveStickerPackMetadata(
                        context, it
                    )
                };
            }

        }
    }

//    if (files != null) {
//        for (filename in files) {
//            val inputStream: InputStream = assetManager.open("$assetsDir/$filename")
//            val outFile = File(
//                context.getExternalFilesDir(null), // Using getExternalFilesDir for better compatibility
//                "$identifier/$filename"
//            )
//            if (!outFile.parentFile.exists()) {
//                outFile.parentFile.mkdirs()
//            }
//            saveFile(inputStream, outFile)
//
//            // Log file sizes for debugging
//            val assetFileSize = assetManager.openFd("$assetsDir/$filename").length
//            val externalFileSize = outFile.length()
//            Log.d(
//                "TAG-",
//                "File copied: $filename, Asset size: $assetFileSize, External size: $externalFileSize"
//            )
//
//            // Compare image properties
//            val assetBitmap = BitmapFactory.decodeStream(assetManager.open("$assetsDir/$filename"))
//            val externalBitmap = BitmapFactory.decodeFile(outFile.absolutePath)
//            val assetHeight = assetBitmap.height
//            val externalHeight = externalBitmap.height
//            Log.d(
//                "TAG-",
//                "File: $filename, Asset height: $assetHeight, External height: $externalHeight"
//            )
////            notifyMediaScanner(context, outFile)
//
//        }
//        loadStickerPack(context, identifier)?.let {
//            StickerPackUtils.saveStickerPackMetadata(
//                context,
//                it
//            )
//        };
////        createStickerPack()
//
//    }
}

fun copyAssetsToExternalStorage(context: Context, identifier: String) {//Single Pack Saving
>>>>>>> Stashed changes
    val assetManager = context.assets
    val assetsDir = "sticker" // Folder name in the assets directory
    val files = assetManager.list(assetsDir)
    Log.d("TAG-", "sff: ${files?.size} files found in assets/$assetsDir")
    if (files != null) {
        for (filename in files) {
            val inputStream: InputStream = assetManager.open("$assetsDir/$filename")
            val outFile = File(
                context.getExternalFilesDir(null), // Using getExternalFilesDir for better compatibility
                "$identifier/$filename"
            )
            if (!outFile.parentFile.exists()) {
                outFile.parentFile.mkdirs()
            }
            saveFile(inputStream, outFile)

            // Log file sizes for debugging
            val assetFileSize = assetManager.openFd("$assetsDir/$filename").length
            val externalFileSize = outFile.length()
            Log.d(
                "TAG-",
                "File copied: $filename, Asset size: $assetFileSize, External size: $externalFileSize"
            )

            // Compare image properties
            val assetBitmap = BitmapFactory.decodeStream(assetManager.open("$assetsDir/$filename"))
            val externalBitmap = BitmapFactory.decodeFile(outFile.absolutePath)
            val assetHeight = assetBitmap.height
            val externalHeight = externalBitmap.height
            Log.d(
                "TAG-",
                "File: $filename, Asset height: $assetHeight, External height: $externalHeight"
            )
        }
<<<<<<< Updated upstream
=======
        loadStickerPack(context, identifier)?.let {
            StickerPackUtils.saveStickerPackMetadata(
                context, it
            )
        };
//        createStickerPack()

>>>>>>> Stashed changes
    }
}

fun initPacks(context: Context) {
    notifyMediaScanner(context)
}

fun saveFile(inputStream: InputStream, outFile: File) {
    val buffer = ByteArray(1024)
    var length: Int
    try {
        val outputStream = FileOutputStream(outFile)
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

<<<<<<< Updated upstream
=======
private fun getStickerPackMetadataList(context: Context): List<StickerPackMetadata> {
    Log.e(TAG, "Pertkoi 1--> " + Gson().toJson(StickerContentProvider.stickerPackList))

    if (StickerContentProvider.stickerPackList == null) {
        StickerContentProvider.stickerPackList = loadAllStickerPacksVisJson(context)
        Log.e(TAG, "Pertkoi 2--> " + Gson().toJson(StickerContentProvider.stickerPackList))
    } else if (StickerContentProvider.stickerPackList.size == 0) {
        StickerContentProvider.stickerPackList = loadAllStickerPacksVisJson(context)
        Log.e(TAG, "Pertkoi 3--> " + Gson().toJson(StickerContentProvider.stickerPackList))
    }
    Log.e(
        TAG,
        "Pertkoi 4--> " + Gson().toJson(StickerContentProvider.stickerPackList) + "   #  " + StickerContentProvider.stickerPackList.size
    )
    return StickerContentProvider.stickerPackList
}

fun notifyMediaScanner(context: Context) {
    getStickerPackMetadataList(context)
    StickerContentProvider().notifyStickerPack()
//    context.contentResolver.notifyChange(StickerPackUtils.getStickersURI())
//    val uri = Uri.fromFile(file)
//    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
}
>>>>>>> Stashed changes

fun createStickerPack(
    identifier: String,
    name: String,
    publisher: String,
    trayImageFile: String,
    stickerFilePaths: List<String>,
    context: Context
): StickerPackMetadata {
    val stickers = stickerFilePaths.map { Sticker(it, ArrayList()) }
    val metadata = StickerPackMetadata(
        identifier,
        name,
        publisher,
        trayImageFile,
        stickers[0].imageFile.endsWith(".gif"),
        stickers
    )


    // Save metadata and stickers
    StickerPackUtils.saveStickerPackMetadata(context, metadata);

//    val contentProvider = StickerContentProvider().apply { attachInfo(context, null) }
//    contentProvider.addStickerPack(stickerPack)
    return metadata
}

//    val contentUri = Uri.parse("content://com.kp.bright.whatsapptickers.stickerprovider/metadata")
fun Activity.addStickerPackToWhatsApp(
    stickerPackIdentifier: String?, stickerPackName: String?
) {
//    validateStickerPack(this, stickerPackIdentifier.toString())

    val intent = Intent()
//    intent.setPackage("com.whatsapp")
    intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK")
    intent.putExtra("sticker_pack_id", stickerPackIdentifier)
    intent.putExtra("sticker_pack_authority", "com.kp.bright.whatsapptickers.stickerprovider")
    intent.putExtra("sticker_pack_name", stickerPackName)


    try {
        startActivityForResult(intent, ADD_PACK)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
    }
}

//fun addStickerPackToWhatsApp(context: Context, identifier: String, packName: String) {
//    validateStickerPack(context, identifier)
//    val intent = Intent().apply {
//        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
//        putExtra("sticker_pack_id", identifier)
//        putExtra("sticker_pack_authority", "${context.packageName}.provider")
//        putExtra("sticker_pack_name", packName)  // Add your sticker pack name
//    }
//
//    try {
//        (context as Activity).startActivityForResult(intent, 200)
//    } catch (e: Exception) {
//        // Handle exception
//        e.printStackTrace()
//    }
//}

@SuppressLint("Range")
fun validateStickerPack(context: Context, identifier: String) {
    val contentResolver = StickerContentProvider()
    val uri = Uri.parse("content://${context.packageName}.stickerprovider/metadata")

    try {
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    val packName = cursor.getString(cursor.getColumnIndex("sticker_pack_name"))
                    val publisher =
                        cursor.getString(cursor.getColumnIndex("sticker_pack_publisher"))
                    val trayIcon = cursor.getString(cursor.getColumnIndex("sticker_pack_tray_icon"))

                    Log.d("StickerPackValidator", "Sticker Pack ID: $identifier")
                    Log.d("StickerPackValidator", "Pack Name: $packName")
                    Log.d("StickerPackValidator", "Publisher: $publisher")
                    Log.d("StickerPackValidator", "Tray Icon: $trayIcon")
                } else {
                    Log.e(
                        "StickerPackValidator",
                        "Sticker pack not found with identifier: $identifier"
                    )
                }
            } finally {
                cursor.close()
            }
        } else {
            Log.e("StickerPackValidator", "Cursor is null")
        }
    } catch (e: Exception) {
        Log.e("StickerPackValidator", "Error validating sticker pack: ${e.localizedMessage}")
    }
}
