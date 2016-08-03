package com.codepath.snapmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.codepath.snapmap.models.Post;

import java.util.ArrayList;

public class ChallengeGeneration extends AppCompatActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    ArrayList<String> challengeList = new ArrayList<>();
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_generation);

        lvItems = (ListView)findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        // trying to retrieve post so that an update can happen

//        String location = getIntent().getStringExtra("location");
//        String category = getIntent().getStringExtra("category");
//        String description = getIntent().getStringExtra("description");



//        ParseQuery<Post> query = new ParseQuery<Post>("Post");
//        //filter by recency
//
//        if(location != null) {
//            query.whereEqualTo("location",location);
//        }
//
//        if(category != null) {
//            query.whereEqualTo("category",category);
//        }
//
//        if(description != null) {
//            query.whereEqualTo("description",description);
//        }

//        query.findInBackground(new FindCallback<Post>() {
//            @Override
//            public void done(List<Post> objects, ParseException e) {
//                for (Post p: objects) {
//
//                    post = new Post();
//                    post.setCategory(p.getCategory());
//                    post.setLocation(p.getLocation());
//                    post.setPrivacy(p.getPrivacy());
//                    post.setDescription(p.getDescription());
//                    post.setOwner(p.getOwner());
//                    post.setCreationDate(p.getCreatedAt());
//                    post.setPhotoFile(p.getPhotoFile());
//                    post.setId(p.getId());
////                    post.setChallenge(p.getChallenge());
//                    post.put("challenge1", p.getJSONArray("challenge1"));
//
//                }
//
//// add something about challenges
//                //update adapter/ListView
//            }
//        });
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        challengeList.add(itemText);
        itemsAdapter.add(itemText);
        etNewItem.setText("");
    }

    public void onReturn(View view) {

//        post.put("hello", "hi");
//        Intent data = new Intent(this, TimelineActivity.class);
//        JSONArray challengeArray = new JSONArray();
//        for (int i=0; i<challengeList.size()-1; i++) {
//            challengeArray.put(challengeList.get(i));
//        }
//        post.put("challenge1", challengeArray);
//        startActivity(data);

        Intent data = new Intent(this, PostCreationActivity.class);
        data.putStringArrayListExtra("challenge", challengeList);
        setResult(RESULT_OK, data);
        finish();

    }
}
