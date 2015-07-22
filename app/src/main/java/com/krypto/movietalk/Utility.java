package com.krypto.movietalk;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.krypto.movietalk.storage.FavContract;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Contains functions used by all acitivites and fragments for performing useful functions
 */
public class Utility {

    private static final Hashtable<String, Typeface> CACHE = new Hashtable<>();
    private static final String FAVORITE = "favorite";

    /**
     * Stores two custom font files in Hashtable,so that only one instance of each is present for the whole application.
     *
     * @param c         Context of the activity
     * @param assetPath Path of the custom font file
     * @return Typeface font from the requested asset path
     */

    public static Typeface get(Context c, String assetPath) {
        synchronized (CACHE) {
            if (!CACHE.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    CACHE.put(assetPath, t);
                } catch (Exception e) {
                    return null;
                }
            }
            return CACHE.get(assetPath);
        }
    }


    /**
     * Applies color to selected text using Spannable
     *
     * @param textView        TextView to set the final text
     * @param fullText        String containing the full text
     * @param textToBeColored Part of the text which needs to be colored
     */
    public static void getColoredText(Context c, TextView textView, String fullText, String textToBeColored) {

        Spannable WordtoSpan = new SpannableString(fullText);
        WordtoSpan.setSpan(new ForegroundColorSpan(c.getResources().getColor(R.color.md_amber_A200)), 0, textToBeColored.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(WordtoSpan);

    }


    /**
     * Stores movie Id in a "Set" which needs to saved in Shared Preferences
     *
     * @param c            Context
     * @param favoriteItem MovieId whihc needs to be stored
     * @return True or false depending upon the success or failure of operation
     */
    public static boolean addFavoriteItem(Context c, String favoriteItem) {
        //Get previous favorite items
        Set<String> favoriteList = getStringFromPreferences(c, new HashSet<String>(), FAVORITE);
        favoriteList.add(favoriteItem);
        // Save in Shared Preferences
        return putStringInPreferences(c, favoriteList, FAVORITE);
    }

    /**
     * Retrieves Favorite Movie List
     *
     * @param c Context
     * @return List of favorite movies which are stored in a "Set"
     */
    public static Set<String> getFavoriteList(Context c) {
        Set<String> favoriteList = getStringFromPreferences(c, new HashSet<String>(), FAVORITE);
        return favoriteList;
    }

    /**
     * Saves the "Set" containing favorite movie Id's in Shared Preferences
     *
     * @param c       Context
     * @param movieId "Set" to be saved
     * @param key     Key used by SharedPreferences to save the "Set"
     * @return True or false depending upon the success or failure of operation
     */
    private static boolean putStringInPreferences(Context c, Set<String> movieId, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, movieId);
        editor.apply();
        return true;
    }

    /**
     * Retrieves the "Set" containing Favorite movie id's from SharedPreferences
     *
     * @param c            Context
     * @param defaultValue Default empty "Set" in case no favorite movie id's are available
     * @param key          Key used by SharedPreferences to retrieve this "Set"
     * @return True or false depending upon the success or failure of operation
     */
    private static Set<String> getStringFromPreferences(Context c, Set<String> defaultValue, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        Set<String> temp = sharedPreferences.getStringSet(key, defaultValue);
        return temp;
    }

    /**
     * Deletes a particular movie id from Shared Preferences
     *
     * @param c        Context
     * @param removeId Id to be deleted
     */
    public static void removeFromFavorites(Context c, String removeId) {
        Set<String> favoriteList = getStringFromPreferences(c, new HashSet<String>(), FAVORITE);

        if (favoriteList.contains(removeId)) {
            favoriteList.remove(removeId);
            putStringInPreferences(c, favoriteList, FAVORITE);
        }
    }

    /**
     * Saves all favorite movie related info in Databases
     *
     * @param c         Context
     * @param id        Movie ID
     * @param title     Name of the movie
     * @param thumbnail Thumbnail Url of the movie
     * @param overview  Overview of the movie
     * @param release   Release date of the movie
     * @param vote      User rating of the movie
     */
    public static void saveInDb(Context c, String id, String title, String thumbnail, String overview, String release, String vote) {


        ContentValues values = new ContentValues();
        values.put(FavContract.FavMovies.MOVIE_ID, id);
        values.put(FavContract.FavMovies.TITLE, title);
        values.put(FavContract.FavMovies.DESCRIPTION, overview);
        values.put(FavContract.FavMovies.RELEASE, release);
        values.put(FavContract.FavMovies.VOTE, vote);
        values.put(FavContract.FavMovies.URL, thumbnail);
        values.put(FavContract.FavMovies.REVIEW, id);

        c.getContentResolver().insert(FavContract.FavMovies.CONTENT_URI, values);
    }

    /**
     * Deletes a particular movie from the database
     *
     * @param c  Context
     * @param id Movie id
     */
    public static void removeFromDb(Context c, String id) {

        c.getContentResolver().delete(FavContract.FavMovies.CONTENT_URI, FavContract.FavMovies.TABLE_NAME + "." + FavContract.FavMovies.MOVIE_ID + " =?", new String[]{id});

    }

    /**
     * Checks availablity of network connectivity
     *
     * @param c Context
     * @return True or false depending upon the connection availability
     */
    public static boolean checkConnection(Context c) {

        ConnectivityManager connection = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connection.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
