package com.krypto.movietalk.storage;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;


public class FavProvider extends ContentProvider {

    private FavHelper favHelper;

    @Override
    public boolean onCreate() {
        favHelper = new FavHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = favHelper.getReadableDatabase().query(FavContract.FavMovies.TABLE_NAME,
                projection,
                selection, selectionArgs,
                null, null,
                sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return FavContract.FavMovies.CONTENT_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = favHelper.getWritableDatabase().insert(FavContract.FavMovies.TABLE_NAME, null, values);
        if (id > 0) {
            FavContract.FavMovies.buildFavUris(id);
        } else {
            throw new SQLiteException("Unable to insert" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted = favHelper.getWritableDatabase().delete(FavContract.FavMovies.TABLE_NAME, selection, selectionArgs);
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated = favHelper.getWritableDatabase().update(FavContract.FavMovies.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
