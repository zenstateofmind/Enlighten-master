package com.example.nikhiljoshi.enlighten.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.nikhiljoshi.enlighten.data.EnlightenDbHelper;

import static com.example.nikhiljoshi.enlighten.data.EnlightenContract.*;

/**
 * Created by nikhiljoshi on 6/2/16.
 */
public class EnlightenProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int FRIENDS_WITH_CURRENT_USERID = 100;
    private static final int FRIEND_WITH_CURRENT_USERID_AND_FRIEND_USER_ID = 101;
    private static final int FRIENDS_WITH_CURRENT_USERID_AND_PACKID = 102;
    private static final int PACKS_WITH_CURRENT_USERID = 103;
    private static final int FRIEND = 105;
    private static final int PACK = 106;
    private static final int PACK_WITH_CURRENT_USERID_AND_PARENT_PACKID = 107;
    private static final int PACK_WITH_CURRENT_USERID_AND_PACKID = 108;

    private EnlightenDbHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        mOpenHelper = new EnlightenDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match) {
            case FRIENDS_WITH_CURRENT_USERID_AND_PACKID: {
                Long currentUserId = FriendEntry.getCurrentUserIdFromPackUri(uri);
                Long packId = FriendEntry.getPackIdFromPackUri(uri);

                // Android has this weird issue where you cannot query null in the database for some weird reason.
                // Hence hashing out the logic right here, before inserting the information into the query
                String selectionQuery = packId == -1 ? FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + FriendEntry.COLUMN_PACK_KEY + " IS NULL "
                                                : FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + FriendEntry.COLUMN_PACK_KEY + " = ? ";


                String[] selectionArgsQuery = packId == -1 ? new String[]{currentUserId + ""} : new String[]{currentUserId + "", packId + ""};

                cursor = db.query(FriendEntry.TABLE_NAME,
                        projection,
                        selectionQuery,
                        selectionArgsQuery,
                        sortOrder,
                        null,
                        null);

                break;
            }
            case FRIENDS_WITH_CURRENT_USERID: {
                Long currentUserId = FriendEntry.getCurrentUserIdFromFriendUri(uri);

                String selectionQuery = selection == null ? FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? "
                                                          : FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + selection;

                String[] selectionArgsQuery = selectionArgs == null ? new String[1]
                                                                    : new String[selectionArgs.length + 1];
                selectionArgsQuery[0] = currentUserId + "";
                if (selectionArgs != null) {
                    for (int i = 0; i < selectionArgs.length; i++) {
                        selectionArgsQuery[i + 1] = selectionArgs[i];
                    }
                }

                cursor = db.query(FriendEntry.TABLE_NAME,
                        projection,
                        selectionQuery,
                        selectionArgsQuery,
                        sortOrder,
                        null,
                        null);

                break;
            } case FRIEND_WITH_CURRENT_USERID_AND_FRIEND_USER_ID: {
                Long currentUserId = FriendEntry.getCurrentUserIdFromFriendUri(uri);
                long friendUserId = FriendEntry.getFriendUserIDFromFriendUri(uri);

                cursor = db.query(FriendEntry.TABLE_NAME,
                        projection,
                        FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " +
                                FriendEntry.COLUMN_USER_ID + " = ?",
                        new String[]{currentUserId + "", friendUserId + ""},
                        sortOrder,
                        null,
                        null);

                break;
            } case PACKS_WITH_CURRENT_USERID: {
                Long currentUserId = PackEntry.getCurrentUserIdFromPackUri(uri);
                cursor = db.query(PackEntry.TABLE_NAME,
                                  projection,
                                  PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? ",
                                  new String[]{currentUserId + ""},
                                  sortOrder,
                                  null,
                                  null);
                break;
            } case PACK_WITH_CURRENT_USERID_AND_PARENT_PACKID: {
                final long currentUserId = PackEntry.getCurrentUserIdFromParentPackIdUri(uri);
                final long parentPackId = PackEntry.getParentPackIdFromParentPackIdUri(uri);
                String selectionQuery = parentPackId == -1 ? PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + PackEntry.COLUMN_PACK_PARENT_ID + " IS NULL "
                                                           : PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + PackEntry.COLUMN_PACK_PARENT_ID + " = ? ";
                String[] selectionArgsQuery = parentPackId == -1 ? new String[]{currentUserId + ""} : new String[] {currentUserId + "", parentPackId + ""};
                cursor = db.query(PackEntry.TABLE_NAME,
                                    projection,
                                    selectionQuery,
                                    selectionArgsQuery,
                                    sortOrder,
                                    null,
                                    null);
                break;
            } case PACK_WITH_CURRENT_USERID_AND_PACKID: {
                final long userId = PackEntry.getCurrentUserIdFromPackIdUri(uri);
                final long packId = PackEntry.getPackIdFromPackIdUri(uri);
                cursor = db.query(PackEntry.TABLE_NAME,
                                    projection,
                                    PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + PackEntry._ID + " = ? ",
                                    new String[]{userId + "", packId + ""},
                                    sortOrder,
                                    null,
                                    null);
                break;
            } default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FRIENDS_WITH_CURRENT_USERID:
                return FriendEntry.CONTENT_TYPE;
            case FRIEND_WITH_CURRENT_USERID_AND_FRIEND_USER_ID:
                return FriendEntry.CONTENT_ITEM_TYPE;
            case FRIENDS_WITH_CURRENT_USERID_AND_PACKID:
                return FriendEntry.CONTENT_TYPE;
            case PACKS_WITH_CURRENT_USERID:
                return PackEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Uri: " + uri + " doesnt have a type");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FRIEND: {
                long id = db.insert(FriendEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = FriendEntry.buildFriendUriWithInsertedRowId(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row for: " + uri);
                }
                break;

            } case PACK: {
                long id = db.insert(PackEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = PackEntry.buildPackUriWithInsertedRowId(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row for: " + uri);
                }
                break;
            } default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsDeleted = 0;

        switch (match) {
            case FRIENDS_WITH_CURRENT_USERID : {
                Long currentUserId = FriendEntry.getCurrentUserIdFromFriendUri(uri);
                numRowsDeleted = db.delete(FriendEntry.TABLE_NAME,
                        FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ?",
                        new String[]{currentUserId + ""});
                break;
            } case FRIEND_WITH_CURRENT_USERID_AND_FRIEND_USER_ID: {
                long currentUserId = FriendEntry.getCurrentUserIdFromFriendUri(uri);
                long friendUserId = FriendEntry.getFriendUserIDFromFriendUri(uri);
                numRowsDeleted = db.delete(FriendEntry.TABLE_NAME,
                        FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " +
                                FriendEntry.COLUMN_USER_ID + " = ? ",
                        new String[]{currentUserId + "", friendUserId + ""});
                break;
            } case PACKS_WITH_CURRENT_USERID: {
                Long currentUserId = PackEntry.getCurrentUserIdFromPackUri(uri);
                numRowsDeleted = db.delete(PackEntry.TABLE_NAME,
                          PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ?" ,
                          new String[]{currentUserId + ""});
                break;
            } case PACK_WITH_CURRENT_USERID_AND_PACKID: {
                final long currenUserId = PackEntry.getCurrentUserIdFromPackIdUri(uri);
                final long packId = PackEntry.getPackIdFromPackIdUri(uri);
                numRowsDeleted = db.delete(PackEntry.TABLE_NAME,
                                            PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + PackEntry._ID + " = ? ",
                                            new String[]{currenUserId + "", packId + ""});
                break;

            } case FRIENDS_WITH_CURRENT_USERID_AND_PACKID : {
                final long currentUserId = FriendEntry.getCurrentUserIdFromPackUri(uri);
                final long packId = FriendEntry.getPackIdFromPackUri(uri);
                numRowsDeleted = db.delete(FriendEntry.TABLE_NAME,
                        FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " + FriendEntry.COLUMN_PACK_KEY + " = ? ",
                        new String[]{currentUserId + "", packId + ""});
                break;

            } default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsUpdated = 0;

        switch (match) {
            case FRIENDS_WITH_CURRENT_USERID : {
                Long currentUserId = FriendEntry.getCurrentUserIdFromFriendUri(uri);
                numRowsUpdated = db.update(FriendEntry.TABLE_NAME,
                        values,
                        FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ?",
                        new String[]{currentUserId + ""});
                break;
            } case FRIEND_WITH_CURRENT_USERID_AND_FRIEND_USER_ID: {
                Long currentUserId = FriendEntry.getCurrentUserIdFromFriendUri(uri);
                Long friendUserId = FriendEntry.getFriendUserIDFromFriendUri(uri);
                numRowsUpdated = db.update(FriendEntry.TABLE_NAME,
                        values,
                        FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " = ? AND " +
                                FriendEntry.COLUMN_USER_ID + " = ? ",
                        new String[]{currentUserId + "", friendUserId + ""});
                break;
            } default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numRowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;

    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FRIEND, FRIEND);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_PACK, PACK);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_PACK + "/#", PACKS_WITH_CURRENT_USERID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FRIEND + "/#" , FRIENDS_WITH_CURRENT_USERID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FRIEND + "/#/" + PATH_PACK + "/*", FRIENDS_WITH_CURRENT_USERID_AND_PACKID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FRIEND + "/#/*", FRIEND_WITH_CURRENT_USERID_AND_FRIEND_USER_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_PACK + "/#/" + PATH_PACK + "/*", PACK_WITH_CURRENT_USERID_AND_PACKID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_PACK + "/#/*", PACK_WITH_CURRENT_USERID_AND_PARENT_PACKID);

        return uriMatcher;
    }

}
