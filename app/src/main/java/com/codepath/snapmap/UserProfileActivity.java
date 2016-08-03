package com.codepath.snapmap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.snapmap.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends MainActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    TextView tvName;
    ImageView ivProfile;
    TextView tvUserName;
    TextView tvBio;
    TextView tvFollowerCount;
    TextView tvFollowingCount;
    TextView tvPostCount;

    Button btnFollow;
    ParseUser currentUser;
    ArrayList<Post> posts;
    SupportMapFragment mapFragment;
    JSONArray array = new JSONArray();

    private HashMap<String, Post> markers = new HashMap<String, Post>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String title = getIntent().getStringExtra("title");
        setTitle(title);

        tvName = (TextView) findViewById(R.id.tvName);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBio = (TextView) findViewById(R.id.tvBio);
        btnFollow = (Button) findViewById(R.id.btnFollow);
        tvFollowerCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        tvPostCount = (TextView) findViewById(R.id.tvPostCount);

        btnFollow.setText("Follow");

        final String id = getIntent().getStringExtra("userid");

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean followed = true;
                ParseUser myUser = ParseUser.getCurrentUser();
                JSONArray array = myUser.getJSONArray("following");
                String myId = myUser.getObjectId();

                for (int i = 0; i < array.length(); i++) {
                    try {
                        if (array.get(i).equals(currentUser.getObjectId())) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                array.remove(i);
                                followed = false;
                                //TODO: remove like from builds lower than 19
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (followed) {
                    myUser.addUnique("following", id);
                    Toast.makeText(getApplicationContext(), currentUser.getUsername() + " followed!", Toast.LENGTH_SHORT).show();
                    btnFollow.setText("Unfollow");
                } else {
                    ArrayList<String> newArray = new ArrayList<String>();
                    newArray.add(myId);

                    ArrayList<String> newArray2 = new ArrayList<String>();
                    newArray2.add(id);
                    myUser.removeAll("following", newArray2);

                    Toast.makeText(getApplicationContext(), currentUser.getUsername() + " unfollowed...", Toast.LENGTH_SHORT).show();
                    btnFollow.setText("Follow");

                }

                myUser.saveInBackground();
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(UserProfileActivity.this, "failure", Toast.LENGTH_SHORT).show();
                }
                for (ParseObject u : objects) {

                    currentUser = (ParseUser) u;

                    String name = u.getString("name");
                    tvName.setText(name);

                    String userName = u.getString("username");
                    tvUserName.setText(userName);

                    String bio = u.getString("bio");
                    tvBio.setText(bio);

                    int followingCount = u.getJSONArray("following").length();
                    tvFollowingCount.setText(Integer.toString(followingCount));

                    ParseFile file = u.getParseFile("imageupload"); // either convert// to parsefile or...is it a parsefile?
                    Picasso.with(getBaseContext()).load(file.getUrl()).into(ivProfile);

                }

                if (array.length() == 0) {
                    btnFollow.setText("Follow");
                } else { // good until

                    boolean found = false;
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            if (array.get(i).equals(currentUser.getObjectId())) {
                                found = true;
                                break;
                            }
                        } catch (JSONException f) {
                            f.printStackTrace();
                        }
                    }

                    if (found) {
                        btnFollow.setText("Unfollow");
                    } else {
                        btnFollow.setText("Follow");
                    }
                }
            }
        });

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("_User");
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(UserProfileActivity.this, "failure", Toast.LENGTH_SHORT).show();
                }

                boolean following = false;
                int followerCount = 0;
                for (ParseObject u : objects) {

                    JSONArray array = u.getJSONArray("following");

                    ParseUser user = (ParseUser) u;

                    if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())){
                        following = true;
                    }

                    for (int i = 0; i < array.length(); i++) {
                        try {
                            if (array.get(i).equals(id)) {
                                followerCount += 1;
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }


                    tvFollowerCount.setText(Integer.toString(followerCount));
                }

                if (following) {
                    btnFollow.setText("Unfollow");
                } else {
                    btnFollow.setText("Follow");
                }

            }
        });

//        lvPosts = (ListView) findViewById(R.id.lvRecent);
        posts = new ArrayList<Post>();

        ParseQuery<Post> newQuery = new ParseQuery<Post>("Post");
        Log.d("USER_ID_PROFILE", id);
        newQuery.whereEqualTo("parentId", id);

        newQuery.orderByDescending("createdAt");

        newQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {

                int postCount = 0;

                if (e != null) {
                    Toast.makeText(UserProfileActivity.this, "error: " + e, Toast.LENGTH_SHORT).show();
                }
//                int x = 0;

                for (Post p : objects) {
                    postCount += 1;
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
                    tvPostCount.setText(Integer.toString(postCount));
                }

//                adapter = new RecentArrayAdapter(UserProfileActivity.this, posts);
//                lvPosts.setAdapter(adapter);

                mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
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
    }

    private void loadMap(GoogleMap map) throws JSONException {
        if (map != null) {
            Log.d("POST_SIZE", String.valueOf(posts.size()));
            for (int i = 0; i < posts.size(); i++) {
                addPostMarker(map, posts.get(i));
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Post post = (Post) markers.get(marker.getId());

                    Intent intent = new Intent(getApplicationContext(), FilteredTimelineActivity.class);
                    try {
                        intent.putExtra("username", post.getOwner().fetchIfNeeded().getUsername());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("location", post.getLocation());
                    intent.putExtra("category", post.getCategory());
                    intent.putExtra("type", "all");
                    intent.putExtra("time", "recent");
                    intent.putExtra("code", 100);

                    startActivity(intent);
                    return true;
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }

    public void addPostMarker(GoogleMap map, Post post) {
        boolean hasKey = post.containsKey("coordinates");
        Log.d("hasKey", String.valueOf(hasKey));
        if (hasKey) {
            JSONArray coordinates = post.getCoordinates();

            double latitude = 0;
            double longitude = 0;
            try {
                latitude = post.getCoordinates().getDouble(0);
                Log.d("LATITUDE", String.valueOf(latitude));
                longitude = post.getCoordinates().getDouble(1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.treasure_chest)).title(post.getLocation());
            markers.put(map.addMarker(markerOptions).getId(), post);
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

    @SuppressLint("NewApi")
    public Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(this);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    public Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}