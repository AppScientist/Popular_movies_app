package com.krypto.movietalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.ButterKnife;
import butterknife.InjectView;



public class CustomAdapter extends ArrayAdapter<Movie> {

    int mLayout;

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
        mLayout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        Movie movieObject = getItem(position);
        if (movieObject.getUrl() != null)
            Glide.with(getContext()).load(movieObject.getUrl()).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.imageView);
        viewHolder.textView.setText(movieObject.getTitle());

        return convertView;
    }


    static class ViewHolder {

        @InjectView(R.id.moviePoster)
        ImageView imageView;
        @InjectView(R.id.movieName)
        TextView textView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}