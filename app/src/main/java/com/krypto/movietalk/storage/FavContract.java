package com.krypto.movietalk.storage;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class FavContract {

    public static final String CONTENT_AUTHORITY = "com.krypto.movietalk";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAV = "favorites";

    public static final class FavMovies implements BaseColumns {

        public static final String TABLE_NAME = "Favorites_list";

        public static final String MOVIE_ID = "movie_id";

        public static final String TITLE = "title";

        public static final String DESCRIPTION = "description";

        public static final String VOTE = "vote_average";

        public static final String RELEASE = "release_date";

        public static final String REVIEW = "movie_review";

        public static final String URL = "url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        public static Uri buildFavUris(long id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
