package com.codepath.snapmap;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class FilterActivity extends MainActivity {

    EditText etUsername;
    EditText etLocation;
//    Spinner spinCategory;
    TextView title;
    RadioGroup radioCategory;
    String category;
    Switch switchTime;
    Switch switchType;
    private int REQUEST_CODE = 50;

    Button nature;
    Button entertainment;
    Button landmark;
    Button misc;
    Button food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        nature = (Button) findViewById(R.id.button5);
        entertainment = (Button) findViewById(R.id.button13);
        landmark = (Button) findViewById(R.id.button12);
        misc = (Button) findViewById(R.id.button14);
        food = (Button) findViewById(R.id.button11);


        title = (TextView) findViewById(R.id.tvSearch);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/AlexBrush-Regular.ttf");
        title.setTypeface(font);

        etUsername =  (EditText) findViewById(R.id.etUsername);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FilterActivity.this, PlacesAutoCompleteActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });


//        radioCategory = (RadioGroup) findViewById(R.id.radioGroup);
        switchTime = (Switch)findViewById(R.id.switchTime);
        switchType = (Switch)findViewById(R.id.switchType);

        nature.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nature.setPressed(true);
                entertainment.setPressed(false);
                landmark.setPressed(false);
                misc.setPressed(false);
                food.setPressed(false);
                category = "nature";
                return true;
            }
        });

        entertainment.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                entertainment.setPressed(true);
                nature.setPressed(false);
                landmark.setPressed(false);
                misc.setPressed(false);
                food.setPressed(false);
                category = "entertainment";
                return true;
            }
        });

        landmark.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                landmark.setPressed(true);
                entertainment.setPressed(false);
                nature.setPressed(false);
                misc.setPressed(false);
                food.setPressed(false);
                category = "landmark";
                return true;
            }
        });

        misc.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                misc.setPressed(true);
                entertainment.setPressed(false);
                landmark.setPressed(false);
                nature.setPressed(false);
                food.setPressed(false);
                category = "misc";
                return true;
            }
        });

        food.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                food.setPressed(true);
                entertainment.setPressed(false);
                landmark.setPressed(false);
                misc.setPressed(false);
                nature.setPressed(false);
                category = "food";
                return true;
            }
        });

    }

//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.nature:
//                if (checked)
//                  category = "nature";
//                    break;
//
//            case R.id.food:
//                if (checked)
//                    category = "food";
//                break;
//
//            case R.id.landmark:
//                if (checked)
//                    category = "landmark";
//                    break;
//
//            case R.id.entertainment:
//                if (checked)
//                    category = "entertainment";
//                    break;
//
//            case R.id.misc:
//                if (checked)
//                    category = "misc";
//                    break;
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String location = data.getExtras().getString("location");
        if (location != null){
            etLocation.setText(location);
        }
    }



    public void onFilter(View view) {
        Intent intent = new Intent(this,FilteredTimelineActivity.class);

        if(!etUsername.getText().toString().equals("")) {
            intent.putExtra("username",etUsername.getText().toString());

        }


        if(!etLocation.getText().toString().equals("")) {
            intent.putExtra("location",etLocation.getText().toString());

        }

//        if(!spinCategory.getSelectedItem().toString().equals("")) {
//            intent.putExtra("category",spinCategory.getSelectedItem().toString());
//        }

//        Log.d("CATEGORY",category);

        if (category !=null) {

//            Toast.makeText(FilterActivity.this, category, Toast.LENGTH_SHORT).show();

            intent.putExtra("category", category);
        }

        intent.putExtra("code", 100);

        if(switchTime.isChecked()) {
            intent.putExtra("time","top");
        }
        else {
            intent.putExtra("time","recent");
        }

        if(switchType.isChecked()) {
            intent.putExtra("type","following");
        }
        else {
            intent.putExtra("type","all");
        }

        startActivity(intent);
    }

    public void onNature(View view) {
        category = "nature";
    }

    public void onFood(View view) {
        category = "food";
    }

    public void onLandmark(View view) {
        category = "landmark";

    }

    public void onEntertain(View view) {
        category = "entertainment";

    }

    public void onMisc(View view) {
        category = "misc";

    }
}
