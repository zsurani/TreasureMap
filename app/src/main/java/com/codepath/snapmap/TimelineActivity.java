package com.codepath.snapmap;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.codepath.snapmap.adapters.RecentArrayAdapter;
import com.codepath.snapmap.geofencing.GeofenceService;
import com.codepath.snapmap.listeners.EndlessScrollListener;
import com.codepath.snapmap.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimelineActivity extends MainActivity {

    ListView lvPosts;
    ArrayList<Post> posts;
    RecentArrayAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    EndlessScrollListener endlessScrollListener;
    GoogleApiClient googleApiClient;
    private static final String TAG = "DEBUG ";
    public ArrayList<String> idList;

    int FILTER_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Timeline");

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ParseUser.getCurrentUser().getJSONArray("bucket") != null || ParseUser.getCurrentUser().getJSONArray("bucket").length() != 0) {
                            Log.d(TAG, "Connected to GoogleApiClient");
                            //                        startLocationMonitoring();

                            JSONArray jsonBucketlist = ParseUser.getCurrentUser().getJSONArray("bucket");

                            String[] bucketList = new String[jsonBucketlist.length()];

                            if (jsonBucketlist != null) {
                                int len = jsonBucketlist.length();
                                for (int i = 0; i < len; i++) {
                                    try {
                                        bucketList[i] = (String) jsonBucketlist.get(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            //perform the query for the results and refresh
                            ParseQuery<Post> query = new ParseQuery<Post>("Post");

                            query.whereContainedIn("objectId", Arrays.asList(bucketList));

                            query.findInBackground(new FindCallback<Post>() {
                                @Override
                                public void done(List<Post> objects, ParseException e) {
                                    ArrayList<ArrayList> bucketArray = new ArrayList<ArrayList>();
                                    ArrayList<String> bucketIds = new ArrayList<String>();
                                    ArrayList<String> bucketNames = new ArrayList<String>();
                                    if (objects != null) {
                                        for (Post p : objects) {
                                            //copy Parse post data into locally created Post objects
                                            if (p.getCoordinates() != null) {
                                                try {
                                                    double Lat = (double) p.getCoordinates().get(0);
                                                    double Long = (double) p.getCoordinates().get(1);
                                                    bucketIds.add((String) p.getObjectId());
                                                    bucketArray.add(new ArrayList<>(Arrays.asList(Lat, Long)));
                                                    bucketNames.add(p.getLocation());
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
//                                        startGeofenceMonitoring(bucketArray, bucketIds, bucketNames);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Suspended connection to GoogleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Failed to connect to GoogleApiClient - " + connectionResult.getErrorMessage());
                    }
                })
                .build();

        googleApiClient.connect();

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_timeline, contentFrameLayout);

        nvDrawer.getMenu().getItem(0).setChecked(true);

        lvPosts = (ListView) findViewById(R.id.lvTime);
        posts = new ArrayList<Post>();

        adapter = new RecentArrayAdapter(TimelineActivity.this, posts);
        lvPosts.setAdapter(adapter);
        retrievePosts(0);



        lvPosts.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                //retrieve more data from according to which page we're on
                retrievePosts(totalItemsCount);
                return true;
            }
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                retrievePosts(0);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline,menu);
        return true;
    }

    public void retrievePosts(int offset){

        ParseQuery<Post> query = new ParseQuery<Post>("Post");
        //get first 5 Posts
        query.setLimit(3);

//        Log.d("offset", Integer.toString(offset));

        //skip retrieval of already displayed posts
        query.setSkip(offset);

        //filter by recency
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<Post>() {

            @Override
            public void done(List<Post> objects, ParseException e) {

                if (objects != null){
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

//                        JSONArray c_array = p.getChallengedBy();
//                        if (c_array != null) {
//                            post.setChallenged(c_array);
//                        }


                        //add each post to List of posts for displaying in ListView
                        posts.add(post);
//                    Log.d("Suh", p.getDescription());
                    }
                    //update adapter/ListView
                    adapter.notifyDataSetChanged();

//                Log.d("DEBUG", objects.toString());
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.clear();
        retrievePosts(0);
    }

    public void onFilter(MenuItem item) {
        Intent intent = new Intent(this,FilterActivity.class);
        startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
    }

    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public void startGeofenceMonitoring(ArrayList<ArrayList> geofenceArrays, ArrayList<String> ids, ArrayList<String> names) {

        Log.d(TAG, "startMonitoring called");
        try {

            ArrayList<Geofence> geofences = new ArrayList<Geofence>();

            int size = geofenceArrays.size();

            for (int i = 0; i < size; i++){

                double Lat = (double) geofenceArrays.get(i).get(0);
                double Long = (double) geofenceArrays.get(i).get(1);
                String id = ids.get(i);
                String name = names.get(i);
                Log.d(TAG, Double.toString(Lat) + ":" + Double.toString(Long));

                Geofence geofence = new Geofence.Builder()
                        .setRequestId(name)
                        .setCircularRegion(Lat, Long, 20000).setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setNotificationResponsiveness(1)
                        .setLoiteringDelay(20)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build();

                geofences.add(geofence);
            }

            idList = names;

            Intent intent = new Intent(this, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET
                    }, 0);
                }
            }

            if (!geofences.isEmpty()){

                GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofences(geofences).build();

                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofenceRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG, "Successfully added geofences");
                                } else {

                                    Log.d(TAG, "Failed to add geofence + " + status.getStatus());
                                }
                            }
                        });
            }


        } catch (SecurityException s) {
            Log.d(TAG, s.getMessage());
        }
    }

    private void stopGeofenceMonitoring(){
        Log.d(TAG, "stopMonitoring called");
        if (!idList.isEmpty()){
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, idList);
        }
    }
}
