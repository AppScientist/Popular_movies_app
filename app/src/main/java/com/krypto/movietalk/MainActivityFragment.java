package com.krypto.movietalk;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.krypto.movietalk.auto_generated_pojos_for_retrofit.popular_pojo.Pojo;
import com.krypto.movietalk.auto_generated_pojos_for_retrofit.popular_pojo.Result;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Downloads and displays movie info as per "Most Popular" or "Highest Rated" user setting
 */
public class MainActivityFragment extends Fragment {

    private static CustomAdapter sAdapter;
    private Start mStart;
    private final static String MOVIE = "Movie";
    private final static String SORT = "Sort";

    @InjectView(R.id.movieGrid)
    GridView mMovieGrid;


    public MainActivityFragment() {
    }

    public interface Start {

        void trigger(Movie movie);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mStart = (Start) activity;
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
        sAdapter = new CustomAdapter(getActivity(), R.layout.fragment_main_content);
        mMovieGrid.setAdapter(sAdapter);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mMovieGrid.getLayoutParams();
            params.topMargin = 0;
        }


        return rootView;
    }

    @OnItemClick(R.id.movieGrid)
    public void onItemClick(int position) {

        Movie movieObject = sAdapter.getItem(position);

        if (Utility.checkConnection(getActivity())) {
            Intent serviceIntent = new Intent(getActivity(), VideoService.class);
            serviceIntent.putExtra("Id", movieObject.getId());
            getActivity().startService(serviceIntent);

            Intent serviceIntent2 = new Intent(getActivity(), ReviewFragment.ReviewService.class);
            serviceIntent2.putExtra("Id", movieObject.getId());
            getActivity().startService(serviceIntent2);
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (!isTablet) {
            Intent activityIntent = new Intent(getActivity(), DetailActivity.class);
            activityIntent.putExtra(MOVIE, movieObject);
            startActivity(activityIntent);
        } else {
            mStart.trigger(movieObject);
        }
    }

    public interface GetMovieDetails {

        @GET("/3/discover/movie")
        Pojo getResult(@Query("sort_by") String sort, @Query("api_key") String api);
    }


    /**
     * Downloads all movie related info and stores in the adapter
     */
    public static class PopularMovieService extends IntentService {

        private GetMovieDetails getMovieDetails;

        public PopularMovieService() {
            super("Popular Movies");
        }


        @Override
        protected void onHandleIntent(Intent intent) {

            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!sAdapter.isEmpty())
                        sAdapter.clear();
                }
            });

            String sort;
            RestAdapter restAdapter;
            final String BASE_URL = "http://api.themoviedb.org";
            final String API_VALUE = BuildConfig.MOVIE_KEY;
            sort = intent.getStringExtra(SORT);

            try {
                restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
                getMovieDetails = restAdapter.create(GetMovieDetails.class);
                Pojo pojo = getMovieDetails.getResult(sort, API_VALUE);
                List<Result> resultList = pojo.getResults();

                int size = resultList.size();
                for (int i = 0; i < size; i++) {

                    Result result = resultList.get(i);
                    String title = result.getOriginalTitle();
                    String overview = result.getOverview();
                    String imageUrl = result.getPosterPath();
                    String finalUrl = "http://image.tmdb.org/t/p/w300" + imageUrl;
                    String release = result.getReleaseDate();
                    String vote = Float.toString(result.getVoteAverage());
                    String id = Integer.toString(result.getId());
                    final Movie movieObject = new Movie(title, overview, vote, finalUrl, release, id);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sAdapter.add(movieObject);
                        }
                    });
                }
            } catch (RetrofitError error) {

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
