package com.codepath.snapmap;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.codepath.snapmap.adapters.BucketArrayAdapter;
import com.codepath.snapmap.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BucketActivity extends MainActivity {

    private ListView lvBucket;
    private ArrayList<Post> posts;
    private BucketArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("BucketList");
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_bucket, contentFrameLayout);

        nvDrawer.getMenu().getItem(3).setChecked(true);

        lvBucket = (ListView) findViewById(R.id.lvBucket);
        posts = new ArrayList<Post>();

        adapter = new BucketArrayAdapter(BucketActivity.this, posts);
        lvBucket.setAdapter(adapter);

        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray bucketArray = currentUser.getJSONArray("bucket");


        String[] bucketList = new String[bucketArray.length()];

        if (bucketArray != null) {
            int len = bucketArray.length();
            for (int i = 0; i < len; i++) {
                try {
                    bucketList[i] = (String) bucketArray.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.whereContainedIn("objectId", Arrays.asList(bucketList));
//        query.whereContainedIn("privacy", Arrays.asList(bucketList));
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {

                if (e != null) {
                    e.printStackTrace();
                }

                if (objects != null) {
                    for (Post p : objects) {
                        Post post = new Post();
                        post.setCategory(p.getCategory());
                        post.setLocation(p.getLocation());
                        post.setPrivacy(p.getPrivacy());
                        post.setDescription(p.getDescription());
                        post.setOwner(p.getOwner());
                        post.setCreationDate(p.getCreatedAt());
                        post.setPhotoFile(p.getPhotoFile());
                        post.setId(p.getId());

                        posts.add(post);
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });
    }
}
