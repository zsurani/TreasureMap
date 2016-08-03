package com.codepath.snapmap.adapters;

/**
 * Created by zsurani on 7/9/16.
 */

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.snapmap.DetailTimelineActivity;
import com.codepath.snapmap.FinalProfileActivity;
import com.codepath.snapmap.MainActivity;
import com.codepath.snapmap.R;
import com.codepath.snapmap.UserProfileActivity;
import com.codepath.snapmap.models.Post;
import com.ocpsoft.pretty.time.PrettyTime;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentArrayAdapter extends ArrayAdapter<Post>{


    private static class ViewHolder {
        //        TextView location;
        ImageView ivTravel;
        TextView nameAndLoc;
        TextView description;
        TextView tvLocation;
        TextView tvUpvoteCount;
        ImageView ivProfile;
        ImageView ivUpvote;
        ImageView bucket;
        TextView tvTime;
        ListView lvTime;
    }

    public RecentArrayAdapter(Context context, List<Post> recent) {
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
            convertView = inflater.inflate(R.layout.item_post, parent, false);
//            viewHolder.location = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.nameAndLoc = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.description = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.ivTravel = (ImageView) convertView.findViewById(R.id.ivPicture);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            viewHolder.ivUpvote = (ImageView) convertView.findViewById(R.id.ivUp);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvLoc);
            viewHolder.tvUpvoteCount = (TextView) convertView.findViewById(R.id.tvUpvotes);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.lvTime = (ListView) convertView.findViewById(R.id.lvTime);

            // JUST ADDED
            viewHolder.bucket = (ImageView) convertView.findViewById(R.id.ivBucket);


            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JSONArray array = travel.getJSONArray("likedBy");

        if (array.length() == 0) {
            Picasso.with(getContext()).load(R.drawable.upvote).into(viewHolder.ivUpvote);
        }
        else { // good until

            ParseUser currentUser = ParseUser.getCurrentUser();
            boolean found = false;
            for (int i = 0; i < array.length(); i++) {
                try {
                    if (array.get(i).equals(currentUser.getObjectId())) {
                        found = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (found) {
                Picasso.with(getContext()).load(R.drawable.ic_upvote_gold).into(viewHolder.ivUpvote);
            }

            else {
                Picasso.with(getContext()).load(R.drawable.upvote).into(viewHolder.ivUpvote);
            }
        }

        viewHolder.ivUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    String stringLikeCount = (String) viewHolder.tvUpvoteCount.getText();

                    String id = ParseUser.getCurrentUser().getObjectId();
                    if (getItem(position).like(id)) {
                        Toast.makeText(getContext(), "Post liked!", Toast.LENGTH_SHORT).show();
                        Picasso.with(getContext()).load(R.drawable.ic_upvote_gold).into(viewHolder.ivUpvote);
//                        int newLikeCount = Integer.parseInt(stringLikeCount) + 1;

                        try {
                            String push_id = getItem(position).getOwner().fetchIfNeeded().getString("push_id");
                            MainActivity.createNotification(push_id,"One of your posts was liked!");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Post unliked!", Toast.LENGTH_SHORT).show();
                        Picasso.with(getContext()).load(R.drawable.upvote).into(viewHolder.ivUpvote);
//                        int newLikeCount = Integer.parseInt(stringLikeCount) - 1;
                    }

                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.tvUpvoteCount.setText(Integer.toString(travel.getLikeCount()));

//        viewHolder.location.setText(travel.getLocation());

        String location = travel.getLocation();

        if(location.length() >= 24) {
            viewHolder.tvLocation.setText(location.substring(0, 21) + "...");
        }

        String description = travel.getDescription();

        PrettyTime p = new PrettyTime();
        Date postDate = travel.getCreationDate();

        viewHolder.tvTime.setText(p.format(postDate));

        ParseFile photo = travel.getPhotoFile();

        ParseUser owner = null;
        try {
            owner = travel.getOwner().fetchIfNeeded();
//            Log.d("DEBUGGING", travel.getOwner().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String ownerUsername = "<font color='#cc9618'>"+owner.getUsername()+"</font>";

        String action = "";
        if(travel.containsKey("action"))
        {
            action = travel.getAction();
        }
        else {
            action = "traveled across the Atlantic Ocean in 7 days";
        }

        viewHolder.nameAndLoc.setText(Html.fromHtml(ownerUsername +" "+ action));


        ParseFile file = owner.getParseFile("imageupload"); // either convert to parsefile or...is it a parsefile?
        Picasso.with(getContext()).load(file.getUrl()).into(viewHolder.ivProfile);

        viewHolder.description.setText(travel.getDescription());
        Picasso.with(getContext()).load(photo.getUrl()).into(viewHolder.ivTravel);

        final ParseUser u = travel.getOwner();
        final String id = u.getObjectId();
        final ParseUser currentUser = ParseUser.getCurrentUser();


        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

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

        viewHolder.nameAndLoc.setOnClickListener(new View.OnClickListener() {
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

        JSONArray challengeArray = new JSONArray();
        try {
            challengeArray = travel.fetchIfNeeded().getJSONArray("challenge1");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final JSONArray finalChallengeArray = challengeArray;
        viewHolder.ivTravel.setOnClickListener(new View.OnClickListener() {

            String postId = travel.getObjectId();
            ArrayList<String> challengeList = new ArrayList<String>();

            public void onClick(View v) {

                Intent intent = new Intent(getContext(), DetailTimelineActivity.class);
                intent.putExtra("postid", postId);
                if (finalChallengeArray != null) {
                    for (int i = 0; i < finalChallengeArray.length(); i++) {
                        try {
                            challengeList.add(finalChallengeArray.get(i).toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                intent.putStringArrayListExtra("challengeList", challengeList);

                getContext().startActivity(intent);
            }
        });


        // JUST ADDED
        final JSONArray bucketArray = currentUser.getJSONArray("bucket");

        // JUST ADDED - just setting the colors
        if (bucketArray.length() == 0) {
            Picasso.with(getContext()).load(R.drawable.bucketlist).into(viewHolder.bucket);
        }
        else { // good until

            boolean found = false;
            for (int i = 0; i < bucketArray.length(); i++) {
                try {
                    if (bucketArray.get(i).equals(travel.getId())) {
                        found = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (found) {
                Picasso.with(getContext()).load(R.drawable.ic_bucket_g).into(viewHolder.bucket);
            }

            else {
                Picasso.with(getContext()).load(R.drawable.bucketlist).into(viewHolder.bucket);
            }
        }


        // problem is what happened to the set size of a JSON array

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



        return convertView;

    }


}