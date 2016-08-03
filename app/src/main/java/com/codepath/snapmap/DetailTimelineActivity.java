package com.codepath.snapmap;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.snapmap.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DetailTimelineActivity extends AppCompatActivity {

    private ListView lvChallenges;
    String postid;
    JSONArray challengeArray = new JSONArray();
    TextView tvCount;
    JSONArray array;
    ImageView ivAccept;
    JSONArray array1;

    Post post;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_detail_timeline);

        ivAccept = (ImageView) findViewById(R.id.ivAccept);


//        lvPosition = (ListView) findViewById(R.id.lvPosition);
//        posts = new ArrayList<Post>();
//        adapter = new ChallengeArrayAdapter(DetailTimelineActivity.this, posts);
//        lvPosition.setAdapter(adapter);

        ivAccept = (ImageView) findViewById(R.id.ivAccept);

        postid = getIntent().getStringExtra("postid");

        tvCount = (TextView) findViewById(R.id.tvCount);

        lvChallenges = (ListView) findViewById(R.id.lvChall);

        ArrayList challengeList = getIntent().getStringArrayListExtra("challengeList");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                challengeList);

        lvChallenges.setAdapter(arrayAdapter);

        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.whereEqualTo("objectId", postid);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {

                if (e != null) {
                    e.printStackTrace();
                }

                if (objects != null) {
                    for (Post p : objects) {
                        post = new Post();
                        post.initializeChallenges();
                        post.setCategory(p.getCategory());
                        post.setLocation(p.getLocation());
                        post.setPrivacy(p.getPrivacy());
                        post.setDescription(p.getDescription());
                        post.setOwner(p.getOwner());
                        post.setCreationDate(p.getCreatedAt());
                        post.setPhotoFile(p.getPhotoFile());
                        post.setId(p.getId());

                        if (p.getJSONArray("challenge1") != null) {
                            post.put("challenge1", p.getJSONArray("challenge1"));
                        }

                        if (p.getJSONArray("challengeAcceptedBy") != null) {
                            post.put("challengeAcceptedBy", p.getJSONArray("challengeAcceptedBy"));
                            array1 = p.getJSONArray("challengeAcceptedBy");
                        }
                        else {
                            JSONArray arrayy = new JSONArray();
                            post.put("challengeAcceptedBy", arrayy);
                            array1 = arrayy;
                        }

                    }
                }

                tvCount.setText(Integer.toString(post.getChallengedCount()));

                if (array1.length() == 0) {
                    Picasso.with(getApplicationContext()).load(R.drawable.ic_accept).into(ivAccept);
                }
                else { // good until

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    boolean found = false;
                    for (int i = 0; i < array1.length(); i++) {
                        try {
                            if (array1.get(i).equals(currentUser.getObjectId())) {
                                found = true;
                                break;
                            }
                        } catch (JSONException f) {
                            f.printStackTrace();
                        }
                    }

                    if (found) {
                        Picasso.with(getApplicationContext()).load(R.drawable.ic_accept_gold).into(ivAccept);
                    }

                    else {
                        Picasso.with(getApplicationContext()).load(R.drawable.ic_accept).into(ivAccept);
                    }
                }


                ivAccept.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String id = ParseUser.getCurrentUser().getObjectId();
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        JSONArray array = post.getJSONArray("challengeAcceptedBy");

                        try {

                            if (currentUser.getJSONArray("challengeAcceptedBy") == null) {
                                currentUser.put("challengeAcceptedBy", new JSONArray());
                            } else {
                                challengeArray = currentUser.getJSONArray("challengeAcceptedBy");
                            }

                            if (post.challenged(id)) {
                                currentUser.addUnique("challengeAcceptedBy", postid);
                                currentUser.saveInBackground();
                                Toast.makeText(getBaseContext(), "Challenge Accepted!", Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.ic_accept_gold).into(ivAccept);
                                tvCount.setText(Integer.toString(post.getChallengedCount()));

                            } else {
                                for (int i = 0; i < challengeArray.length(); i++) {
                                    if (challengeArray.get(i).equals(postid)) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            challengeArray.remove(i);
                                        }
                                    }
                                }
                                currentUser.put("challengeAcceptedBy", challengeArray);
                                currentUser.saveInBackground();
                                Toast.makeText(getBaseContext(), "Challenge Declined!", Toast.LENGTH_SHORT).show();
                                Picasso.with(getApplicationContext()).load(R.drawable.ic_accept).into(ivAccept);
                                tvCount.setText(Integer.toString(post.getChallengedCount()));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });

    }
}





