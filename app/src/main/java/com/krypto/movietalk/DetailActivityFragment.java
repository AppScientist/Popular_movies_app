package com.krypto.movietalk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.krypto.movietalk.auto_generated_pojos_for_retrofit.video_pojo.VideoPojo;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Shows the Details of the selected movie
 */
public class DetailActivityFragment extends Fragment {

    private BroadcastReceiver mBroadcastReceiver;
    private String mKey;
    private String mTitle, mThumbnail, mOverview, mRelease, mVote;
    private String mId;
    private Movie mMovie;
    private Set<String> mFavoriteList = new HashSet<>();
    private ActivityName mActivityName;
    private final static String MOVIE = "Movie";
    private static final String GOOGLE_API_KEY = "AIzaSyDPCRC02svRDPfjBHlbcggs2ktz_GOj7U8";
    private static final String KEY = "Key";
    private static final String ID = "Id";


    @InjectView(R.id.videoThumbnail)
    ImageView mVideo;
    @InjectView(R.id.youtube)
    ImageView youtube;
    @InjectView(R.id.tab)
    TabLayout mTab;
    @InjectView(R.id.pager)
    ViewPager mPager;

    public DetailActivityFragment() {
    }

    public interface ActivityName {

        void setName(String name);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivityName = (ActivityName) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovie = bundle.getParcelable(MOVIE);
        } else {
            mMovie = getActivity().getIntent().getParcelableExtra(MOVIE);
        }

        mId = mMovie.getId();
        mTitle = mMovie.getTitle();
        mThumbnail = mMovie.getUrl();
        mOverview = mMovie.getOverview();
        mRelease = mMovie.getRelease();
        mVote = mMovie.getVote();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.inject(this, rootView);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),mMovie,getActivity());

        mPager.setAdapter(adapter);
        mTab.setupWithViewPager(mPager);
        mTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mTab.setBackgroundColor(getResources().getColor(R.color.md_indigo_500));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mTab.setElevation(10f);


        mActivityName.setName(mTitle);

        if (savedInstanceState != null) {
            mKey = savedInstanceState.getString(KEY);
            if (!mKey.equals(""))
                youtube.setVisibility(View.VISIBLE);

            Glide.with(getActivity()).load("http://img.youtube.com/vi/" + mKey + "/0.jpg").placeholder(R.drawable.trailer_placeholder).into(mVideo);

            if (Utility.checkConnection(getActivity())) {
                Intent serviceIntent2 = new Intent(getActivity(), ReviewFragment.ReviewService.class);
                serviceIntent2.putExtra(ID, mId);
                getActivity().startService(serviceIntent2);
            }
        }


        return rootView;
    }

    @OnClick({R.id.videoThumbnail, R.id.youtube})
    public void click() {

        try {
            Intent videoIntent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), GOOGLE_API_KEY, mKey);
            startActivity(videoIntent);
        } catch (ActivityNotFoundException a) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + mKey);
            intent.setData(uri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY, mKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(KEY);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                mKey = intent.getStringExtra(KEY);
                if (!mKey.equals("")) {
                    youtube.setVisibility(View.VISIBLE);
                } else {
                    youtube.setVisibility(View.GONE);
                }
                Glide.with(getActivity()).load("http://img.youtube.com/vi/" + mKey + "/0.jpg").placeholder(R.drawable.trailer_placeholder).into(mVideo);
                getActivity().invalidateOptionsMenu();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public interface GetVideoId {

        @GET("/3/movie/{id}/videos")
        VideoPojo getResult(@Path("id") String id, @Query("api_key") String key);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        mFavoriteList = Utility.getFavoriteList(getActivity());
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.share);

        MenuItem item2 = menu.findItem(R.id.rating);
        if (mFavoriteList.contains(mId)) {
            item2.setIcon(android.R.drawable.btn_star_big_on);
        } else {
            item2.setIcon(android.R.drawable.btn_star_big_off);
        }

        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        String msg = "Check out the trailer of " + mTitle + "\n" + "https://www.youtube.com/watch?v=" + mKey;

        Intent shareIntent = new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, msg).setType("text/plain").addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        mShareActionProvider.setShareIntent(shareIntent);

        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (!mFavoriteList.contains(mId)) {
                    item.setIcon(android.R.drawable.btn_star_big_on);
                    Toast.makeText(getActivity(), getString(R.string.fav), Toast.LENGTH_SHORT).show();
                    Utility.addFavoriteItem(getActivity(), mId);
                    Utility.saveInDb(getActivity(), mId, mTitle, mThumbnail, mOverview, mRelease, mVote);
                } else {
                    item.setIcon(android.R.drawable.btn_star_big_off);
                    Toast.makeText(getActivity(), getString(R.string.remove_fav), Toast.LENGTH_SHORT).show();
                    Utility.removeFromFavorites(getActivity(), mId);
                    Utility.removeFromDb(getActivity(), mId);
                }
                return true;
            }
        });
    }
}
