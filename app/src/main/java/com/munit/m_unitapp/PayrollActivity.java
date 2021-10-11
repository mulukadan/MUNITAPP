package com.munit.m_unitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class PayrollActivity extends AppCompatActivity {
private RelativeLayout employeesCV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll);
        getSupportActionBar().hide();
        employeesCV = findViewById(R.id.employeesCV);

        employeesCV.setOnClickListener(v -> {
            Intent  intent = new Intent(getApplicationContext(), EmployeesListActivity.class);
            startActivity(intent);
        });


    }
}