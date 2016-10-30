package com.example.nikhiljoshi.enlighten.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.nikhiljoshi.enlighten.data.EnlightenContract.*;

/**
 * Created by nikhiljoshi on 6/3/16.
 */
public class EnlightenDbHelper extends SQLiteOpenHelper {

    //TODO: Upgrade the database version to the next level. Fill out onUpgrade
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "enlighten.db";

    public EnlightenDbHelper(Context context) {
        this(context, DATABASE_VERSION);
    }

    EnlightenDbHelper(Context context, int databaseVersion) {
        super(context, DATABASE_NAME, null, databaseVersion);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FRIEND_TABLE = "CREATE TABLE " + FriendEntry.TABLE_NAME + "(" +
                FriendEntry._ID + " INTEGER PRIMARY KEY, " +
                FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " INTEGER NOT NULL, " +
                FriendEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                FriendEntry.COLUMN_USER_NAME + " STRING NOT NULL, " +
                FriendEntry.COLUMN_PROFILE_NAME + " STRING NOT NULL, " +
                FriendEntry.COLUMN_PROFILE_PICTURE_URL + " STRING NOT NULL, " +
                FriendEntry.COLUMN_PACK_KEY + " INTEGER, " +
                " FOREIGN KEY (" + FriendEntry.COLUMN_PACK_KEY + ") REFERENCES " +
                PackEntry.TABLE_NAME + "(" + PackEntry._ID + "), " +
                " UNIQUE (" + FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + ", " +
                FriendEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE" +
                ");";

        final String SQL_CREATE_PACK_TABLE = "CREATE TABLE " + PackEntry.TABLE_NAME + "(" +
                PackEntry._ID + " INTEGER PRIMARY KEY, " +
                PackEntry.COLUMN_CURRENT_SESSION_USER_ID + " INTEGER NOT NULL, " +
                PackEntry.COLUMN_PACK_NAME + " STRING NOT NULL, " +
                PackEntry.COLUMN_DESCRIPTION + " STRING NOT NULL, " +
                PackEntry.COLUMN_PACK_PARENT_ID + " INTEGER, " +
                " FOREIGN KEY (" + PackEntry.COLUMN_PACK_PARENT_ID + ") REFERENCES " +
                PackEntry.TABLE_NAME + "(" + PackEntry._ID + "), " +
                " UNIQUE (" + PackEntry.COLUMN_CURRENT_SESSION_USER_ID + ", " +
                              PackEntry.COLUMN_PACK_NAME + ", " +
                              PackEntry.COLUMN_PACK_PARENT_ID + ") ON CONFLICT REPLACE" +
                ");";


        db.execSQL(SQL_CREATE_FRIEND_TABLE);
        db.execSQL(SQL_CREATE_PACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * The logic behind onUpgrade is as follows:
         * onCreate is only run the first time. When you start this dbHelper for the first time,
         * onCreate is run, and after that, it always calls onUpgrade. So when you need to make changes
         * to the db, you need to update the database version and add the changes in onUpgrade.
         *
         * For ex: Lets say that the first version contains friend table without pack column or the pack table.
         * In this case, for the second version, we would have to modify the friend table to add pack column
         * and add pack table in the onUpgrade.
         *
         * The way we would need to do this is as follows:
         * 1) change the old friends table name to a dummy name
         * 2) create the new friends table and the pack table
         * 3) copy data from old friends table to the new friends table
         * 4) drop the old table
         */
//        if (oldVersion < 2) {
//            String tempFriendTableName = FriendEntry.TABLE_NAME + "_TEMP";
//            String alterFamilyTableSql = "ALTER TABLE " + FriendEntry.TABLE_NAME + " RENAME TO " + tempFriendTableName;
//            String copyDataFromOldFriendTableToNew =
//                    "INSERT INTO " + FriendEntry.TABLE_NAME + " (" +
//                            FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + ", " +
//                            FriendEntry.COLUMN_USER_ID + ", " +
//                            FriendEntry.COLUMN_USER_NAME + ", " +
//                            FriendEntry.COLUMN_PROFILE_PICTURE_URL + ", " +
//                            FriendEntry.COLUMN_PROFILE_NAME + ") " +
//                            " SELECT " +
//                                FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + " " + FriendEntry.COLUMN_CURRENT_SESSION_USER_ID + ", " +
//                                FriendEntry.COLUMN_USER_ID + " " + FriendEntry.COLUMN_USER_ID + ", " +
//                                FriendEntry.COLUMN_USER_NAME + " " + FriendEntry.COLUMN_USER_NAME +  ", " +
//                                FriendEntry.COLUMN_PROFILE_PICTURE_URL + " " + FriendEntry.COLUMN_PROFILE_PICTURE_URL + ", " +
//                                FriendEntry.COLUMN_PROFILE_NAME + " " + FriendEntry.COLUMN_PROFILE_NAME + " FROM " + tempFriendTableName;
//            String drop_table = "DROP TABLE " + tempFriendTableName;
//
//            db.execSQL(alterFamilyTableSql);
//            db.execSQL(SQL_CREATE_PACK_TABLE_2);
//            db.execSQL(SQL_CREATE_FRIEND_TABLE_2);
//            db.execSQL(copyDataFromOldFriendTableToNew);
//            db.execSQL(drop_table);
//        }
    }
}
