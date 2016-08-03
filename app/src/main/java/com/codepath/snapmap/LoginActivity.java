package com.codepath.snapmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    //    ImageView mProfileImage;
    Button mBtnFb;
    TextView title;
    TextView slogan;
    //CHANGED
    Profile mFbProfile;
    ParseUser parseUser;
    Bitmap bitmap;
    String pictureUrl;
    ImageView mProfileImage;
    ImageView ivLoginBackground;

    String name, email;
    String mail;

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.codepath.snapmap",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("MyKeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mBtnFb = (Button) findViewById(R.id.btnLogin);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        ivLoginBackground = (ImageView) findViewById(R.id.ivLoginBackground);
//
        BitmapDrawable drawable = (BitmapDrawable) ivLoginBackground.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap, 20);//second parametre is radius
        ivLoginBackground.setImageBitmap(blurred);

        title = (TextView) findViewById(R.id.tvTitle);
        slogan = (TextView) findViewById(R.id.tvSlogan);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/AlexBrush-Regular.ttf");
        title.setTypeface(font);
        slogan.setTypeface(font);
        //CHANGED

        mFbProfile = Profile.getCurrentProfile();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.codepath.snapmap",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("MyKeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mBtnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logOut();

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {

                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            parseUser = ParseUser.getCurrentUser();
                            getUserDetailsFromFB();
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            getUserDetailsFromParse();
                            Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
//                            Intent i = new Intent(LoginActivity.this, GeofenceTestActivity.class);
                            startActivity(i);
                            
                        }
                    }
                });

            }
        });

        printKeyHash(this);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.d("DEBUG", "suhdude");
            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


    private void saveNewUser() {

        parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);

        //  Returns a 50x50 profile picture
        parseUser.put("prof_pic_url", pictureUrl);
        Log.d("Profile pic", "url: " + pictureUrl);


        Log.d("DEBUG", name);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });
    }


    private void getUserDetailsFromFB() {

        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        try {
                            parseUser =  ParseUser.getCurrentUser();
                            email = response.getJSONObject().getString("email");
//                            mEmailID.setText(email);
                            Log.d("DEBUG", email);
                            name = response.getJSONObject().getString("name");
//                            mUsername.setText(name);

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            pictureUrl = data.getString("url");

                            // new ProfilePhotoAsync(pictureUrl).execute();
                            saveNewUser();

                            Intent i = new Intent(LoginActivity.this, EditProfileActivity.class);
//                            Log.d("DEBUG2", parseUser.get("prof_pic_url").toString());
                            i.putExtra("prof_pic", pictureUrl);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            i.putExtra("code", 50);

                            //CHANGED
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }


    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();

//Fetch profile photo
        try {
            name = parseUser.getUsername();
            email = parseUser.getEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(LoginActivity.this, "Welcome back " + name, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        mProfileImage.setVisibility(View.INVISIBLE);
        //CHANGE
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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


//    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
//        public Bitmap bitmap;
//        String url;
//
//        public ProfilePhotoAsync(String url) {
//            this.url = url;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            // Fetching data from URI and storing in bitmap
////        bitmap = DownloadImageBitmap();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            mProfileImage.setImageBitmap(bitmap);
//            saveNewUser();
//        }
//    }



//    public static Bitmap DownloadImageBitmap(String url) {
//        Bitmap bm = null;
//        try {
//            URL aURL = new URL(url);
//            URLConnection conn = aURL.openConnection();
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            bm = BitmapFactory.decodeStream(bis);
//            bis.close();
//            is.close();
//        } catch (IOException e) {
//            Log.e("IMAGE", "Error getting bitmap", e);
//        }
//        return bm;
//    }


}