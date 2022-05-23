package com.munit.m_unitapp.UI.CARWASH;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.munit.m_unitapp.R;

public class CarWashHomeActivity extends AppCompatActivity {
    private RelativeLayout dailyW, carwashHist, CWSummary;
    private ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash_home);

        getSupportActionBar().hide();

        dailyW = findViewById(R.id.dailyW);
        carwashHist = findViewById(R.id.carwashHist);
        back_arrow = findViewById(R.id.back_arrow);
        CWSummary = findViewById(R.id.CWSummary);

        dailyW.setOnClickListener(view -> {
            Intent intent = new Intent(this, CarWashMainActivity.class);
            startActivity(intent);
        });
        carwashHist.setOnClickListener(view -> {
            Intent intent = new Intent(this, CarWashHistActivity.class);
            startActivity(intent);
        });
        CWSummary.setOnClickListener(view -> {
            Intent intent = new Intent(this, SummaryActivity.class);
            startActivity(intent);
        });
        back_arrow.setOnClickListener(view -> {
           finish();
        });
    }
}