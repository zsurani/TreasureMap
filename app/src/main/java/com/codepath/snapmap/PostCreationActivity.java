package com.codepath.snapmap;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.snapmap.models.Post;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostCreationActivity extends MainActivity {

    ImageView viewImage;
    private Bitmap yourbitmap;
    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_CODE = 50;
    EditText loc;

    ArrayList<String> challengeList;
    // adding

    private int CHALLENGE_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        String title = getIntent().getStringExtra("title");
        setTitle("Create Post");

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_post_creation, contentFrameLayout);

        nvDrawer.getMenu().getItem(1).setChecked(true);

        viewImage = (ImageView) findViewById(R.id.viewImage);

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        loc = (EditText) findViewById(R.id.tvLocation);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostCreationActivity.this, PlacesAutoCompleteActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostCreationActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] BYTE;
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                viewImage.setImageBitmap(photo);
                yourbitmap = photo;

            } else if (requestCode == 2) {

                Uri selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT < 19) {
                    String selectedImagePath = getPath(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    yourbitmap = bitmap;
                    viewImage.setImageBitmap(bitmap);

                }
                else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                        viewImage.setImageBitmap(image);
                        yourbitmap = image;

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
//                viewImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            } else if (requestCode == 10) {
                challengeList = data.getExtras().getStringArrayList("challenge");
            }
        }

        else if (resultCode == 5) {
            String location = data.getExtras().getString("location");
            loc.setText(location);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(Uri selectedImageUri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(selectedImageUri);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();

        return filePath;
    }

    public void onSubmit(View view) throws IOException, JSONException {

        try {

            Post post = new Post();

            post.initializeLikes();
            post.initializeCoordinates();

            ParseUser user = ParseUser.getCurrentUser();
            Log.d("USER_ID", user.getObjectId());
            post.setOwnerId(user.getObjectId());

            post.setOwner(user);

            TextView desc = (TextView) findViewById(R.id.tvDescription);
            String description = desc.getText().toString();
            if (description.toString().trim().equals("")) {
                post.setDescription("");

            } else {
                post.setDescription(description);
            }


            Spinner spinner = (Spinner) findViewById(R.id.spinPrivacy);
            String privacy = spinner.getSelectedItem().toString();
            post.setPrivacy(privacy);

            Spinner spin = (Spinner) findViewById(R.id.spinCategory);
            String category = spin.getSelectedItem().toString();
            post.setCategory(category);

            EditText tvAction = (EditText) findViewById(R.id.tvAction);
            String action = tvAction.getText().toString();
            if (action.trim().equals("")) {
                tvAction.setError("Action is required!");
            } else {
                post.setAction(action);
            }

            EditText loc = (EditText) findViewById(R.id.tvLocation);
            String location = loc.getText().toString();
            if (location.trim().equals("")) {
                loc.setError("Location is required!");
            } else {
                post.setLocation(location);
            }


            Address address = getLocationCoordinates(location);
            post.setCoordinates(address.getLatitude(), address.getLongitude());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yourbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file = new ParseFile("androidbegin.png", image);
            post.setPhotoFile(file);

            JSONArray challengeArrayy = new JSONArray();

            if (challengeList != null) {
                for (int i = 0; i < challengeList.size(); i++) {
                    challengeArrayy.put(challengeList.get(i));
                }
            }


            post.put("challenge1", challengeArrayy);


            post.save();
            Toast.makeText(getApplicationContext(), "post submitted!", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, TimelineActivity.class);


            startActivity(i);
        }
        catch (NullPointerException n){
            Toast.makeText(this, "Please fill in missing info...", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Address getLocationCoordinates(String location) throws IOException {
        Geocoder geocoder = new Geocoder(this);

        List<Address> addresses = geocoder.getFromLocationName(location, 1);

        if(!addresses.isEmpty()) {
            return addresses.get(0);
        }
        return null;
    }

    public void onChallenge(View view) {
        Intent intent = new Intent(PostCreationActivity.this, ChallengeGeneration.class);
        startActivityForResult(intent, CHALLENGE_CODE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (yourbitmap != null){
//
//        }
    }
}