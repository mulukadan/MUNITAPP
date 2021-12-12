package com.munit.m_unitapp.UI.POOL;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.Employee;
import com.munit.m_unitapp.MODELS.PoolTable;
import com.munit.m_unitapp.R;

import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolTableActivity extends AppCompatActivity {
    private ImageView back_arrow, editPLBtn, CloseDialog, calenderIcon;
    private EditText nameEt, costEt;
    private Spinner colorSpiner, locationSpiner;
    private TextView purchaseDate, dateOfPurchaseTV, ageTV, costTV, returnsTV, locationTV, nameTV;
    private Button SaveBtn;
    private CircularImageView ProfilePic;

    firebase db = new firebase();
    private Dialog newPoolDialog;
    PoolTable poolTable = new PoolTable();

    private Calendar calendar;
    private int year, month, day;
    String todate,DateDisplaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_table);

        getSupportActionBar().hide();

        Gson gson = new Gson();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            poolTable = gson.fromJson(getIntent().getStringExtra("poolTableJson"), PoolTable.class);


        } else {
            finish();
        }
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;

        editPLBtn = findViewById(R.id.editPLBtn);
        dateOfPurchaseTV = findViewById(R.id.dateOfPurchaseTV);
        costTV = findViewById(R.id.costTV);
        ageTV = findViewById(R.id.ageTV);
        returnsTV = findViewById(R.id.returnsTV);
        ProfilePic = findViewById(R.id.ProfilePic);
        locationTV = findViewById(R.id.locationTV);
        nameTV = findViewById(R.id.nameTV);

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(v -> finish());

        newPoolDialog = new Dialog(this);
        newPoolDialog.setCanceledOnTouchOutside(false);
        newPoolDialog.setContentView(R.layout.new_pool_dialog);
        newPoolDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseDialog = newPoolDialog.findViewById(R.id.CloseDialog);
        nameEt = newPoolDialog.findViewById(R.id.nameEt);
        costEt = newPoolDialog.findViewById(R.id.costEt);
        colorSpiner = newPoolDialog.findViewById(R.id.colorSpiner);
        locationSpiner = newPoolDialog.findViewById(R.id.locationSpiner);
        purchaseDate = newPoolDialog.findViewById(R.id.purchaseDate);
        calenderIcon = newPoolDialog.findViewById(R.id.calenderIcon);
        SaveBtn = newPoolDialog.findViewById(R.id.SaveBtn);

        CloseDialog.setOnClickListener(v -> {
            newPoolDialog.dismiss();
        });


        calenderIcon.setOnClickListener((view) -> {
            setDate();
        });
        SaveBtn.setOnClickListener(v -> {
            savePool();
        });
        editPLBtn.setOnClickListener(v -> {
            nameEt.setText(poolTable.getName());
            costEt.setText(String.valueOf(poolTable.getCost()));
            colorSpiner.setSelection(((ArrayAdapter) colorSpiner.getAdapter()).getPosition(poolTable.getColor()));
            locationSpiner.setSelection(((ArrayAdapter) locationSpiner.getAdapter()).getPosition(poolTable.getLocation()));
            purchaseDate.setText(poolTable.getDateOfPurchase());
            nameEt.setSelection(nameEt.getText().length());
            newPoolDialog.show();
        });
        updateUi();
    }

    private void updateUi() {
        dateOfPurchaseTV.setText(poolTable.getDateOfPurchase());
        ageTV.setText(poolTable.getAge() + " Years");
        costTV.setText("Ksh. " + String.format("%,.2f", (double) poolTable.getCost()));
        returnsTV.setText("Ksh. " + String.format("%,.2f", (double) poolTable.getReturns()));
        dateOfPurchaseTV.setText(poolTable.getDateOfPurchase());
        locationTV.setText(poolTable.getLocation());
        nameTV.setText(poolTable.getName());

        if (poolTable.getColor().equalsIgnoreCase("blue")) {
            ProfilePic.setBackgroundResource(R.drawable.blue_pool_table);
        } else if (poolTable.getColor().equalsIgnoreCase("red")) {
            ProfilePic.setBackgroundResource(R.drawable.red_pool_table);
        } else {
            ProfilePic.setBackgroundResource(R.drawable.green_pool_table);
        }
    }

    private void savePool() {
        int error = 0;
        String name = nameEt.getText().toString().trim().toUpperCase();
        if (name.length() < 2) {
            nameEt.setError("Enter valid name");
            error = 1;
        }

        String cost = costEt.getText().toString().trim();
        if (name.length() < 2) {
            costEt.setError("Enter valid Cost");
            error = 1;
        }

        String color = colorSpiner.getSelectedItem().toString();
        String location = locationSpiner.getSelectedItem().toString();
        String dateOfP = purchaseDate.getText().toString();

        if (error == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Are you sure you want to update Pool Table Details?")
                    .showCancelButton(true)
                    .setCancelText("Cancel")
                    .setCancelClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                    })
                    .setConfirmText("Yes!")
                    .setConfirmClickListener(sDialog -> {

                        poolTable.setName(name);
                        poolTable.setStatus("Active");
                        poolTable.setLocation(location);
                        poolTable.setCost(Integer.parseInt(cost));
                        poolTable.setColor(color);
                        poolTable.setDateOfPurchase(dateOfP);
                        db.savePoolTable(poolTable);
                        updateUi();
                        newPoolDialog.dismiss();
                        sDialog
                                .setTitleText("Updated!")
                                .setContentText("Done!")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss())
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    })
                    .show();


        }


    }

    public void setDate() {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, calendar.get(Calendar.MONTH), day);
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
                    DateDisplaying = arg3 +"/" + (arg2+1) +"/" + arg1;
                    purchaseDate.setText(DateDisplaying);
                }
            };
}