package com.kp.bright.whatsapptickers.whatsappsticker

data class StickerPackMetadata(
    val identifier: String,
    val name: String,
    val publisher: String,
    val trayImageFile: String,
    val animated: Boolean,
    val stickers: List<Sticker>
)

data class Sticker(
    val imageFile: String,
    val emojis: List<String>
)
