package com.krypto.movietalk;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailActivity extends BaseActivity implements DetailActivityFragment.ActivityName {

    @InjectView(R.id.app_bar)
    Toolbar mAppBar;
    @InjectView(R.id.toolbarTitle)
    TextView mToolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        activateToolbar();
        Boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (getSupportActionBar() != null && !isTablet) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mAppBar.getBackground().setAlpha(0);
        }

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragment, new DetailActivityFragment()).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setName(String name) {
        mToolBarTitle.setText(name);
        mToolBarTitle.setTypeface(Utility.get(this, getString(R.string.roboto_medium)));
    }
}
