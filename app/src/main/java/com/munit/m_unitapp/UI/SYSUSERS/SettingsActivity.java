package com.munit.m_unitapp.UI.SYSUSERS;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.munit.m_unitapp.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageView back_arrow;
    private RelativeLayout UserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) ->{
            finish();
        });

        UserProfile = findViewById(R.id.UserProfile);
        UserProfile.setOnClickListener((view) ->{
            Intent userpr = new Intent(SettingsActivity.this, UserProfileActivity.class);
            startActivity(userpr);
        });

    }
}
