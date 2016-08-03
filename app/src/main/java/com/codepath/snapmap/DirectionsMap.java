package com.codepath.snapmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.snapmap.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DirectionsMap extends MainActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    TextView tvName;
    ImageView ivProfile;
    TextView tvUserName;
    TextView tvBio;
    TextView tvFav;
    Button btnFollow;
    ParseUser currentUser;
    ArrayList<Post> posts;
    SupportMapFragment mapFragment;

//    RecentArrayAdapter adapter;
//    ListView lvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_map);

//        tvName = (TextView) findViewById(R.id.tvName);
//        ivProfile = (ImageView) findViewById(R.id.ivProfile);
//        tvUserName = (TextView) findViewById(R.id.tvUserName);
//        tvBio = (TextView) findViewById(R.id.tvBio);
//        tvFav = (TextView) findViewById(R.id.tvFav);
//        btnFollow = (Button) findViewById(R.id.btnFollow);

        final String id = getIntent().getStringExtra("postid");

        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.whereEqualTo("objectId",id);
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                }
                for (Post p: objects) {

                    Post post = new Post();
                    post.setCategory(p.getCategory());
                    post.setLocation(p.getLocation());
                    post.setPrivacy(p.getPrivacy());
                    post.setDescription(p.getDescription());
                    post.setOwner(p.getOwner());
                    post.setCreationDate(p.getCreatedAt());
                    post.setPhotoFile(p.getPhotoFile());
                    post.setId(p.getId());
                    try {
                        if (p.containsKey("coordinates")) {
                            post.setCoordinates(p.getCoordinates().getDouble(0), p.getCoordinates().getDouble(1));
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    posts.add(post);
                }

                mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1));
                if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap map) {
                            try {
                                loadMap(map);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        posts = new ArrayList<Post>();

    }

    private void loadMap(GoogleMap map) throws JSONException {
        if (map != null) {
            Log.d("POST_SIZE",String.valueOf(posts.size()));
            for(int i = 0; i<posts.size();i++) {
                addPostMarker(map,posts.get(i));
            }
            map.getUiSettings().setMapToolbarEnabled(true);

        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        map.getUiSettings().setMapToolbarEnabled(true);

    }

    public void addPostMarker(GoogleMap map, Post post) {
        boolean hasKey = post.containsKey("coordinates");
        Log.d("hasKey", String.valueOf(hasKey));
        if(hasKey) {
            JSONArray coordinates = post.getCoordinates();

            double latitude = 0;
            double longitude = 0;
            try {
                latitude = post.getCoordinates().getDouble(0);
                Log.d("LATITUDE",String.valueOf(latitude));
                longitude = post.getCoordinates().getDouble(1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            map.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(post.getLocation()));
            map.getUiSettings().setMapToolbarEnabled(true);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}






