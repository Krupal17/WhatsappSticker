/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.kp.bright.whatsapptickers.whatsappsticker;

import static com.kp.bright.whatsapptickers.stickersmanage.StickerPackUtils.*;

import static com.kp.bright.whatsapptickers.whatsappsticker.UtilsKt.loadAllStickerPacks;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StickerContentProvider extends ContentProvider {

    /**
     * Do not change the strings listed below, as these are used by WhatsApp. And changing these will break the interface between sticker app and WhatsApp.
     */
    public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
    public static final String STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name";
    public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
    public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
    public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
    public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
    public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
    public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
    public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
    public static final String LICENSE_AGREEMENT_WEBSITE = "sticker_pack_license_agreement_website";
    public static final String IMAGE_DATA_VERSION = "image_data_version";
    public static final String AVOID_CACHE = "whatsapp_will_not_cache_stickers";
    public static final String ANIMATED_STICKER_PACK = "animated_sticker_pack";

    public static final String STICKER_FILE_NAME_IN_QUERY = "sticker_file_name";
    public static final String STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji";
    private static final String CONTENT_FILE_NAME = "contents.json";
    private static final String AUTHORITY = "com.kp.bright.whatsapptickers.stickerprovider";
    public static final Uri AUTHORITY_URI = new Uri.Builder().scheme( ContentResolver.SCHEME_CONTENT ).authority( AUTHORITY ).appendPath( StickerContentProvider.METADATA ).build();

    /**
     * Do not change the values in the UriMatcher because otherwise, WhatsApp will not be able to fetch the stickers from the ContentProvider.
     */
    private static final UriMatcher MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
    private static final String METADATA = "metadata";
    private static final int METADATA_CODE = 1;

    private static final int METADATA_CODE_FOR_SINGLE_PACK = 2;

    static final String STICKERS = "stickers";
    private static final int STICKERS_CODE = 3;

    static final String STICKERS_ASSET = "stickers_asset";
    private static final int STICKERS_ASSET_CODE = 4;

    private static final int STICKER_PACK_TRAY_ICON_CODE = 5;

    private List<StickerPackMetadata> stickerPackList;

    @Override
    public boolean onCreate() {
        final String authority = AUTHORITY;
        if (!authority.startsWith( Objects.requireNonNull( getContext() ).getPackageName() )) {
            throw new IllegalStateException( "your authority (" + authority + ") for the content provider should start with your package name: " + getContext().getPackageName() );
        }

        //the call to get the metadata for the sticker packs.
        MATCHER.addURI( authority, METADATA, METADATA_CODE );

        //the call to get the metadata for single sticker pack. * represent the identifier
        MATCHER.addURI( authority, METADATA + "/*", METADATA_CODE_FOR_SINGLE_PACK );

        //gets the list of stickers for a sticker pack, * represent the identifier.
        MATCHER.addURI( authority, STICKERS + "/*", STICKERS_CODE );
        Log.e( TAG, "onCreate: 111 " );


        for (StickerPackMetadata stickerPack : getStickerPackMetadataList()) {
            MATCHER.addURI( authority, STICKERS_ASSET + "/" + stickerPack.getIdentifier() + "/" + stickerPack.getTrayImageFile(), STICKER_PACK_TRAY_ICON_CODE );
            for (Sticker sticker : stickerPack.getStickers()) {
                Log.e( TAG, "onCreate: " + sticker.getImageFile() );
                MATCHER.addURI( authority, STICKERS_ASSET + "/" + stickerPack.getIdentifier() + "/" + sticker.getImageFile(), STICKERS_ASSET_CODE );
            }
        }

        return true;
    }

<<<<<<< Updated upstream
=======

    void notifyStickerPack() {
        for (StickerPackMetadata stickerPack : stickerPackList) {
            MATCHER.addURI( AUTHORITY, STICKERS_ASSET + "/" + stickerPack.getIdentifier() + "/" + stickerPack.getTrayImageFile(), STICKER_PACK_TRAY_ICON_CODE );
            for (Sticker sticker : stickerPack.getStickers()) {
                Log.e( TAG, "onCreate: " + sticker.getImageFile() );
                MATCHER.addURI( AUTHORITY, STICKERS_ASSET + "/" + stickerPack.getIdentifier() + "/" + sticker.getImageFile(), STICKERS_ASSET_CODE );
            }
        }
    }

>>>>>>> Stashed changes
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.e( TAG, "query: " + uri  );
        final int code = MATCHER.match( uri );
        if (code == METADATA_CODE) {
            return getPackForAllStickerPackMetadatas( uri );
        } else if (code == METADATA_CODE_FOR_SINGLE_PACK) {
            return getCursorForSingleStickerPackMetadata( uri );
        } else if (code == STICKERS_CODE) {
            return getStickersForAStickerPackMetadata( uri );
        } else {
            throw new IllegalArgumentException( "Unknown URI: " + uri );
        }
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) {
        final int matchCode = MATCHER.match( uri );
        if (matchCode == STICKERS_ASSET_CODE || matchCode == STICKER_PACK_TRAY_ICON_CODE) {
            try {
                return getImageAsset( uri );
            } catch (IOException e) {
                Log.e( TAG, "openAssetFile: " + uri );
                throw new RuntimeException( e );
            }
        }
        return null;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int matchCode = MATCHER.match( uri );
        switch (matchCode) {
            case METADATA_CODE:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + METADATA;
            case METADATA_CODE_FOR_SINGLE_PACK:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + METADATA;
            case STICKERS_CODE:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + STICKERS;
            case STICKERS_ASSET_CODE:
                return "image/webp";
            case STICKER_PACK_TRAY_ICON_CODE:
                return "image/png";
            default:
                throw new IllegalArgumentException( "Unknown URI: " + uri );
        }
    }

    private List<StickerPackMetadata> getStickerPackMetadataList() {
        if (stickerPackList == null) {
            stickerPackList = loadAllStickerPacks( Objects.requireNonNull( getContext() ) );
        }
        return stickerPackList;
    }

    private Cursor getPackForAllStickerPackMetadatas(@NonNull Uri uri) {
        return getStickerPackMetadataInfo( uri, getStickerPackMetadataList() );
    }

    private Cursor getCursorForSingleStickerPackMetadata(@NonNull Uri uri) {
        final String identifier = uri.getLastPathSegment();
        for (StickerPackMetadata stickerPack : getStickerPackMetadataList()) {
            if (identifier.equals( stickerPack.getIdentifier() )) {
                return getStickerPackMetadataInfo( uri, Collections.singletonList( stickerPack ) );
            }
        }

        return getStickerPackMetadataInfo( uri, new ArrayList<>() );
    }

    @NonNull
    private Cursor getStickerPackMetadataInfo(@NonNull Uri uri, @NonNull List<StickerPackMetadata> stickerPackList) {
        MatrixCursor cursor = new MatrixCursor(
                new String[]{
                        STICKER_PACK_IDENTIFIER_IN_QUERY,
                        STICKER_PACK_NAME_IN_QUERY,
                        STICKER_PACK_PUBLISHER_IN_QUERY,
                        STICKER_PACK_ICON_IN_QUERY,
                        ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                        IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                        PUBLISHER_EMAIL,
                        PUBLISHER_WEBSITE,
                        PRIVACY_POLICY_WEBSITE,
                        LICENSE_AGREEMENT_WEBSITE,
                        IMAGE_DATA_VERSION,
                        AVOID_CACHE,
                        ANIMATED_STICKER_PACK,
                } );
        for (StickerPackMetadata stickerPack : stickerPackList) {
            MatrixCursor.RowBuilder builder = cursor.newRow();
            builder.add( stickerPack.getIdentifier() );
            builder.add( stickerPack.getName() );
            builder.add( stickerPack.getPublisher() );
            builder.add( stickerPack.getTrayImageFile() );
            builder.add( "" );
            builder.add( "" );
            builder.add( "" );
            builder.add( "" );
            builder.add( "" );
            builder.add( "" );
            builder.add( "1" );
            builder.add( 0 );
            builder.add( stickerPack.getAnimated() ? 1 : 0 );
        }
        cursor.setNotificationUri( Objects.requireNonNull( getContext() ).getContentResolver(), uri );
        return cursor;
    }

    @NonNull
    private Cursor getStickersForAStickerPackMetadata(@NonNull Uri uri) {
        final String identifier = uri.getLastPathSegment();
        MatrixCursor cursor = new MatrixCursor( new String[]{STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY} );
        for (StickerPackMetadata stickerPack : getStickerPackMetadataList()) {
            if (identifier.equals( stickerPack.getIdentifier() )) {
                for (Sticker sticker : stickerPack.getStickers()) {
                    cursor.addRow( new Object[]{sticker.getImageFile(), TextUtils.join( ",", sticker.getEmojis() )} );
                }
            }
        }
        cursor.setNotificationUri( Objects.requireNonNull( getContext() ).getContentResolver(), uri );
        return cursor;
    }

    private AssetFileDescriptor getImageAsset(Uri uri) throws IllegalArgumentException, IOException {
        AssetManager am = Objects.requireNonNull( getContext() ).getAssets();
        final List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() != 3) {
            throw new IllegalArgumentException( "path segments should be 3, uri is: " + uri );
        }
        String fileName = pathSegments.get( pathSegments.size() - 1 );
        final String identifier = pathSegments.get( pathSegments.size() - 2 );
        if (TextUtils.isEmpty( identifier )) {
            throw new IllegalArgumentException( "identifier is empty, uri: " + uri );
        }
        if (TextUtils.isEmpty( fileName )) {
            throw new IllegalArgumentException( "file name is empty, uri: " + uri );
        }
        //making sure the file that is trying to be fetched is in the list of stickers.
        for (StickerPackMetadata stickerPack : getStickerPackMetadataList()) {
            if (identifier.equals( stickerPack.getIdentifier() )) {
                if (fileName.equals( stickerPack.getTrayImageFile() )) {
                    return fetchFile( uri, am, fileName, identifier );
                } else {
                    for (Sticker sticker : stickerPack.getStickers()) {
                        if (fileName.equals( sticker.getImageFile() )) {
                            return fetchFile( uri, am, fileName, identifier );
                        }
                    }
                }
            }
        }
        return null;
    }

