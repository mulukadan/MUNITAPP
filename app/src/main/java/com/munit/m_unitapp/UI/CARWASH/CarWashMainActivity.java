package com.munit.m_unitapp.UI.CARWASH;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.Gson;
import com.munit.m_unitapp.DB.Firestore;
import com.munit.m_unitapp.MODELS.CarwashRec;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.UI.CYBER.AddSales;
import com.munit.m_unitapp.UI.CYBER.CashInActivity;
import com.munit.m_unitapp.UI.CYBER.SummaryActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CarWashMainActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private Calendar calendar;
    private int year, month, day;
    String todate;
    String DateDisplaying;
    private TextView motorbksBtn, carsBtn, trucksBtn;
    User userdb;
    private Dialog newCustomerDialog;
    private RecyclerView activityRV;
    private Spinner attendantSpner;
    private ArrayAdapter<String> attendantSpnerAdapter;
    List<String> attentantsSpinnerArray = new ArrayList<>();

    private TextView usernameTV, daysDate;
    private ImageView CloseDialog, dateIcon;
    private RadioButton rBike, rCar, rTruck, rOther;
    private EditText toPayAmtET, regNoET, serviceDescET;
    private Button SaveBtn;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    FloatingActionMenu fab;
    FloatingActionButton addSaleBtn;
    String USERID;
    Gson gson;
    SweetAlertDialog pDialog;
    FirebaseFirestore firedb;
    Button summaryBtn;
    final int MOTORBIKES_DATA = 1;
    final int CARS_DATA = 2;
    final int TRUCKS_DATA = 3;

    int showingDataFor = MOTORBIKES_DATA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash_main);

        getSupportActionBar().hide();

        gson = new Gson();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firedb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firedb.setFirestoreSettings(settings);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Fetching Data....");
        pDialog.setCancelable(false);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;

        activityRV = findViewById(R.id.activityRV);
        activityRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityRV.smoothScrollToPosition(0);