//public class DetailTimelineActivity extends AppCompatActivity {
//
//    //    TextView challenge;
//    ImageView ivAccept;
//    TextView tvCount;
//
//    JSONArray challengeArray;
//    String postid;
//
//    private ListView lvChallenges;
//    private ArrayList<Post> posts;
//    private DetailTimelineArrayAdapter adapter;
//    ArrayList<String> challengeList = new ArrayList<>();
//    JSONArray challengeArrayy = new JSONArray();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_timeline);
//
//        lvChallenges = (ListView) findViewById(R.id.lvChallenge);
//        posts = new ArrayList<Post>();
//
//        adapter = new DetailTimelineArrayAdapter(DetailTimelineActivity.this, posts);
//        lvChallenges.setAdapter(adapter);
//
//
////        challenge = (TextView) findViewById(R.id.tvChallenge);
////        final String chall = getIntent().getStringExtra("challenge");
//
////        tvCount = (TextView) findViewById(R.id.tvCount);
////        ivAccept = (ImageView) findViewById(R.id.ivAccept);
//
//        postid = getIntent().getStringExtra("postid");
//
//        ParseQuery<Post> query = ParseQuery.getQuery("Post");
//        query.whereEqualTo("objectId", postid);
//        query.findInBackground(new FindCallback<Post>() {
//            @Override
//            public void done(List<Post> objects, ParseException e) {
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
//                        post.put("challenge1", p.getJSONArray("challenge1"));
//
//                        challengeArrayy = p.getJSONArray("challenge1");
//
//                        JSONArray c_array = p.getChallengedBy();
//                        if (c_array != null) {
//                            post.setChallenged(c_array);
//                        }
//                    }
//
//                    adapter.clear();
//                    if (challengeArray != null) {
//                        for (int i=0;i<challengeArrayy.length();i++){
//                            try {
//                                challengeList.add(challengeArrayy.get(i).toString());
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//                    if (challengeArrayy != null){
//
//                        for (Object object : challengeList) {
//
//                            adapter.insert((Post) object, adapter.getCount());
//                        }
//                    }
//
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//        });
//    }
//}








//            tvCount.setText(post.getChallengedBy().length());



//        ivAccept.setOnClickListener(new View.OnClickListener() {
//
//            String challengeId = postid;
//
//            @Override
//            public void onClick(View view) {
//
//                String id = ParseUser.getCurrentUser().getObjectId();
//                ParseUser currentUser = ParseUser.getCurrentUser();
//
//                try {
//
//
//                    if (currentUser.getJSONArray("challengeAcceptedBy") == null) {
//                        currentUser.put("challengeAcceptedBy", new JSONArray());
//                    } else {
//                        challengeArray = currentUser.getJSONArray("challengeAcceptedBy");
//                    }
//
//                    if (post.challenged(id)) {
//                        Toast.makeText(getBaseContext(), "Post liked!", Toast.LENGTH_SHORT).show();
//                        currentUser.addUnique("challengeAcceptedBy", challengeId);
//                        currentUser.saveInBackground();
//                        Toast.makeText(getBaseContext(), "Challenge Accepted!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getBaseContext(), "Post unliked!", Toast.LENGTH_SHORT).show();
//
//                        for (int i = 0; i < challengeArray.length(); i++){
//                            if (challengeArray.get(i).equals(postid)){
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                    challengeArray.remove(i);
//                                }
//                            }
//                        }

//                        currentUser.put("challengeAcceptedBy", challengeArray);
//                        Toast.makeText(getBaseContext(), "Challenge Declined!", Toast.LENGTH_SHORT).show();



//                        for (int i = 0; i < challengeArray.length(); i++) {
//                            JSONArray temp = new JSONArray();
//
//                            for (int j = 0; j < challengeArray.length(); j++) {
//                                if (challengeArray.get(j) != challengeArray.get(i)) {
//                                    temp.put(challengeArray.get(j));
//                                }
//                            }
//                            currentUser.put("challengeAcceptedBy", temp);
//                            Toast.makeText(getBaseContext(), "Challenge Declined!", Toast.LENGTH_SHORT).show();
//                        }
//                    }

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                    if (challengeArray != null) {
//                        for (int i = 0; i < challengeArray.length(); i++) {
//                            if (challengeArray.get(i).equals(challengeId)) {
//                                challenge_status = false;
//                                JSONArray temp = new JSONArray();
//                                for (int j = 0; j < challengeArray.length(); j++) {
//                                    if (challengeArray.get(j) != challengeArray.get(i)) {
//                                        temp.put(challengeArray.get(j));
//                                    }
//                                }
//                                currentUser.put("challengeAcceptedBy", temp);
//                                Toast.makeText(getBaseContext(), "Challenge Declined!", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    }
//
//                    if (challenge_status) {
//                        currentUser.addUnique("challengeAcceptedBy", challengeId);
//                        Toast.makeText(getBaseContext(), "Challenge Accepted!", Toast.LENGTH_SHORT).show();
//                    }

//                currentUser.saveInBackground();


//            }
//        });

