package com.example.nikhiljoshi.enlighten.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.nikhiljoshi.enlighten.UtilityForTest;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;
import com.example.nikhiljoshi.enlighten.data.Provider.EnlightenProvider;

import static com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract.*;

/**
 * Created by nikhiljoshi on 6/5/16.
 */
public class TestEnlightenProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteRecordsFromDb();
    }

    private void deleteRecordsFromDb() {
        EnlightenDbHelper enlightenDbHelper = new EnlightenDbHelper(mContext);
        SQLiteDatabase db = enlightenDbHelper.getWritableDatabase();
        db.delete(FriendEntry.TABLE_NAME, null, null);
        db.delete(PackEntry.TABLE_NAME, null, null);
        db.close();
    }

    /**
     * Test to ensure that the EnlightenProvider has been registered -
     * mainly through the android manifest file
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), EnlightenProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("The provider has been registered with the authority has " +
                    "was mentioned in the class", providerInfo.authority, EnlightenContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException e) {
            assertTrue(componentName.getClassName() + " has not been registered", false);
        }

    }

    public void testGetType() {

        long currentSessionUserId = 12345L;
        String userName = "userName";
        long friendUserId = 145L;
        long packId = 1234L;

        String type = mContext.getContentResolver().getType(FriendEntry.buildFriendUriWithCurrentUserId(currentSessionUserId));
        assertEquals("This should return multiple type", FriendEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(FriendEntry
                .buildUriWithCurrentUserIdAndFriendUserId(currentSessionUserId, friendUserId));
        assertEquals("This should return individual type", FriendEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(
                FriendEntry.buildUriWithCurrentUserIdAndPackId(currentSessionUserId, packId));
        assertEquals("This should return multiple type", FriendEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
                PackEntry.buildPackUriWithCurrentUserId(currentSessionUserId));
        assertEquals("This should return multiple type", PackEntry.CONTENT_TYPE, type);


    }


    public void testQuery() {

        ContentValues packData = UtilityForTest.createSamplePackData();
        final long packRowId = UtilityForTest.insertSampleData(packData, mContext, PackEntry.TABLE_NAME);
        ContentValues contentValues = UtilityForTest.createSampleFriendData(packRowId);
        final long insertedFriendId = UtilityForTest.insertSampleData(contentValues, mContext, FriendEntry.TABLE_NAME);

        // query friends table with current user id
        long currentSessionUserId = Long.parseLong(contentValues.getAsString(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID));
        Uri uri = FriendEntry.buildFriendUriWithCurrentUserId(currentSessionUserId);
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        assertTrue("Could not get information back", cursor.moveToFirst());

        assertTrue("Results are not accurate", UtilityForTest.validateResults(cursor, contentValues));

        //query friends table with current user id and userName
        Long friendUserId = contentValues.getAsLong(FriendEntry.COLUMN_USER_ID);
        uri = FriendEntry.buildUriWithCurrentUserIdAndFriendUserId(currentSessionUserId, friendUserId);

        cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        assertTrue("Could not get information back", cursor.moveToFirst());
        assertTrue("Results are not accurate", UtilityForTest.validateResults(cursor, contentValues));

        //query friends table with current user id and pack id
        uri = FriendEntry.buildUriWithCurrentUserIdAndPackId(currentSessionUserId, packRowId);
        cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assertTrue("Could not get information back", cursor.moveToFirst());
        assertTrue("Results are not accurate", UtilityForTest.validateResults(cursor, contentValues));

        // query pack table with current user id
        uri = PackEntry.buildPackUriWithCurrentUserId(currentSessionUserId);
        cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assertTrue("Could not get information back", cursor.moveToFirst());
        assertTrue("Results are not accurate", UtilityForTest.validateResults(cursor, packData));

    }

    public void testInsert() {

        // insert into pack table
        Uri basicPackUri = PackEntry.CONTENT_URI;
        ContentValues packData = UtilityForTest.createSamplePackData();
        final Uri insertPackUri = mContext.getContentResolver().insert(basicPackUri, packData);
        long insertPackId = ContentUris.parseId(insertPackUri);
        assertTrue("Data wasnt inserted properly", insertPackId != -1);

        // insert into friend table
        ContentValues contentValues = UtilityForTest.createSampleFriendData(insertPackId);
        Uri basicFriendUri = FriendEntry.CONTENT_URI;
        Uri insertedFriendUri = mContext.getContentResolver().insert(basicFriendUri, contentValues);
        long insertedFriendId = ContentUris.parseId(insertedFriendUri);
        assertTrue("Data wasnt inserted properly", insertedFriendId != -1);
    }

    public void testUpdate() {

        ContentValues packData = UtilityForTest.createSamplePackData();
        final long packRowId = UtilityForTest.insertSampleData(packData, mContext, PackEntry.TABLE_NAME);

        ContentValues contentValues = UtilityForTest.createSampleFriendData(packRowId);
        String upatedProfileName = "pmarca";
        String updatedProfilePictureUrl = "https://updated.com";

        long inserted_id = UtilityForTest.insertSampleData(contentValues, mContext, FriendEntry.TABLE_NAME);

        contentValues.put(FriendEntry.COLUMN_PROFILE_NAME, upatedProfileName);

        Uri uriWithCurrentUserId =
                FriendEntry.buildFriendUriWithCurrentUserId(contentValues.getAsLong(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID));

        int numRowsUpdated = mContext.getContentResolver().update(uriWithCurrentUserId, contentValues, null, null);

        assertTrue("Row wasn't updated", numRowsUpdated == 1);

        contentValues.put(FriendEntry.COLUMN_PROFILE_PICTURE_URL, updatedProfilePictureUrl);

        Uri uriWithCurrentUserIdAndFriendUserName = FriendEntry.buildUriWithCurrentUserIdAndFriendUserId(
                contentValues.getAsLong(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID),
                contentValues.getAsLong(FriendEntry.COLUMN_USER_ID));

        numRowsUpdated = mContext.getContentResolver().update(uriWithCurrentUserIdAndFriendUserName, contentValues, null, null);

        assertTrue("Row wasn't updated... number of rows that were updated is: " + numRowsUpdated, numRowsUpdated == 1);

    }

    public void testDelete() {

        ContentValues packData = UtilityForTest.createSamplePackData();
        final long packRowId = UtilityForTest.insertSampleData(packData, mContext, PackEntry.TABLE_NAME);
        ContentValues friendData = UtilityForTest.createSampleFriendData(packRowId);
        UtilityForTest.insertSampleData(friendData, mContext, FriendEntry.TABLE_NAME);

        // delete from friend table with user id
        Long userSessionId = friendData.getAsLong(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID);
        Uri uri = FriendEntry.buildFriendUriWithCurrentUserId(userSessionId);
        int numRowsDeleted = mContext.getContentResolver().delete(uri, null, null);
        assertTrue("Rows haven't been deleted... deleted rows: " + numRowsDeleted, numRowsDeleted == 1);

        // delete from friend table with current session user id and a friend's user name
        UtilityForTest.insertSampleData(friendData, mContext, FriendEntry.TABLE_NAME);
        long friendUserId = friendData.getAsLong(FriendEntry.COLUMN_USER_ID);
        uri = FriendEntry.buildUriWithCurrentUserIdAndFriendUserId(userSessionId, friendUserId);
        numRowsDeleted = mContext.getContentResolver().delete(uri, null, null);
        assertTrue("Rows haven't been deleted... deleted rows: " + numRowsDeleted, numRowsDeleted == 1);

        // delete from pack table with current session user id
        final Long packSessionId = packData.getAsLong(PackEntry.COLUMN_CURRENT_SESSION_USER_ID);
        uri = PackEntry.buildPackUriWithCurrentUserId(packSessionId);
        numRowsDeleted = mContext.getContentResolver().delete(uri, null, null);
        assertTrue("Rows haven't been deleted... deleted rows: " + numRowsDeleted, numRowsDeleted == 1);


    }



}
