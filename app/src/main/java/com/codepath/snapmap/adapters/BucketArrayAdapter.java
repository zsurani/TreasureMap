package com.codepath.snapmap.adapters;

/**
 * Created by zsurani on 7/9/16.
 */

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.snapmap.DetailBucketActivity;
import com.codepath.snapmap.FinalProfileActivity;
import com.codepath.snapmap.R;
import com.codepath.snapmap.UserProfileActivity;
import com.codepath.snapmap.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class BucketArrayAdapter extends ArrayAdapter<Post>{


    private static class ViewHolder {
        TextView location;
        ImageView ivPicture;
        TextView tvUsername;
        ImageView ivProfile;
        ImageView bucket;
        ListView lvBucket;
    }

    public BucketArrayAdapter(Context context, List<Post> recent) {
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

            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvBucketUsername);
            viewHolder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            viewHolder.bucket = (ImageView) convertView.findViewById(R.id.ivBucket);
            viewHolder.location = (TextView) convertView.findViewById(R.id.tvLoc);
            viewHolder.lvBucket = (ListView) convertView.findViewById(R.id.lvBucket);


            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseFile photo = travel.getPhotoFile();

        ParseUser owner = null;
        String ownerUsername = "";
        try {
            owner = travel.getOwner().fetchIfNeeded();
            ownerUsername = "<font color='#cc9618'>"+owner.fetchIfNeeded().getUsername()+"</font>";
            Log.d("DEBUGGING", travel.getOwner().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.tvUsername.setText(Html.fromHtml(ownerUsername));

        String location = travel.getLocation();
        if(location.length() > 25) {
            boolean comma = false;
            for (int i = 25; i > 0; i--){
                char letter = location.charAt(i);
                if (letter == ','){
                    viewHolder.location.setText(location.substring(0,i));
                    comma = true;
                    break;
                }
            }
            if (comma = false){
                viewHolder.location.setText(location.substring(0,22) + "...");
            }
        }
        else {
            viewHolder.location.setText(location);
        }

        ParseFile file = owner.getParseFile("imageupload"); // either convert to parsefile or...is it a parsefile?
        Picasso.with(getContext()).load(file.getUrl()).into(viewHolder.ivProfile);
        Picasso.with(getContext()).load(photo.getUrl()).into(viewHolder.ivPicture);

        final ParseUser u = travel.getOwner();
        final String id = u.getObjectId();
        final ParseUser currentUser = ParseUser.getCurrentUser();


        viewHolder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id.equals(currentUser.getObjectId())) {
                    Intent intent = new Intent(getContext(), FinalProfileActivity.class);
                    getContext().startActivity(intent);
                }

                else {

                    Intent intent = new Intent(getContext(), UserProfileActivity.class);

                    intent.putExtra("userid", id);
                    //Toast.makeText(getContext(), tweet.getUser().getName(), Toast.LENGTH_SHORT).show();
                    getContext().startActivity(intent);
                }
            }
        });

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                ParseUser currentUser = ParseUser.getCurrentUser();

                if (id.equals(currentUser.getObjectId())) {
                    Intent intent = new Intent(getContext(), FinalProfileActivity.class);
                    getContext().startActivity(intent);
                }

                else {

                    Intent intent = new Intent(getContext(), UserProfileActivity.class);

                    intent.putExtra("userid", id);
                    //Toast.makeText(getContext(), tweet.getUser().getName(), Toast.LENGTH_SHORT).show();
                    getContext().startActivity(intent);
                }
            }
        });

        Picasso.with(getContext()).load(R.drawable.ic_bucket_g).into(viewHolder.bucket);

        viewHolder.bucket.setOnClickListener(new View.OnClickListener() {

            String bucketId = travel.getObjectId();

            public void onClick(View v) {

                try {

                    boolean bucket_status = true;

                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    final JSONArray bucketArray = currentUser.getJSONArray("bucket");

                    if (bucketArray != null) {
                        for (int i = 0; i < bucketArray.length(); i++) {
                            if (bucketArray.get(i).equals(bucketId)) {
                                bucket_status = false;
                                JSONArray temp = new JSONArray();
                                for (int j = 0; j < bucketArray.length(); j++) {
                                    if (bucketArray.get(j) != bucketArray.get(i)) {
                                        temp.put(bucketArray.get(j));
                                    }
                                }
                                Picasso.with(getContext()).load(R.drawable.bucketlist).into(viewHolder.bucket);
                                currentUser.put("bucket", temp);
                            }
                        }
                    }

                    if (bucket_status) {
                        currentUser.addUnique("bucket", bucketId);
                        Picasso.with(getContext()).load(R.drawable.ic_bucket_g).into(viewHolder.bucket);
                    }

                    currentUser.saveInBackground();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });



        viewHolder.ivPicture.setOnClickListener(new View.OnClickListener() {

            String detailId = travel.getObjectId();

            public void onClick(View v) {

                final String location = travel.getLocation();

                Intent intent = new Intent(getContext(), DetailBucketActivity.class);

                intent.putExtra("location", location);
                intent.putExtra("postid", detailId);
                //Toast.makeText(getContext(), tweet.getUser().getName(), Toast.LENGTH_SHORT).show();
                getContext().startActivity(intent);
            }
        });

        return convertView;

    }

}