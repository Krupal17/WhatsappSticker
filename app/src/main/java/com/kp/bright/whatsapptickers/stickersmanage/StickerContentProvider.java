package com.kp.bright.whatsapptickers.stickersmanage;

import static com.kp.bright.whatsapptickers.stickersmanage.StickerPackUtils.*;
import static com.kp.bright.whatsapptickers.whatsappsticker.UtilsKt.loadAllStickerPacks;

import static java.lang.System.in;
import static java.lang.System.out;

import android.content.ContentProvider;
import android.content.ContentValues;
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

import com.google.gson.Gson;
import com.kp.bright.whatsapptickers.whatsappsticker.Sticker;
import com.kp.bright.whatsapptickers.whatsappsticker.StickerPackMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

//public class StickerContentProvider extends ContentProvider {
//    public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
//    public static final String STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name";
//    public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
//    public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
//    public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
//    public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
//    public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
//    public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
//    public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
//    public static final String LICENSE_AGREEMENT_WEBSITE = "sticker_pack_license_agreement_website";
//    public static final String IMAGE_DATA_VERSION = "image_data_version";
//    public static final String AVOID_CACHE = "whatsapp_will_not_cache_stickers";
//    public static final String ANIMATED_STICKER_PACK = "animated_sticker_pack";
//
//    private static final String AUTHORITY = "com.kp.bright.whatsapptickers.stickerprovider";
//    private static final UriMatcher uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
//    private List<StickerPackMetadata> stickerPackList;
//
//    private static final String METADATA = "metadata";
//    private static final int METADATA_CODE = 1;
//    private static final int METADATA_CODE_FOR_SINGLE_PACK = 2;
//    static final String STICKERS = "stickers";
//    private static final int STICKERS_CODE = 3;
//    static final String STICKERS_ASSET = "stickers_asset";
//    private static final int STICKERS_ASSET_CODE = 4;
//    private static final int STICKER_PACK_TRAY_ICON_CODE = 5;
//
//    @Override
//    public boolean onCreate() {
//        stickerPackList = getStickerPackList();
//
//        if (!AUTHORITY.startsWith( Objects.requireNonNull( getContext() ).getPackageName() )) {
//            throw new IllegalStateException( "your authority (" + AUTHORITY + ") for the content provider should start with your package name: " + getContext().getPackageName() );
//        }
//        uriMatcher.addURI( AUTHORITY, METADATA, METADATA_CODE );
//        uriMatcher.addURI( AUTHORITY, METADATA + "/*", METADATA_CODE_FOR_SINGLE_PACK );
//        uriMatcher.addURI( AUTHORITY, STICKERS + "/*", STICKERS_CODE );
//        uriMatcher.addURI( AUTHORITY, STICKERS_ASSET + "/*/" + "trayicon.webp", STICKER_PACK_TRAY_ICON_CODE );
//        uriMatcher.addURI( AUTHORITY, STICKERS_ASSET + "/*/*", STICKERS_ASSET_CODE );
//
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Log.e( TAG, "query: ---- " + uri + "     " + uriMatcher.match( uri ) );
//        switch (uriMatcher.match( uri )) {
//            case METADATA_CODE:
//                Log.e( TAG, "getStickerPackMetadataCursor: 1" );
//                return getStickerPackMetadataCursor();
//            case METADATA_CODE_FOR_SINGLE_PACK:
//                Log.e( TAG, "getSingleStickerPackMetadataCursor: 1" );
//
//                return getSingleStickerPackMetadataCursor( uri );
//            case STICKERS_CODE:
//                return getStickersCursor( uri );
//            default:
//                throw new IllegalArgumentException( "Unknown URI: " + uri );
//        }
//    }
//
//    private Cursor getStickerPackMetadataCursor() {
//        Log.e( TAG, "getStickerPackMetadataCursor: 2" );
//
//        MatrixCursor cursor = new MatrixCursor( new String[]{
//                STICKER_PACK_IDENTIFIER_IN_QUERY, STICKER_PACK_NAME_IN_QUERY,
//                STICKER_PACK_PUBLISHER_IN_QUERY, STICKER_PACK_ICON_IN_QUERY,
//                ANDROID_APP_DOWNLOAD_LINK_IN_QUERY, IOS_APP_DOWNLOAD_LINK_IN_QUERY,
//                PUBLISHER_EMAIL, PUBLISHER_WEBSITE, PRIVACY_POLICY_WEBSITE,
//                LICENSE_AGREEMENT_WEBSITE, IMAGE_DATA_VERSION, AVOID_CACHE,
//                ANIMATED_STICKER_PACK
//        } );
//
//        for (StickerPackMetadata pack : stickerPackList) {
//            cursor.addRow( new Object[]{
//                    pack.getIdentifier(), pack.getName(), pack.getPublisher(),
//                    pack.getTrayImageFile(), "", "", "", "", "", "", "1",
//                    false, pack.getAnimated()
//            } );
//        }
//        Log.e( TAG, "getStickerPackMetadataCursor: 3" + (cursor.getCount()) );
//        return cursor;
//    }
//
//    private Cursor getSingleStickerPackMetadataCursor(Uri uri) {
//        String identifier = uri.getLastPathSegment();
//        for (StickerPackMetadata pack : stickerPackList) {
//            if (pack.getIdentifier().equals( identifier )) {
//                MatrixCursor cursor = new MatrixCursor( new String[]{
//                        STICKER_PACK_IDENTIFIER_IN_QUERY, STICKER_PACK_NAME_IN_QUERY,
//                        STICKER_PACK_PUBLISHER_IN_QUERY, STICKER_PACK_ICON_IN_QUERY,
//                        ANDROID_APP_DOWNLOAD_LINK_IN_QUERY, IOS_APP_DOWNLOAD_LINK_IN_QUERY,
//                        PUBLISHER_EMAIL, PUBLISHER_WEBSITE, PRIVACY_POLICY_WEBSITE,
//                        LICENSE_AGREEMENT_WEBSITE, IMAGE_DATA_VERSION, AVOID_CACHE,
//                        ANIMATED_STICKER_PACK
//                } );
//
//                cursor.addRow( new Object[]{
//                        pack.getIdentifier(), pack.getName(), pack.getPublisher(),
//                        pack.getTrayImageFile(), "", "", "", "", "", "", "1",
//                        false, pack.getAnimated()
//                } );
//                cursor.setNotificationUri( Objects.requireNonNull( getContext() ).getContentResolver(), uri );
//                return cursor;
//            }
//        }
//        return new MatrixCursor( new String[0] );
//    }
//
//    private Cursor getStickersCursor(Uri uri) {
//        List<String> segments = uri.getPathSegments();
//        String identifier = segments.get( 1 );
//
//        MatrixCursor cursor = new MatrixCursor( new String[]{"sticker_file_name", "sticker_emoji"} );
//        for (StickerPackMetadata pack : stickerPackList) {
//            if (pack.getIdentifier().equals( identifier )) {
//                for (Sticker sticker : pack.getStickers()) {
//                    File stickerFile = new File( getContext().getExternalFilesDir( null ), "Demo2/" + identifier + "/" + sticker.getImageFile() );
//                    Uri stickerUri = Uri.fromFile( stickerFile );
//                    cursor.addRow( new Object[]{stickerUri.toString(), new Gson().toJson( sticker.getEmojis() )} );
//                }
//                cursor.setNotificationUri( Objects.requireNonNull( getContext() ).getContentResolver(), uri );
//                return cursor;
//            }
//        }
//        return new MatrixCursor( new String[0] );
//    }
//
//    private List<StickerPackMetadata> getStickerPackList() {
//        if (stickerPackList == null) {
//            stickerPackList = loadAllStickerPacks( Objects.requireNonNull( getContext() ) );
//        }
//        return stickerPackList;
//    }
//
//    @Override
//    public String getType(@NonNull Uri uri) {
//        final int matchCode = uriMatcher.match( uri );
//        Log.e( TAG, "getType: " + matchCode );
//        switch (matchCode) {
//            case METADATA_CODE:
//                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + METADATA;
//            case METADATA_CODE_FOR_SINGLE_PACK:
//                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + METADATA;
//            case STICKERS_CODE:
//                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + STICKERS;
//            case STICKERS_ASSET_CODE:
//                return "image/webp";
//            case STICKER_PACK_TRAY_ICON_CODE:
//                return "image/webp";
//            default:
//                throw new IllegalArgumentException( "Unknown URI: " + uri );
//        }
//    }
//
//    @Nullable
//    @Override
//    public Uri insert(@NonNull Uri uri, ContentValues values) {
//        throw new UnsupportedOperationException( "Not supported" );
//    }
//
//    @Override
//    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
//        throw new UnsupportedOperationException( "Not supported" );
//    }
//
//    @Override
//    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        throw new UnsupportedOperationException( "Not supported" );
//    }
//
//    @Nullable
//    @Override
//    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) {
//        final int matchCode = uriMatcher.match( uri );
//        if (matchCode == STICKERS_ASSET_CODE || matchCode == STICKER_PACK_TRAY_ICON_CODE) {
//            try {
//                return getImageAsset( uri );
//            } catch (IOException e) {
//                Log.e( TAG, "openAssetFile: ->" + e.getLocalizedMessage() );
//                throw new RuntimeException( e );
//            }
//        }
//        return null;
//    }
//
//    private AssetFileDescriptor getImageAsset(Uri uri) throws IllegalArgumentException, IOException {
//        AssetManager am = Objects.requireNonNull( getContext() ).getAssets();
//        final List<String> pathSegments = uri.getPathSegments();
//        Log.e( TAG, "getImageAsset: ---->segment" + new Gson().toJson( pathSegments ) );
//        if (pathSegments.size() != 3) {
//            throw new IllegalArgumentException( "path segments should be 3, uri is: " + uri );
//        }
//        String fileName = pathSegments.get( pathSegments.size() - 1 );
//        final String identifier = pathSegments.get( pathSegments.size() - 2 );
//        if (TextUtils.isEmpty( identifier )) {
//            throw new IllegalArgumentException( "identifier is empty, uri: " + uri );
//        }
//        if (TextUtils.isEmpty( fileName )) {
//            throw new IllegalArgumentException( "file name is empty, uri: " + uri );
//        }
//        //making sure the file that is trying to be fetched is in the list of stickers.
//        for (StickerPackMetadata stickerPack : getStickerPackList()) {
//            if (identifier.equals( stickerPack.getIdentifier() )) {
//                if (fileName.equals( stickerPack.getTrayImageFile() )) {
//                    return fetchFile( uri, am, fileName, identifier );
//                } else {
//                    for (Sticker sticker : stickerPack.getStickers()) {
//                        if (fileName.equals( sticker.getImageFile() )) {
//                            return fetchFile( uri, am, fileName, identifier );
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    //    private AssetFileDescriptor fetchFile(@NonNull Uri uri, @NonNull AssetManager am, @NonNull String fileName, @NonNull String identifier) {
////        try {
////            return am.openFd(identifier + "/" + fileName);
////        } catch (IOException e) {
////            Log.e(Objects.requireNonNull(getContext()).getPackageName(), "IOException when getting asset file, uri:" + uri, e);
////            return null;
////        }
////    }
//    private AssetFileDescriptor fetchFile(@NonNull Uri uri, @NonNull AssetManager am, @NonNull String fileName, @NonNull String identifier) throws IOException {
//
//        // External directory where your stickers are saved
//        final File externalDir = new File( getContext().getExternalFilesDir( null ), identifier );
//
//        // Create a File object for the sticker file
//        final File file = new File( externalDir, fileName );
//
//        // Check if the file exists
//        if (!file.exists()) {
//            throw new FileNotFoundException( "File not found: " + file.getAbsolutePath() );
//        }
//
//        // Open and return AssetFileDescriptor
//        return new AssetFileDescriptor( ParcelFileDescriptor.open( file, ParcelFileDescriptor.MODE_READ_ONLY ), 0, AssetFileDescriptor.UNKNOWN_LENGTH );
//    }
//
////    @Nullable
////    @Override
////    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
////        int match = uriMatcher.match(uri);
////        switch (match) {
////            case STICKER_PACK_TRAY_ICON_CODE:
////                return getFileDescriptorForSticker(uri);
////            case STICKERS_ASSET_CODE:
////                return getFileDescriptorForSticker(uri);
////            default:
////                throw new FileNotFoundException("No files supported by provider at " + uri);
////        }
////    }
////
////    private ParcelFileDescriptor getFileDescriptorForSticker(Uri uri) throws FileNotFoundException {
////        List<String> segments = uri.getPathSegments();
////        if (segments.size() != 3) {
////            throw new FileNotFoundException("Invalid URI: " + uri);
////        }
////        String packName = segments.get(1);
////        String fileName = segments.get(2);
////        File stickerFile = new File(getContext().getExternalFilesDir(null), "Demo2/" + packName + "/" + fileName);
////        if (!stickerFile.exists()) {
////            throw new FileNotFoundException("File not found: " + stickerFile.getAbsolutePath());
////        }
////
////        return ParcelFileDescriptor.open(stickerFile, ParcelFileDescriptor.MODE_READ_ONLY);
////    }
//}
