package com.codepath.snapmap;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.codepath.snapmap.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ChallengeActivity extends MainActivity {

    private ListView lvChallen;
    boolean set = false;
    JSONArray postidArray = new JSONArray();
    int length;
    ArrayList<String> challengeList = new ArrayList<>();

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

//        String title = getIntent().getStringExtra("title");
        setTitle("ChallengeList");

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_challenge, contentFrameLayout);

        nvDrawer.getMenu().getItem(4).setChecked(true);

        lvChallen = (ListView) findViewById(R.id.lvChallen);

        ParseUser currentUser = ParseUser.getCurrentUser();
        postidArray = currentUser.getJSONArray("challengeAcceptedBy");

        length = postidArray.length();

        for (int i = 0; i < length; i++) {

            if (i == length-1) {
                set = true;
            }

            ParseQuery<Post> query = ParseQuery.getQuery("Post");
            try {
                query.whereEqualTo("objectId", postidArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            query.findInBackground(new FindCallback<Post>() {

                @Override
                public void done(List<Post> objects, ParseException e) {

                    JSONArray tempChallenge = new JSONArray();
                    if (e != null) {
                        e.printStackTrace();
                    }

                    if (objects != null) {
                        for (Post p : objects) {
                            Post post = new Post();
                            post.initializeChallenges();
                            post.setCategory(p.getCategory());
                            post.setLocation(p.getLocation());
                            post.setPrivacy(p.getPrivacy());
                            post.setDescription(p.getDescription());
                            post.setOwner(p.getOwner());
                            post.setCreationDate(p.getCreatedAt());
                            post.setPhotoFile(p.getPhotoFile());
                            post.setId(p.getId());
                            post.put("challenge1", p.getJSONArray("challenge1"));
                            tempChallenge = p.getJSONArray("challenge1");

                            JSONArray c_array = p.getChallengedBy();
                            if (c_array != null) {
                                post.setChallenged(c_array);
                            }
                        }
                    }

                    for (int j = 0; j < tempChallenge.length(); j++) {
                        try {
                            challengeList.add(String.valueOf(tempChallenge.get(j)));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (set == true) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_challenge, challengeList);

                        lvChallen.setAdapter(arrayAdapter);

                    }
                }
            });
        }




    }
}

//    private ListView lvChallenge;
//    private ArrayList<Post> posts;
//    private ChallengeArrayAdapter adapter;
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
//        getLayoutInflater().inflate(R.layout.activity_challenge, contentFrameLayout);
//
//        nvDrawer.getMenu().getItem(4).setChecked(true);
//
//        lvChallenge = (ListView) findViewById(R.id.lvChallenge);
//        posts = new ArrayList<Post>();
//
//        adapter = new ChallengeArrayAdapter(ChallengeActivity.this, posts);
//        lvChallenge.setAdapter(adapter);
//
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        JSONArray challengeArray = currentUser.getJSONArray("challengeAcceptedBy");
//
//
//        String[] challengeList = new String[challengeArray.length()];
//
//        if (challengeArray != null) {
//            int len = challengeArray.length();
//            for (int i = 0; i < len; i++) {
//                try {
//                    if (challengeArray.get(i) != "") {
//                        challengeList[i] = (String) challengeArray.get(i);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        ParseQuery<Post> query = ParseQuery.getQuery("Post");
//        query.whereContainedIn("objectId", Arrays.asList(challengeList));
////        query.whereContainedIn("privacy", Arrays.asList(bucketList));
//        query.findInBackground(new FindCallback<Post>() {
//            @Override
//            public void done(List<Post> objects, ParseException e) {
//
//                if (e != null) {
//                    e.printStackTrace();
//                }
//
//                if (objects != null) {
//                    for (Post p : objects) {
//                        Post post = new Post();
//                        post.initializeChallenges();
//                        post.setCategory(p.getCategory());
//                        post.setLocation(p.getLocation());
//                        post.setPrivacy(p.getPrivacy());
//                        post.setDescription(p.getDescription());
//                        post.setOwner(p.getOwner());
//                        post.setCreationDate(p.getCreatedAt());
//                        post.setPhotoFile(p.getPhotoFile());
//                        post.setId(p.getId());
////                        post.setChallenge(p.getChallenge());
//
//                        JSONArray c_array = p.getChallengedBy();
//                        if (c_array != null) {
//                            post.setChallenged(c_array);
//                        }
//
//                        posts.add(post);
//
//                    }
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//        });
//    }