//  motorbksBtn, carsBtn, trucksBtn;
        carsBtn = findViewById(R.id.carsBtn);
        trucksBtn = findViewById(R.id.trucksBtn);
        motorbksBtn = findViewById(R.id.motorbksBtn);

        motorbksBtn.setBackgroundResource(R.color.colorPrimary);
        carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        motorbksBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(MOTORBIKES_DATA);
            carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            motorbksBtn.setBackgroundResource(R.color.colorPrimary);
        });
        carsBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(CARS_DATA);
            carsBtn.setBackgroundResource(R.color.colorPrimary);
            trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            motorbksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        trucksBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(TRUCKS_DATA);
            carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            trucksBtn.setBackgroundResource(R.color.colorPrimary);
            motorbksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });
        summaryBtn = findViewById(R.id.summaryBtn);
        summaryBtn.setOnClickListener((view) -> {
//            Intent intent = new Intent(CashInActivity.this, SummaryActivity.class);
//            startActivity(intent);
        });
        fab = findViewById(R.id.fab);
        addSaleBtn = findViewById(R.id.addSaleBtn);
        addSaleBtn.setOnClickListener(v -> {
            fab.close(true);
            Intent addSales = new Intent(this, AddSales.class);
            Gson gson = new Gson();
//            String userJson = gson.toJson(userdb);
//            addSales.putExtra("userJson", userJson);
//            startActivity(addSales);

            newCustomerDialog.show();
        });
        fetchUsers();
        newCustomerDialog = new Dialog(this);
        newCustomerDialog.setContentView(R.layout.new_car_dialog);
        newCustomerDialog.setCanceledOnTouchOutside(false);
        newCustomerDialog.setCancelable(false);
        newCustomerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rBike = newCustomerDialog.findViewById(R.id.rBike);
        rCar = newCustomerDialog.findViewById(R.id.rCar);
        rTruck = newCustomerDialog.findViewById(R.id.rTruck);
        rOther = newCustomerDialog.findViewById(R.id.rOther);
        toPayAmtET = newCustomerDialog.findViewById(R.id.toPayAmtET);
        regNoET = newCustomerDialog.findViewById(R.id.regNoET);
        serviceDescET = newCustomerDialog.findViewById(R.id.serviceDescET);
        dateIcon = newCustomerDialog.findViewById(R.id.dateIcon);
        daysDate = newCustomerDialog.findViewById(R.id.daysDate);
        daysDate.setText(DateDisplaying);
        usernameTV = newCustomerDialog.findViewById(R.id.usernameTV);
        attendantSpner = newCustomerDialog.findViewById(R.id.attendantSpner);

        attentantsSpinnerArray.add("Select");
        attentantsSpinnerArray.add("Kasyoka");
        attentantsSpinnerArray.add("Ndunda");
        attentantsSpinnerArray.add("Other");


        attendantSpnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, attentantsSpinnerArray);

        attendantSpnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendantSpner.setAdapter(attendantSpnerAdapter);

        dateIcon.setOnClickListener(v -> {
            setDate();
        });
        CloseDialog = newCustomerDialog.findViewById(R.id.CloseAdvDialog);
        CloseDialog.setOnClickListener(v -> {
            newCustomerDialog.dismiss();
        });
        SaveBtn = newCustomerDialog.findViewById(R.id.SaveBtn);

        SaveBtn.setOnClickListener(v -> {
            try {
                int error = 0;
                if (regNoET.getText().toString().length() < 2) {
                    regNoET.setError("Invalid");
                    regNoET.requestFocus();
                    error = 1;
                } else {
                    regNoET.setText(regNoET.getText().toString().replace(" ", "").toUpperCase());
                }
                String amtStr = toPayAmtET.getText().toString().trim();
                if (amtStr.length() < 2) {
                    toPayAmtET.setError("Invalid");
                    toPayAmtET.requestFocus();
                    error = 1;
                } else {
                    try {
                        int amount = Integer.parseInt(amtStr);
                    } catch (Exception e) {
                        toPayAmtET.setError("Invalid");
                        toPayAmtET.requestFocus();
                        error = 1;
                    }
                }

                if (attendantSpner.getSelectedItemPosition() < 1) {
                    error = 1;
                    attendantSpner.requestFocus();
                    Toast.makeText(getApplicationContext(), "Select Attentant", Toast.LENGTH_SHORT).show();
                }

                if (error == 0) {
                    saveCarWashRec();
                }
            } catch (Exception e) {
                toPayAmtET.setError("Invalid");
                toPayAmtET.requestFocus();
            }

        });

    }

    public void saveCarWashRec() {
        CarwashRec carwashRec = new CarwashRec();
        carwashRec.setRecordedBy(userdb.getName());
        carwashRec.setDate(daysDate.getText().toString());
        carwashRec.setRecordedTimeNDate(getDateNTime());
        carwashRec.setRegNo(regNoET.getText().toString().toUpperCase());
        carwashRec.setAttendant(attendantSpner.getSelectedItem().toString());
        carwashRec.setAmount(Integer.parseInt(toPayAmtET.getText().toString().trim()));
        carwashRec.setDesc(serviceDescET.getText().toString().trim());
        carwashRec.setPaid(false);

        String vehicleType = "Other";

        if (rBike.isChecked()) {
            vehicleType = "Motorbike";
        } else if (rCar.isChecked()) {
            vehicleType = "Car";
        } else if (rTruck.isChecked()) {
            vehicleType = "Truck";
        } else {

        }
        carwashRec.setVehicleType(vehicleType);

        new Firestore(CarWashMainActivity.this).addCarWashRec(carwashRec);

        newCustomerDialog.dismiss();
    }

    private void displayRecords(int dataFor) {
        showingDataFor = dataFor;
//        fetchUserSales(USERID);
        pDialog.dismiss();
    }


    @SuppressWarnings("deprecation")
    public void setDate() {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    myDateListener, year, month - 1, day);

            datePickerDialog.setCanceledOnTouchOutside(false);
            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    DateDisplaying = arg3 + "/" + (arg2 + 1) + "/" + arg1;
                    daysDate.setText(DateDisplaying);

//                    getDailySale(DateDisplaying, USERID);

                }
            };


    public void fetchUsers() {
        SweetAlertDialog pDialog = new SweetAlertDialog(CarWashMainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("users");
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    User u = postSnapshot.getValue(User.class);
                    if (u.getUsername().equals(user.getEmail())) {
                        userdb = u;
                        USERID = "" + userdb.getId();
                        usernameTV.setText(userdb.getName());
                        break;
                    }

                }
                if (userdb.getLevel() > 2) {

                }
                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(CarWashMainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });
    }

    public String getDateNTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy : HH:mm:ss", Locale.getDefault());
        return df.format(c);
    }
}