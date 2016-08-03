package com.codepath.snapmap;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.codepath.snapmap.adapters.RecentArrayAdapter;
import com.codepath.snapmap.models.Post;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteredTimelineActivity extends MainActivity {

    ListView lvPosts;
    ArrayList<Post> posts;
    RecentArrayAdapter adapter;
    private int BUCKET_CODE;
    String postId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_filtered_timeline, contentFrameLayout);

        lvPosts = (ListView) findViewById(R.id.lvFiltered);
        posts = new ArrayList<Post>();

        adapter = new RecentArrayAdapter(FilteredTimelineActivity.this, posts);
        lvPosts.setAdapter(adapter);

        String switchType = getIntent().getStringExtra("type");

        BUCKET_CODE = getIntent().getIntExtra("code",100);
        if(BUCKET_CODE == 50) {
            postId = getIntent().getStringExtra("postid");
            switchType = "all";
        }
        if(BUCKET_CODE == 88) {
            switchType = "all";
        }

        //distinguishes between public and following feed
        if(switchType.equals("all")) {
            String screenname = getIntent().getStringExtra("username");

            if(screenname != null) {
                ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                queryUser.whereEqualTo("username",screenname);

                queryUser.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        ParseUser user = (ParseUser) object;
                        String userId = user.getObjectId();

                        String location = getIntent().getStringExtra("location");
                        String category = getIntent().getStringExtra("category");
                        String time = getIntent().getStringExtra("time");

                        Log.d("VALUES",location + " " + userId + " " + category);

                        //perform the query for the results and refresh
                        ParseQuery<Post> query = new ParseQuery<Post>("Post");

                        query.whereEqualTo("privacy","public");

                        if(time.equals("recent")) {
                            //filter by recency
                            query.orderByDescending("createdAt");
                        }
                        else {
                            //filter by top posts
                            query.orderByDescending("likeCount");
                        }

                        if(location != null) {
                            query.whereEqualTo("location",location);
                        }

                        if(userId != null ) {
                            query.whereEqualTo("parentId", userId);
                        }

                        if(category != null) {
                            query.whereEqualTo("category",category);
                        }

                        query.findInBackground(new FindCallback<Post>() {
                            @Override
                            public void done(List<Post> objects, ParseException e) {
                                for (Post p: objects){
                                    //copy Parse post data into locally created Post objects
                                    Post post = new Post();
                                    post.initializeLikes();
//                        post.initializeChallenges();

                                    post.setCategory(p.getCategory());
                                    post.setLocation(p.getLocation());
                                    post.setPrivacy(p.getPrivacy());

                                    if (p.getDescription() != null) {
                                        post.setDescription(p.getDescription());
                                    }

                                    post.setOwner(p.getOwner());
                                    post.setCreationDate(p.getCreatedAt());

                                    if (p.getPhotoFile() != null) {
                                        post.setPhotoFile(p.getPhotoFile());
                                    }
                                    post.setId(p.getId());

//                        if (p.getChallenge() != null) {
//                            post.setChallenge(p.getChallenge());
//                        }

                                    if(p.containsKey("action")) {
                                        post.setAction(p.getAction());
                                    }

                                    JSONArray array = p.getLikedBy();
                                    if (array != null) {
                                        post.setLikes(array);
                                    }

                                    if (p.getJSONArray("challenge1") != null) {
                                        post.put("challenge1", p.getJSONArray("challenge1"));
                                    }


                                    if (BUCKET_CODE == 50 && !p.getId().equals(postId)) {

                                    } else {
                                        posts.add(post);
                                    }

                                    //add each post to List of posts for displaying in ListView
                                }
                                //update adapter/ListView
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
            else {
                String location = getIntent().getStringExtra("location");
                String category = getIntent().getStringExtra("category");
                String time = getIntent().getStringExtra("time");
                Log.d("VALUES",location + " " + "null" + " " + category);

                //perform the query for the results and refresh
                ParseQuery<Post> query = new ParseQuery<Post>("Post");

                query.whereEqualTo("privacy","public");

                if(time.equals("recent")) {
                    //filter by recency
                    query.orderByDescending("createdAt");
                }
                else {
                    //filter by top posts
                    query.orderByDescending("likeCount");
                }

                if(location != null) {
                    query.whereEqualTo("location",location);
                }

                if(category != null) {
                    query.whereEqualTo("category",category);
                }
                // where's the code

                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> objects, ParseException e) {
                        for (Post p : objects) {
                            //copy Parse post data into locally created Post objects
                            Post post = new Post();
                            post.initializeLikes();
//                        post.initializeChallenges();

                            post.setCategory(p.getCategory());
                            post.setLocation(p.getLocation());
                            post.setPrivacy(p.getPrivacy());

                            if (p.getDescription() != null) {
                                post.setDescription(p.getDescription());
                            }

                            post.setOwner(p.getOwner());
                            post.setCreationDate(p.getCreatedAt());

                            if (p.getPhotoFile() != null) {
                                post.setPhotoFile(p.getPhotoFile());
                            }
                            post.setId(p.getId());


                            if(p.containsKey("action")) {
                                post.setAction(p.getAction());
                            }

                            JSONArray array = p.getLikedBy();
                            if (array != null) {
                                post.setLikes(array);
                            }

                            if (p.getJSONArray("challenge1") != null) {
                                post.put("challenge1", p.getJSONArray("challenge1"));
                            }


                            //add each post to List of posts for displaying in ListView


                            if (BUCKET_CODE == 50 && p.getId().equals(postId)) {

                            } else {
                                posts.add(post);
                            }
                            //update adapter/ListView
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
        else {
            String[] followingList = new String[0];
            try {
                followingList = convertToNormalArray(ParseUser.getCurrentUser().getJSONArray("following"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String location = getIntent().getStringExtra("location");
            String category = getIntent().getStringExtra("category");
            String time = getIntent().getStringExtra("time");

            //perform the query for the results and refresh
            ParseQuery<Post> query = new ParseQuery<Post>("Post");

            query.whereContainedIn("parentId", Arrays.asList(followingList));

            if(time.equals("recent")) {
                //filter by recency
                query.orderByDescending("createdAt");
            }
            else {

                //filter by top posts
                query.orderByDescending("likeCount");
            }

            if(location != null) {
                query.whereEqualTo("location",location);
            }


            if(category != null) {
                query.whereEqualTo("category",category);
            }

            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {
                    for (Post p: objects){
                        //copy Parse post data into locally created Post objects
                        Post post = new Post();
                        post.initializeLikes();
//                        post.initializeChallenges();

                        post.setCategory(p.getCategory());
                        post.setLocation(p.getLocation());
                        post.setPrivacy(p.getPrivacy());

                        if (p.getDescription() != null) {
                            post.setDescription(p.getDescription());
                        }

                        post.setOwner(p.getOwner());
                        post.setCreationDate(p.getCreatedAt());

                        if (p.getPhotoFile() != null) {
                            post.setPhotoFile(p.getPhotoFile());
                        }
                        post.setId(p.getId());


                        if(p.containsKey("action")) {
                            post.setAction(p.getAction());
                        }

                        JSONArray array = p.getLikedBy();
                        if (array != null) {
                            post.setLikes(array);
                        }

                        if (p.getJSONArray("challenge1") != null) {
                            post.put("challenge1", p.getJSONArray("challenge1"));
                        }


                        //add each post to List of posts for displaying in ListView
                        posts.add(post);
                    }
                    //update adapter/ListView
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }

    public String[] convertToNormalArray(JSONArray jsonArray) throws JSONException {
        ArrayList<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.get(i).toString());
            }
        }

        return list.toArray(new String[0]);
    }
}