package com.codepath.snapmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    protected DrawerLayout mDrawer;
    protected Toolbar toolbar;
    protected NavigationView nvDrawer;
    protected ActionBarDrawerToggle drawerToggle;
    MenuItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_closed);
        mDrawer.setDrawerListener(drawerToggle);

        setupDrawerContent(nvDrawer);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        onOptionsItemSelected(menuItem);
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.timeline:
                item.setChecked(true);
//                setTitle(item.getTitle());
                mItem = item;
                Intent intent2 = new Intent(getApplicationContext(),TimelineActivity.class);
                startActivity(intent2);
                mDrawer.closeDrawers();
                break;
            case R.id.createPost:
                item.setChecked(true);
//                setTitle(item.getTitle());
                mItem = item;
                Intent intent = new Intent(getApplicationContext(),PostCreationActivity.class);
                startActivity(intent);
                mDrawer.closeDrawers();
                break;
            case R.id.profile:
                item.setChecked(true);
                mItem = item;
                Intent intent3 = new Intent(getApplicationContext(),FinalProfileActivity.class);
                startActivity(intent3);
                mDrawer.closeDrawers();
                break;
            case R.id.bucketList:
                item.setChecked(true);
//                setTitle(item.getTitle());
                mItem = item;
                Intent intent4 = new Intent(getApplicationContext(),BucketActivity.class);
                startActivity(intent4);
                mDrawer.closeDrawers();
                break;
            case R.id.challengeList:
                item.setChecked(true);
                mItem = item;
                Intent intent5 = new Intent(getApplicationContext(),ChallengeActivity.class);
                intent5.putExtra("title",item.getTitle());
                startActivity(intent5);
                mDrawer.closeDrawers();
                break;
        }
        return false;
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    public static void createNotification(String pushId, String content) {
        JSONObject notification = new JSONObject();
        JSONObject text = new JSONObject();
        JSONObject heading = new JSONObject();
        JSONArray pushIds = new JSONArray();

        //Creates JsonObjects/Arrays to be included in notification
        try {
            text.put("en",content);
            heading.put("en","Treasure Map");
            pushIds.put(pushId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Creates notification json object to be pushed
        try {
            notification.put("app_id","eaaeda24-41d5-40a8-94d4-ec97194a478a");
            notification.put("contents",text);
            notification.put("headings",heading);
            notification.put("include_player_ids",pushIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //pushes notification
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.NONE);
        OneSignal.postNotification(notification, null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mItem != null){
            mItem.setChecked(false);
        }
    }
}
