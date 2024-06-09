package com.kp.bright.whatsapptickers.whatsappsticker;

import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GifToWebPConverter {

    public static void convertGifToWebP(Context context, String inputAssetPath, String outputPath, ConversionCallback callback) {
        File outputDir = new File(outputPath).getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Copy input GIF file from assets to a temporary location
        String tempInputPath = outputDir.getAbsolutePath() + "/temp.gif";
        copyAssetToFile(context, inputAssetPath, tempInputPath);

        String[] command = {
                "-i", tempInputPath,
                "-vcodec", "libwebp",
                "-filter:v", "fps=fps=15",
                "-lossless", "0",
                "-loop", "0",
                "-preset", "default",
                "-an",
                "-vsync", "0",
                "-s", "512:512",
                outputPath
        };

        Config.enableLogCallback(message -> Log.d(Config.TAG, message.getText()));
        Config.enableStatisticsCallback(statistics -> Log.d(Config.TAG, statistics.toString()));

        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            // Delete temporary input file
            new File(tempInputPath).delete();

            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                callback.onSuccess(outputPath);
            } else {
                callback.onFailure("Conversion failed with return code: " + returnCode);
            }
        });
    }

    private static void copyAssetToFile(Context context, String assetPath, String outputPath) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(assetPath);
            FileOutputStream outputStream = new FileOutputStream(outputPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ConversionCallback {
        void onSuccess(String outputPath);
        void onFailure(String error);
    }
}

