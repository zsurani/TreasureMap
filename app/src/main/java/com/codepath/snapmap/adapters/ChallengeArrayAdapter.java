package com.codepath.snapmap.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.snapmap.R;
import com.codepath.snapmap.models.Post;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by zsurani on 7/27/16.
 */
public class ChallengeArrayAdapter extends ArrayAdapter<Post> {

    private static class ViewHolder {
        ImageView ivBullet;
        TextView tvChallenge;
    }

    public ChallengeArrayAdapter(Context context, List<Post> recent) {
        super(context, android.R.layout.simple_list_item_1, recent);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Post travel = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_challenge, parent, false);
            viewHolder.tvChallenge = (TextView) convertView.findViewById(R.id.tvChallenge);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseUser owner = null;
        //            owner = travel.getOwner().fetchIfNeeded();
        Log.d("DEBUGGING", travel.getOwner().getUsername());
//        viewHolder.tvChallenge.setText(travel.getChallenge());

        //
//        final ParseUser u = travel.getOwner();
//        final String id = u.getObjectId();
//

//        JSONArray challengeArray = travel.getChallenge();
//
//        for (int i=0; i<challengeArray.length(); i++) {
//            try {
//                viewHolder.challenge.setText(challengeArray.get(i).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

//        viewHolder.challenge.setText(travel.getChallenge());


        return convertView;

    }

}
