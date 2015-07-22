package com.krypto.movietalk;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class ViewPagerAdapter extends FragmentPagerAdapter {

    Movie movie;
    Context context;
    private final static String MOVIE = "Movie";

    public ViewPagerAdapter(FragmentManager manager, Movie movie, Context context) {

        super(manager);
        this.movie = movie;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
                InfoFragment infoFragment = new InfoFragment();
                if (isTablet) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MOVIE, movie);
                    infoFragment.setArguments(bundle);
                }
                return infoFragment;
            case 1:

                return new ReviewFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {


        switch (position) {
            case 0:
                return "INFO";
            case 1:
                return "REVIEWS";
        }
        return null;
    }
}
