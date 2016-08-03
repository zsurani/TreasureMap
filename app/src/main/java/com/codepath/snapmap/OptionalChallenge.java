package com.codepath.snapmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class OptionalChallenge extends AppCompatActivity {

    String location, category, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional_challenge);

        location = getIntent().getStringExtra("location");
        category = getIntent().getStringExtra("category");
        description = getIntent().getStringExtra("description");
    }

    public void onSkip(View view) {
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }


    public void onChallenge(View view) {

        Intent intent = new Intent(this, ChallengeGeneration.class);
        intent.putExtra("location", location);
        intent.putExtra("category", category);
        intent.putExtra("description", description);
        startActivity(intent);
    }
}
