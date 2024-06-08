package com.kp.bright.whatsapptickers.stickersmanage;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kp.bright.whatsapptickers.whatsappsticker.StickerPackMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StickerPackUtils {

    public static String TAG = "KNS_STICKER-->";

    public static void saveStickerPackMetadata(Context context, StickerPackMetadata metadata) {
        // Create JSON structure
        List<StickerPackMetadata> stickerPacks = new ArrayList<>();
        stickerPacks.add( metadata );
//        StickerPackJson stickerPackJson = new StickerPackJson();
//        stickerPackJson.sticker_packs = stickerPacks;

        // Convert to JSON
        Gson gson = new Gson();
        String json = gson.toJson( stickerPacks );

        // Create the stickers directory if it doesn't exist
        File stickersDir = new File( context.getExternalFilesDir(null), "stickers" );
        if (!stickersDir.exists()) {
            boolean dirCreated = stickersDir.mkdirs();
            if (!dirCreated) {
                Log.w( TAG, "Failed to create stickers directory: " + stickersDir.getAbsolutePath() );
                return;
            }
        }

        // Save JSON to file
        File file = new File( stickersDir, "sticker_packs.json" );
        Log.w( TAG, "saveStickerPackMetadata: " + file.getAbsolutePath() );
        try (FileWriter writer = new FileWriter( file )) {
            writer.write( json );
        } catch (IOException e) {
            Log.w( TAG, "IOException: " + file.getAbsolutePath() );
            e.printStackTrace();
        }
    }


    // Class representing the structure of the JSON file
    private static class StickerPackJson {
        List<StickerPackMetadata> sticker_packs;
    }
}
