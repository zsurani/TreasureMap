package com.codepath.snapmap;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.onesignal.OneSignal;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    EditText tvName;
    EditText tvUserName;
    EditText tvBio;
    ImageView ivBackgroundImage;
    EditText tvEmail;
    EditText tvPhone;
    ImageView viewImage;
    Button b;

    // JUST ADDED
    private Bitmap yourbitmap;
    JSONArray bucket = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ParseUser user = ParseUser.getCurrentUser();

        if (user.get("following") == null){
            user.put("following", new ArrayList<String>());
        }

        if (user.get("followedBy") == null){
            user.put("followedBy", new ArrayList<String>());
        }

        tvName = (EditText) findViewById(R.id.tvName);
        viewImage = (ImageView) findViewById(R.id.ivProfile);
        tvUserName = (EditText) findViewById(R.id.tvUserName);
        tvBio = (EditText) findViewById(R.id.tvBio);
        tvEmail = (EditText) findViewById(R.id.tvEmail);
        tvPhone = (EditText) findViewById(R.id.tvPhone);
        ivBackgroundImage = (ImageView)findViewById(R.id.ivBackgroundImage);

        if (getIntent() != null){
            int code = getIntent().getExtras().getInt("code");

            if (code == 50)
            {
//                String profPic = getIntent().getParcelableExtra("prof_pic");
                String email = getIntent().getStringExtra("email");
                String name = getIntent().getStringExtra("name");

                tvName.setText(name);
                tvEmail.setText(email);

            }

            else if (code == 20) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String name = currentUser.getString("name");
                tvName.setText(name);

                String username = currentUser.getString("username");
                tvUserName.setText(username);

                String bio = currentUser.getString("bio");
                tvBio.setText(bio);

                String email = currentUser.getString("email");
                tvEmail.setText(email);

                String phone = currentUser.getString("phone");
                tvPhone.setText(phone);

                ParseFile file = currentUser.getParseFile("imageupload");
                Picasso.with(this).load(file.getUrl()).into(viewImage);
                Picasso.with(this).load(file.getUrl()).into(ivBackgroundImage);


                BitmapDrawable drawable = (BitmapDrawable) ivBackgroundImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Bitmap blurred = blurRenderScript(bitmap, 25);//second parametre is radius
                ivBackgroundImage.setImageBitmap(blurred);

            }
        }

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        
        if (user.get("following") == null){
            user.put("following", new ArrayList<String>());
        }

        if (user.get("followedBy") == null){
            user.put("followedBy", new ArrayList<String>());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    viewImage.setImageBitmap(bitmap);

                    // try once here
                    yourbitmap = bitmap;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    yourbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    ParseFile file1 = new ParseFile("androidbegin.png", image);
                    file1.saveInBackground();
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("imageupload", file1);
                    user.saveInBackground();

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";

                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {

                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);

                        // try once here


                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                yourbitmap = thumbnail;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                yourbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                ParseFile file1 = new ParseFile("androidbegin.png", image);
                file1.saveInBackground();
                ParseUser user = ParseUser.getCurrentUser();
                user.put("imageupload", file1);
                user.saveInBackground();

                // Log.w("path of image from gallery......******************.........", picturePath + "");
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }

    public void onSave(View view) {

        // remember to surround an if-statement for existing users
        final ParseUser user = ParseUser.getCurrentUser();

        user.put("name", tvName.getText().toString());
        user.put("username", tvUserName.getText().toString());
        user.put("bio", tvBio.getText().toString());
        user.put("fav", "");
        user.put("email", tvEmail.getText().toString());
        user.put("phone", tvPhone.getText().toString());
        user.put("bucket", bucket);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                user.put("push_id",userId);
            }
        });
        user.saveInBackground();

        // add picture later on

        Intent i = new Intent(EditProfileActivity.this, FinalProfileActivity.class);
        startActivity(i);
    }

    public void onCancel(View view) {

        // Intent i = new Intent(EditProfileActivity.this, FinalProfileActivity.class);
        finish();
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