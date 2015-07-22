package com.krypto.movietalk;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.krypto.movietalk.auto_generated_pojos_for_retrofit.review_pojo.ReviewPojo;
import com.krypto.movietalk.auto_generated_pojos_for_retrofit.review_pojo.Reviewresult;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Displays review info about particular movie
 */
public class ReviewFragment extends Fragment {

    private static CustomArrayAdapter sCustomArrayAdapter;
    private static final String ID="Id";

    @InjectView(R.id.listview)
    ListView mListView;
    @InjectView(R.id.emptytext)
    TextView textView;

    public ReviewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        sCustomArrayAdapter = new CustomArrayAdapter(getActivity(), R.layout.fragment_review_content, new ArrayList<Review>());
        ButterKnife.inject(this, rootView);
        mListView.setAdapter(sCustomArrayAdapter);
        mListView.setEmptyView(textView);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    public interface GetReview {

        @GET("/3/movie/{id}/reviews")
        ReviewPojo getResult(@Path("id") String id, @Query("api_key") String api);
    }

    /**
     * Downloads review info
     */
    public static class ReviewService extends IntentService {

        public ReviewService() {
            super("Review");
        }

        @Override
        protected void onHandleIntent(Intent intent) {

            final String BASE_URL = "http://api.themoviedb.org";
            final String API_VALUE = BuildConfig.MOVIE_KEY;

            String id = intent.getStringExtra(ID);

            GetReview getReview;
            RestAdapter restAdapter;
            try {
                restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
                getReview = restAdapter.create(GetReview.class);
                ReviewPojo reviewPojo = getReview.getResult(id, API_VALUE);

                List<Reviewresult> reviewresults = reviewPojo.getResults();

                int size = reviewresults.size();

                for (int i = 0; i < size; i++) {

                    Reviewresult reviewresult = reviewresults.get(i);

                    String critic = reviewresult.getAuthor();
                    String quote = reviewresult.getContent();

                    final Review review = new Review(critic, quote);

                    Handler handler = new Handler(getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sCustomArrayAdapter.add(review);
                        }
                    });
                }
            }catch (RetrofitError error){

            }
        }
    }
}
