package com.codepath.snapmap;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.snapmap.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;

public class DetailBucketActivity extends AppCompatActivity {

    String category, location, postid, screenname;
    ParseUser u;


//    private ListView lvBucket;
//    private ArrayList<Post> posts;
//    private BucketArrayAdapter adapter;

    TextView tvLocation;
    Button uberButton;
    double longitude;
    double latitude;
    ParseUser user = ParseUser.getCurrentUser();
    String name = user.getString("name");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bucket);

        location = getIntent().getStringExtra("location");
        postid = getIntent().getStringExtra("postid");

//        lvBucket = (ListView) findViewById(R.id.lvBucket);
//        posts = new ArrayList<Post>();
//
//        adapter = new BucketArrayAdapter(DetailBucketActivity.this, posts);
//        lvBucket.setAdapter(adapter);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocation.setText(location);
        try {
            Address address = getLocationCoordinates(location);
            longitude = address.getLongitude();
            latitude = address.getLatitude();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onDirect(View view) {

        Intent intent = new Intent(DetailBucketActivity.this, DirectionsMap.class);

        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("postid", postid);
        startActivity(intent);
    }

    public Address getLocationCoordinates(String location) throws IOException {
        Geocoder geocoder = new Geocoder(this);

        List<Address> addresses = geocoder.getFromLocationName(location, 1);

        if(!addresses.isEmpty()) {
            return addresses.get(0);
        }
        return null;
    }

    public void onOriginal(View view) {

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
                        Post post = new Post();
                        post.setCategory(p.getCategory());
                        post.setLocation(p.getLocation());
                        post.setPrivacy(p.getPrivacy());
                        post.setDescription(p.getDescription());
                        post.setOwner(p.getOwner());
                        post.setCreationDate(p.getCreatedAt());
                        post.setPhotoFile(p.getPhotoFile());
                        post.setId(p.getId());


                        category = p.getCategory();
                        u = p.getOwner();
                        try {
                            screenname = u.fetchIfNeeded().getString("username");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        location = p.getLocation();

                    }

                }

                Intent intent = new Intent(getApplicationContext(), FilteredTimelineActivity.class);
                intent.putExtra("location",location);
                intent.putExtra("category",category);
                intent.putExtra("code", 50);
                intent.putExtra("postid", postid);
                intent.putExtra("username", screenname);
                intent.putExtra("time", "recent");



                startActivity(intent);
            }
        });



    }

    public void onMessage(View view) {

//        Intent intent = new Intent(DetailBucketActivity.this, MessageActivity.class);
//        startActivity(intent);

        Uri uri = Uri.parse("smsto:");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);

        it.putExtra("sms_body", name + " would like you to join in an adventure to " + location);
        startActivity(it);
    }
}
