package com.krypto.movietalk.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "fav.db";

    public FavHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_FAV_TABLE = "CREATE TABLE " + FavContract.FavMovies.TABLE_NAME + " (" +
                FavContract.FavMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + FavContract.FavMovies.MOVIE_ID +
                " TEXT NOT NULL, " + FavContract.FavMovies.TITLE + " TEXT, " + FavContract.FavMovies.RELEASE +
                " TEXT, " + FavContract.FavMovies.DESCRIPTION + " TEXT, " + FavContract.FavMovies.VOTE +
                " TEXT, " + FavContract.FavMovies.URL + " TEXT, " + FavContract.FavMovies.REVIEW + " TEXT, " +
                "UNIQUE (" + FavContract.FavMovies.MOVIE_ID + ") ON CONFLICT REPLACE );";

        db.execSQL(CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
    }
}