//    private AssetFileDescriptor fetchFile(@NonNull Uri uri, @NonNull AssetManager am, @NonNull String fileName, @NonNull String identifier) {
//        try {
//            return am.openFd( identifier + "/" + fileName );
//        } catch (IOException e) {
//            Log.e( Objects.requireNonNull( getContext() ).getPackageName(), "IOException when getting asset file, uri:" + uri, e );
//            return null;
//        }
//    }

    private AssetFileDescriptor fetchFile(@NonNull Uri uri, @NonNull AssetManager am, @NonNull String fileName, @NonNull String identifier) throws IOException {

        // External directory where your stickers are saved
        final File externalDir = new File( getContext().getExternalFilesDir( null ), identifier );

        // Create a File object for the sticker file
        final File file = new File( externalDir, fileName );

        // Check if the file exists
        if (!file.exists()) {
            throw new FileNotFoundException( "File not found: " + file.getAbsolutePath() );
        }

        // Open and return AssetFileDescriptor
        return new AssetFileDescriptor( ParcelFileDescriptor.open( file, ParcelFileDescriptor.MODE_READ_ONLY ), 0, AssetFileDescriptor.UNKNOWN_LENGTH );
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException( "Not supported" );
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException( "Not supported" );
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException( "Not supported" );
    }
}
