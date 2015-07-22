package com.krypto.movietalk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krypto.movietalk.storage.FavContract;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;


/**
 * Displays Favorite movies as selected by user
 */
public class FavFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CustomCursorAdapter mCustomAdapter;
    private Cursor mCursor;
    private Open open;
    private boolean isTablet;
    private static final int FAV_LOADER = 0;
    private final static String MOVIE = "Movie";
    private static final String ID = "Id";

    @InjectView(R.id.movieGrid)
    GridView mGridView;

    public FavFragment() {

    }

    public interface Open {

        void start(Movie movie);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        open=(Open)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        mCustomAdapter = new CustomCursorAdapter(getActivity(),
                R.layout.fragment_main_content,
                null,
                new String[]{FavContract.FavMovies.URL, FavContract.FavMovies.TITLE},
                new int[]{R.id.moviePoster, R.id.movieName}, 0);
        mGridView.setAdapter(mCustomAdapter);
        isTablet=getResources().getBoolean(R.bool.isTablet);
        if(isTablet) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mGridView.getLayoutParams();
            params.topMargin=0;
        }

        return rootView;
    }


    @OnItemClick(R.id.movieGrid)
    public void click(View view) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String name = viewHolder.textView.getText().toString();

        mCursor = getActivity().getContentResolver().query(FavContract.FavMovies.CONTENT_URI, null, FavContract.FavMovies.TABLE_NAME + "." + FavContract.FavMovies.TITLE + " =?", new String[]{name}, null);
        if (mCursor != null && mCursor.moveToFirst()) {

            int idCol = mCursor.getColumnIndex(FavContract.FavMovies.MOVIE_ID);
            String id = mCursor.getString(idCol);

            if (Utility.checkConnection(getActivity())) {
                Intent serviceIntent = new Intent(getActivity(), VideoService.class);
                serviceIntent.putExtra(ID, id);
                getActivity().startService(serviceIntent);

                Intent serviceIntent2 = new Intent(getActivity(), ReviewFragment.ReviewService.class);
                serviceIntent2.putExtra(ID, id);
                getActivity().startService(serviceIntent2);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }

            int titleCol = mCursor.getColumnIndex(FavContract.FavMovies.TITLE);
            String title = mCursor.getString(titleCol);

            int urlLoc = mCursor.getColumnIndex(FavContract.FavMovies.URL);
            String thumbnail = mCursor.getString(urlLoc);

            int releaseCol = mCursor.getColumnIndex(FavContract.FavMovies.RELEASE);
            String release = mCursor.getString(releaseCol);

            int voteCol = mCursor.getColumnIndex(FavContract.FavMovies.VOTE);
            String vote = mCursor.getString(voteCol);

            int overviewCol = mCursor.getColumnIndex(FavContract.FavMovies.DESCRIPTION);
            String overview = mCursor.getString(overviewCol);


            if(!isTablet) {
                Intent activityIntent = new Intent(getActivity(), DetailActivity.class);
                activityIntent.putExtra(MOVIE, new Movie(title, overview, vote, thumbnail, release, id));
                startActivity(activityIntent);
            }else{
                open.start(new Movie(title, overview, vote, thumbnail, release, id));
            }
        }

        mCursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FavContract.FavMovies.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCustomAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCustomAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FAV_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(FAV_LOADER, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public class CustomCursorAdapter extends SimpleCursorAdapter {

        int layout;
        ViewHolder viewHolder;

        public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.layout = layout;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View v = LayoutInflater.from(context).inflate(R.layout.fragment_main_content, parent, false);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            viewHolder = (ViewHolder) view.getTag();

            int urlCol = cursor.getColumnIndex(FavContract.FavMovies.URL);
            String url = cursor.getString(urlCol);

            if (url != null)
                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.imageView);

            int titleCol = cursor.getColumnIndex(FavContract.FavMovies.TITLE);
            String title = cursor.getString(titleCol);

            viewHolder.textView.setTypeface(Utility.get(context, getString(R.string.roboto_regular)));
            viewHolder.textView.setText(title);

        }
    }

    static class ViewHolder {
        @InjectView(R.id.moviePoster)
        ImageView imageView;
        @InjectView(R.id.movieName)
        TextView textView;

        public ViewHolder(View v) {
            ButterKnife.inject(this, v);
        }
    }
}
