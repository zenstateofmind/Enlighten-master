package com.example.nikhiljoshi.enlighten.data.Contract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.nikhiljoshi.enlighten.pojo.Friend;
import com.example.nikhiljoshi.enlighten.pojo.Pack;
import com.twitter.sdk.android.Twitter;

/**
 * Created by nikhiljoshi on 6/2/16.
 */
public class EnlightenContract {

    public static final String CONTENT_AUTHORITY = "com.example.nikhiljoshi.enlighten";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FRIEND = "friend";

    public static final String PATH_PACK = "pack";

    public static final class FriendEntry implements BaseColumns {

        //////////////// table name ///////////////////////
        public static final String TABLE_NAME = "friend";

        ////////////// column names /////////////////////
        public static final String COLUMN_CURRENT_SESSION_USER_ID = "current_session_user_id";

        public static final String COLUMN_USER_ID = "friend_user_id";

        public static final String COLUMN_USER_NAME = "user_name";

        public static final String COLUMN_PROFILE_NAME = "profile_name";

        public static final String COLUMN_PROFILE_PICTURE_URL = "profile_picture_url";

        public static final String COLUMN_PACK_KEY = "pack_id";


        ///////// URI Stuff //////////////
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIEND)
                .build();

        //// Content Type //////
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY;

        //////////// Functions to build URIs/////////////////////

        /**
         * Used to return URI with the row ID of the inserted data
         */
        public static Uri buildFriendUriWithInsertedRowId(long insertedRowId) {
            return ContentUris.withAppendedId(CONTENT_URI, insertedRowId);
        }

        public static Uri buildFriendUriWithCurrentUserId(Long currentSessionUserId) {
            return CONTENT_URI.buildUpon().appendPath(currentSessionUserId + "").build();
        }

        public static Uri buildUriWithCurrentUserIdAndFriendUserId(Long currentSessionUserId,
                                                                   Long friendUserId) {
            return CONTENT_URI.buildUpon().appendPath(currentSessionUserId + "")
                    .appendPath(friendUserId + "").build();
        }

        public static Uri buildUriWithCurrentUserIdAndPackId(Long currentSessionUserId,
                                                             Long packId) {
            return CONTENT_URI.buildUpon().appendPath(currentSessionUserId + "")
                    .appendPath(PATH_PACK)
                    .appendPath(packId + "").build();
        }

        //////////////// Get info from built uri //////////////////

        public static long getCurrentUserIdFromFriendUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getFriendUserIDFromFriendUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getCurrentUserIdFromPackUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getPackIdFromPackUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(3));
        }

        ///////////////// Helper functions ////////////////////

        public static ContentValues getContentValues(Friend user, Long packId) {
            Long packKey = packId != -1 ? packId : null;
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_CURRENT_SESSION_USER_ID, Twitter.getSessionManager().getActiveSession().getUserId());
            contentValues.put(COLUMN_USER_ID, user.userId);
            contentValues.put(COLUMN_PROFILE_NAME, user.profileName);
            contentValues.put(COLUMN_USER_NAME, user.userName);
            contentValues.put(COLUMN_PROFILE_PICTURE_URL, user.profilePictureUrl);
            contentValues.put(COLUMN_PACK_KEY, packKey);

            return contentValues;
        }

        public static Friend convertToFriend(Cursor cursor) {
            int userNameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
            int profileNameIndex = cursor.getColumnIndex(COLUMN_PROFILE_NAME);
            int profilePictureUrlIndex = cursor.getColumnIndex(COLUMN_PROFILE_PICTURE_URL);
            int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID);

            String userName = cursor.getString(userNameIndex);
            String profileName = cursor.getString(profileNameIndex);
            String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
            long userId = cursor.getLong(userIdIndex);

            return new Friend(userName, profileName, profilePictureUrl, userId);
        }
    }

    public static final class PackEntry implements BaseColumns {

        //// TABLE NAME //////////
        public static final String TABLE_NAME = "pack";

        //////////// Columns /////////////
        public static final String COLUMN_CURRENT_SESSION_USER_ID = "current_session_user_id";

        public static final String COLUMN_PACK_NAME = "pack_name";

        public static final String COLUMN_PACK_PARENT_ID = "parent_pack_key";

        public static final String COLUMN_DESCRIPTION = "pack_description";

        /////// URI ////////////////
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PACK).build();

        //////////// CONTENT TYPES ///////////////////

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY;

        //////////// Functions to build URIs/////////////////////

        /**
         * Used to return URI with the row ID of the inserted data
         */
        public static Uri buildPackUriWithInsertedRowId(long insertedRowId) {
            return ContentUris.withAppendedId(CONTENT_URI, insertedRowId);
        }

        public static Uri buildPackUriWithCurrentUserId(Long currentUserId) {
            return CONTENT_URI.buildUpon().appendPath(currentUserId + "").build();
        }

        public static Uri buildPackUriWithCurrentUserIdAndParentPackId(Long currentUserId, Long parentPackId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(currentUserId + "")
                    .appendPath(parentPackId + "")
                    .build();
        }

        public static Uri buildPackUriWithPackId(Long currentUserId, Long currentPackId) {
            return CONTENT_URI.buildUpon().appendPath(currentUserId + "")
                    .appendPath(PATH_PACK)
                    .appendPath(currentPackId + "").build();
        }

        public static Uri buildPackUriWithParentPackName(Long currentUserId, String parentPackName) {
            return CONTENT_URI.buildUpon().appendPath(currentUserId + "")
                    .appendPath(parentPackName).build();
        }

        //////////////// Get info from built uri //////////////////

        public static long getCurrentUserIdFromPackUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getCurrentUserIdFromPackIdUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getPackIdFromPackIdUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(3));
        }

        public static long getCurrentUserIdFromParentPackNameUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getParentPackNameFromParentPackNameUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static long getCurrentUserIdFromParentPackIdUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getParentPackIdFromParentPackIdUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static ContentValues getContentValues(String packName, String packDescription, Long parentPackId) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_CURRENT_SESSION_USER_ID, Twitter.getSessionManager().getActiveSession().getUserId());
            contentValues.put(COLUMN_PACK_NAME, packName);
            contentValues.put(COLUMN_PACK_PARENT_ID, parentPackId != -1 ? parentPackId : null);
            contentValues.put(COLUMN_DESCRIPTION, packDescription);

            return contentValues;
        }

        public static Pack convertToPack(Cursor cursor) {
            final int packNameIndex = cursor.getColumnIndex(COLUMN_PACK_NAME);
            final int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            final int currentPackIdIndex = cursor.getColumnIndex(_ID);
            final int parentPackIdIndex = cursor.getColumnIndex(COLUMN_PACK_PARENT_ID);

            final String packName = cursor.getString(packNameIndex);
            final String description = cursor.getString(descriptionIndex);
            final long currentPackId = cursor.getLong(currentPackIdIndex);
            long parentPackId;
            if (cursor.isNull(parentPackIdIndex)) {
                parentPackId = -1;
            } else {
                parentPackId = cursor.getLong(parentPackIdIndex);
            }

            return new Pack(packName, description, parentPackId, currentPackId);
        }

    }
}
