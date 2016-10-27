package com.example.nikhiljoshi.enlighten;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import static com.example.nikhiljoshi.enlighten.data.Contract.BookContract.BookEntry.*;

/**
 * Created by nikhiljoshi on 4/27/16.
 */
public class TestDataOperator extends AndroidTestCase {

    public static final String BOOK_NAME = "tin tin";

    public static ContentValues getSampleBook() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, BOOK_NAME);
        contentValues.put(COLUMN_AUTHOR, "herge");
        contentValues.put(COLUMN_BOOK_DESCRIPTION, "an adventure");
        contentValues.put(COLUMN_COVER_URL, "http://crap.com");
        contentValues.put(COLUMN_PUBLISHED_DATE, "2012-12-12");
        contentValues.put(COLUMN_PUBLISHER, "have fun comics");
        contentValues.put(COLUMN_WEEKS_ON_LIST, "100");
        return contentValues;
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
