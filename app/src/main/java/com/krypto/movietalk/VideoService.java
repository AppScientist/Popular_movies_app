package com.krypto.movietalk;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.krypto.movietalk.auto_generated_pojos_for_retrofit.video_pojo.VideoPojo;
import com.krypto.movietalk.auto_generated_pojos_for_retrofit.video_pojo.VideoResult;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Retrieves the video key used for playing trailers
 */
public class VideoService extends IntentService {

    private static final String KEY="Key";
    private static final String ID="Id";

    public VideoService() {
        super("Video Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DetailActivityFragment.GetVideoId getVideoId;
        RestAdapter restAdapter;
        final String BASE_URL = "http://api.themoviedb.org";
        final String API_VALUE = BuildConfig.MOVIE_KEY;

        String id = intent.getStringExtra(ID);
        try {
            restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
            getVideoId = restAdapter.create(DetailActivityFragment.GetVideoId.class);
            VideoPojo pojo = getVideoId.getResult(id, API_VALUE);

            List<VideoResult> result = pojo.getResults();
            if (!result.isEmpty()) {
                VideoResult videoResult = result.get(0);
                String key = videoResult.getKey();

                Intent broadcastIntent = new Intent(KEY);
                broadcastIntent.putExtra(KEY, key);
                LocalBroadcastManager.getInstance(VideoService.this).sendBroadcast(broadcastIntent);

            } else {

                Intent broadcastIntent = new Intent("Key");
                broadcastIntent.putExtra(KEY, "");
                LocalBroadcastManager.getInstance(VideoService.this).sendBroadcast(broadcastIntent);
            }
        }catch (RetrofitError error){

        }
    }
}
