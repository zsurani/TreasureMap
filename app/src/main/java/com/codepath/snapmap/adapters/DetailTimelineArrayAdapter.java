package com.codepath.snapmap.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.snapmap.R;
import com.codepath.snapmap.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by zsurani on 7/28/16.
 */



public class DetailTimelineArrayAdapter extends ArrayAdapter<Post> {


    JSONArray challengeArray = new JSONArray();

    private static class ViewHolder {

        TextView tvCount;
        ImageView ivAccept;

    }

    public DetailTimelineArrayAdapter(Context context, List<Post> recent) {
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
            convertView = inflater.inflate(R.layout.item_bucket, parent, false);

            viewHolder.ivAccept = (ImageView) convertView.findViewById(R.id.ivAccept);
            viewHolder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);


            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        ParseUser owner = null;
        try {
            owner = travel.getOwner().fetchIfNeeded();
            Log.d("DEBUGGING", travel.getOwner().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // put in something about keeping track of acceptances
//        viewHolder.nameAndLoc.setText(owner.getUsername()
                //+ " visited " + travel.getLocation());


        final ParseUser u = travel.getOwner();
        final String postid = u.getObjectId();

        final View finalConvertView = convertView;
        viewHolder.ivAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String id = ParseUser.getCurrentUser().getObjectId();
                ParseUser currentUser = ParseUser.getCurrentUser();

                try {


                    if (currentUser.getJSONArray("challengeAcceptedBy") == null) {
                        currentUser.put("challengeAcceptedBy", new JSONArray());
                    } else {
                        challengeArray = currentUser.getJSONArray("challengeAcceptedBy");
                    }

                    if (travel.challenged(id)) {
                        Toast.makeText(getContext(), "Post liked!", Toast.LENGTH_SHORT).show();
                        currentUser.addUnique("challengeAcceptedBy", postid);
                        currentUser.saveInBackground();
                        Toast.makeText(getContext(), "Challenge Accepted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Post unliked!", Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < challengeArray.length(); i++) {
                            if (challengeArray.get(i).equals(postid)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    challengeArray.remove(i);
                                }
                            }
                        }

                        currentUser.put("challengeAcceptedBy", challengeArray);
                        Toast.makeText(getContext(), "Challenge Declined!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


                return convertView;

    }

}