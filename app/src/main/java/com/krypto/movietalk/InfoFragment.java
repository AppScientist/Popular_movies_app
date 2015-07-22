package com.krypto.movietalk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Displays basic info about selected movie
 */
public class InfoFragment extends Fragment {

    private String mThumbnail, mOverview, mRelease, mVote;
    private Movie mMovie;
    private final static String MOVIE = "Movie";

    @InjectView(R.id.thumbnail)
    ImageView mImageView;
    @InjectView(R.id.release)
    TextView mDate;
    @InjectView(R.id.overview)
    TextView mSummary;
    @InjectView(R.id.rating)
    TextView mRating;

    public InfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.inject(this, rootView);


        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovie = bundle.getParcelable(MOVIE);
        } else {
            mMovie = getActivity().getIntent().getParcelableExtra(MOVIE);
        }

        mThumbnail = mMovie.getUrl();
        mOverview = mMovie.getOverview();
        mRelease = mMovie.getRelease();
        mVote = mMovie.getVote();


        if (mThumbnail != null) {
            Glide.with(getActivity()).load(mThumbnail).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImageView);
        }

        mDate.setTypeface(Utility.get(getActivity(), getString(R.string.roboto_regular)));
        mSummary.setTypeface(Utility.get(getActivity(), getString(R.string.roboto_regular)));
        mRating.setTypeface(Utility.get(getActivity(), getString(R.string.roboto_regular)));

        Utility.getColoredText(getActivity(), mDate, "Release Date:\n" + mRelease, "Release Date: ");
        Utility.getColoredText(getActivity(), mSummary, "Plot Synopsis:\n" + mOverview, "Plot Synopsis: ");
        Utility.getColoredText(getActivity(), mRating, "User Rating: " + mVote + "/10", "User Rating: ");

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
