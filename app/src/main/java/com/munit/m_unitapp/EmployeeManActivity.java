package com.munit.m_unitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.MODELS.Employee;
import com.munit.m_unitapp.MODELS.User;
import com.squareup.picasso.Picasso;

public class EmployeeManActivity extends AppCompatActivity {

    private ImageView back_arrow;
    private TextView nameTV, titleTV, GenderTV, ageTV, emplyDateTV, salaryTV, emplymentStatusTV;
    private CircularImageView ProfilePic;

    Bundle extras;
    private Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_man);
        getSupportActionBar().hide();

        back_arrow = findViewById(R.id.back_arrow);
        nameTV = findViewById(R.id.nameTV);
        titleTV = findViewById(R.id.titleTV);
        GenderTV = findViewById(R.id.GenderTV);
        ageTV = findViewById(R.id.ageTV);
        emplyDateTV = findViewById(R.id.emplyDateTV);
        salaryTV = findViewById(R.id.salaryTV);
        ProfilePic = findViewById(R.id.ProfilePic);
        emplymentStatusTV = findViewById(R.id.emplymentStatusTV);

        Gson gson = new Gson();
        extras = getIntent().getExtras();
        if (extras != null) {
            employee = gson.fromJson(getIntent().getStringExtra("employeeJson"), Employee.class);
        } else {

        }

        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        updateUI();

    }

    public void updateUI() {
        if (employee.getImgUrl().length() > 2) {
            Picasso.get().load(employee.getImgUrl()).into(ProfilePic);
        }

        nameTV.setText(employee.getName());
        titleTV.setText(employee.getJobDescription() + "\n" + employee.getDepartment());
        GenderTV.setText(employee.getGender());
        ageTV.setText(employee.getEmploymentDate());
        emplyDateTV.setText(employee.getEmploymentDate());
        salaryTV.setText("Ksh. " + employee.getSalary());
        emplymentStatusTV.setText("Employment Status: " + employee.getActive().toString());

    }
}