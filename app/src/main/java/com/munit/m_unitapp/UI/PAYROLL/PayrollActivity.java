package com.munit.m_unitapp.UI.PAYROLL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.munit.m_unitapp.R;
import com.munit.m_unitapp.UI.PAYROLL.EmployeesListActivity;

public class PayrollActivity extends AppCompatActivity {
private RelativeLayout employeesCV;
private ImageView back_arrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll);
        getSupportActionBar().hide();

        back_arrow = findViewById(R.id.back_arrow);
        employeesCV = findViewById(R.id.employeesCV);

        back_arrow.setOnClickListener(v -> {
            finish();
        });

        employeesCV.setOnClickListener(v -> {
            Intent  intent = new Intent(getApplicationContext(), EmployeesListActivity.class);
            startActivity(intent);
        });


    }
}