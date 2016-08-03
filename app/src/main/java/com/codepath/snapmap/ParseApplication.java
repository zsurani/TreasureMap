package com.codepath.snapmap;

import android.app.Application;
import android.content.Context;

import com.codepath.snapmap.models.Post;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;


public class ParseApplication extends Application {

    public static final String YOUR_APPLICATION_ID = "kd8372nxmzms2k2827dhdj";
    public static final String YOUR_CLIENT_KEY = "82jsmzla01928sjajshdgf";

    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
        OneSignal.startInit(this).init();
//        OneSignal.enableInAppAlertNotification(true);

        CONTEXT = this;

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(YOUR_APPLICATION_ID) // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://infinite-atoll-68457.herokuapp.com/parse/").build());

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        ParseFacebookUtils.initialize(this);

        ParseObject.registerSubclass(Post.class);

//        // Test creation of object
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();


        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("EXRVG7eSZgA1WnP7fp6xbhTW6x1E7B9m") //This is necessary
                .setRedirectUri("YOUR_REDIRECT_URI") //This is necessary if you'll be using implicit grant
                .setEnvironment(SessionConfiguration.Environment.SANDBOX) //Useful for testing your app in the sandbox environment
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS)) //Your scopes for authentication here
                .build();

//This is a convenience method and will set the default config to be used in other components without passing it directly.
        UberSdk.initialize(config);


    }
}
