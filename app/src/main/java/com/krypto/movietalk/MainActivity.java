package com.krypto.movietalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Displays popular movies as per User setting.
 * For "Most Popular" and "Highest Rated" Setting,MainActivityFragment is intantiated.
 * For "Favorites" setting, FavFragment is instantiated.
 * Two Fragments are used cause FavFragment uses loaders and cursors to retrieve Favorite movies Data
 * as Favorite movie details are stored in Database.
 * User settings details are stored in SharedPreferences.
 */
public class MainActivity extends BaseActivity implements DetailActivityFragment.ActivityName, MainActivityFragment.Start, FavFragment.Open {

    @InjectView(R.id.toolbarTitle)
    TextView mToolbarTitle;
    @InjectView(R.id.app_bar)
    Toolbar mToolbar;

    private String mSortValue;
    private final static String SORT = "Sort";
    private final static String MOVIE = "Movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        activateToolbar();
        mToolbarTitle.setTypeface(Utility.get(this, getString(R.string.roboto_medium)));
        mToolbarTitle.setText(getString(R.string.popular));
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSortValue = preferences.getString(getString(R.string.sort_pref), "null");

        if (Utility.checkConnection(this)) {

            if (mSortValue.equals("null") || mSortValue.equals("1") || mSortValue.equals("2")) {

                Intent serviceIntent = new Intent(this, MainActivityFragment.PopularMovieService.class);
                if (mSortValue.equals("null") || mSortValue.equals("1"))
                    serviceIntent.putExtra(SORT, getString(R.string.pop_desc));
                else if (mSortValue.equals("2"))
                    serviceIntent.putExtra(SORT, getString(R.string.vote_desc));
                startService(serviceIntent);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MainActivityFragment()).commit();

            }
        } else {
            Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
        if (mSortValue.equals("3")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new FavFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.rating) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setBackgroundColor(getResources().getColor(R.color.md_indigo_500));
    }

    @Override
    public void setName(String name) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)
            mToolbarTitle.setText(name);
    }

    @Override
    public void trigger(Movie movie) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MOVIE, movie);
            detailActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragment, detailActivityFragment).commit();
        }
    }

    @Override
    public void start(Movie movie) {

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MOVIE, movie);
            detailActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragment, detailActivityFragment).commit();
        }
    }
}
