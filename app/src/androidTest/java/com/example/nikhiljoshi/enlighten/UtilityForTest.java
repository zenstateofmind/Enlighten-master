package com.example.nikhiljoshi.enlighten;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nikhiljoshi.enlighten.data.EnlightenDbHelper;

import java.util.Map;
import java.util.Set;

import static com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract.*;

/**
 * Created by nikhiljoshi on 6/3/16.
 */
public class UtilityForTest {

    public static ContentValues createSampleFriendData(long packRowId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID, 123456);
        contentValues.put(FriendEntry.COLUMN_USER_ID, 23456);
        contentValues.put(FriendEntry.COLUMN_USER_NAME, "@hello");
        contentValues.put(FriendEntry.COLUMN_PROFILE_NAME, "Hello Name");
        contentValues.put(FriendEntry.COLUMN_PROFILE_PICTURE_URL, "https://hello.com");
        contentValues.put(FriendEntry.COLUMN_PACK_KEY, packRowId);
        return contentValues;
    }

    public static ContentValues createSamplePackData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PackEntry.COLUMN_CURRENT_SESSION_USER_ID, 123456);
        contentValues.put(PackEntry.COLUMN_DESCRIPTION, "This is the description");
        contentValues.put(PackEntry.COLUMN_PACK_NAME, "India");
        contentValues.put(PackEntry.COLUMN_PACK_PARENT_ID, 2);
        return contentValues;
    }

    public static boolean validateResults(Cursor cursor, ContentValues contentValues) {
        Set<Map.Entry<String, Object>> entries = contentValues.valueSet();

        for (Map.Entry<String, Object> entry: entries) {
            String columnName = entry.getKey();
            int columnIndex = cursor.getColumnIndex(columnName);
            String columnValueInDb = cursor.getString(columnIndex);
            if (!(entry.getValue()+"").equals(columnValueInDb)) {
                return false;
            }
        }

        return true;

    }

    public static long insertSampleData(ContentValues contentValues, Context context, String tableName) {
        final SQLiteDatabase db = new EnlightenDbHelper(context).getWritableDatabase();
        long insertId = db.insert(tableName, null, contentValues);
        return insertId;
    }

}
