package com.krypto.movietalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CustomArrayAdapter extends ArrayAdapter<Review> {

    ViewHolder viewHolder;

    public CustomArrayAdapter(Context context, int resource, List<Review> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_review_content, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Review reviewObject = getItem(position);

        viewHolder.critic.setTypeface(Utility.get(getContext(), getContext().getString(R.string.roboto_regular)));
        Utility.getColoredText(getContext(), viewHolder.critic, "Critic: " + reviewObject.getCritic(), "Critic: ");

        viewHolder.quote.setTypeface(Utility.get(getContext(), getContext().getString(R.string.roboto_regular)));
        Utility.getColoredText(getContext(), viewHolder.quote, "Quote: " + reviewObject.getQuote(), "Quote: ");

        return convertView;
    }


    static class ViewHolder {

        @InjectView(R.id.critic)
        TextView critic;
        @InjectView(R.id.quote)
        TextView quote;

        public ViewHolder(View v) {
            ButterKnife.inject(this, v);
        }
    }
}
