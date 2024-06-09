package com.kp.bright.whatsapptickers.stickersmanage

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.kp.bright.whatsapptickers.whatsappsticker.StickerContentProvider
import com.kp.bright.whatsapptickers.whatsappsticker.StickerPackMetadata
import com.kp.bright.whatsapptickers.whatsappsticker.loadAllStickerPacksVisJson
import java.io.File
import java.io.FileWriter
import java.io.IOException

object StickerPackUtils {
    @JvmField
    var TAG: String = "KNS_STICKER-->"

    fun getMetadataURI(identifier: String?): Uri {
        return StickerContentProvider.AUTHORITY_URI.appendPath(StickerContentProvider.METADATA)
            .build()
    }

    fun getStickersURI(identifier: String?): Uri {
        return StickerContentProvider.AUTHORITY_URI.appendPath(StickerContentProvider.STICKERS)
            .appendPath(identifier).build()
    }

    fun getStickers_AssetsURI(identifier: String?, stickerName: String?): Uri {
        return StickerContentProvider.AUTHORITY_URI.appendPath(StickerContentProvider.STICKERS_ASSET)
            .appendPath(identifier).appendPath(stickerName).build()
    }

    fun saveStickerPackMetadata(context: Context, metadata: StickerPackMetadata) {
        // Create JSON structure
        val stickerPacks = loadAllStickerPacksVisJson(context)
        var s = stickerPacks.find { it.identifier == metadata.identifier }

        if (s != null) {
            return
        }
        stickerPacks.add(metadata)

        //        StickerPackJson stickerPackJson = new StickerPackJson();
//        stickerPackJson.sticker_packs = stickerPacks;

        // Convert to JSON
        val gson = Gson()
        val json = gson.toJson(stickerPacks)

        // Create the stickers directory if it doesn't exist
        val stickersDir = File(context.getExternalFilesDir(null), "stickers")

        if (!stickersDir.exists()) {
            val dirCreated = stickersDir.mkdirs()
            if (!dirCreated) {
                Log.w(TAG, "Failed to create stickers directory: " + stickersDir.absolutePath)
                return
            }
        }

        // Save JSON to file
        val file = File(stickersDir, "sticker_packs.json")
        Log.w(TAG, "saveStickerPackMetadata: " + file.absolutePath)
        try {
            FileWriter(file).use { writer ->
                writer.write(json)
            }
            Log.wtf(TAG, "saveStickerPackMetadata: $json")
        } catch (e: IOException) {
            Log.w(TAG, "IOException: " + file.absolutePath)
            e.printStackTrace()
        }
    }


    // Class representing the structure of the JSON file
    private class StickerPackJson {
        var sticker_packs: List<StickerPackMetadata>? = null
    }
}
