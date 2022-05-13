package com.munit.m_unitapp.UI.CARWASH;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.ADAPTERS.CarwashDailyRecsAdapter;
import com.munit.m_unitapp.DB.Firestore;
import com.munit.m_unitapp.MODELS.CarWashDailySummary;
import com.munit.m_unitapp.MODELS.CarwashAttentantTotal;
import com.munit.m_unitapp.MODELS.CarwashRec;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;
import com.munit.m_unitapp.UI.CYBER.AddSales;
import com.munit.m_unitapp.UI.CYBER.CashInActivity;
import com.munit.m_unitapp.UI.CYBER.SummaryActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CarWashMainActivity extends AppCompatActivity implements CarwashDailyRecsAdapter.ClickListener {
    private ImageView back_arrow, CloseCommDialog, ClosePDialog, rightArrow, leftArrow, CloseMRDialog;
    private Calendar calendar;
    private int year, month, day;
    String todate;
    private LinearLayout labourLL, expenseLL;
    String DateDisplaying;
    private TextView motorbksBtn, carsBtn, trucksBtn, othersBtn, mainDaysDate, overrallTotalTV, laborTotalTV, expenseTV, remTotalTV, pdVehicletypeTV, pdRegnoTV, pdAmountTV;
    User userdb;
    private Dialog newCustomerDialog, commissionsDialog, paymentDialog, meterReadingDialog;
    private RecyclerView activityRV;
    private Spinner attendantSpner, expenseSpner;
    private ArrayAdapter<String> attendantSpnerAdapter;
    private ArrayAdapter<String> expenseSpnerAdapter;
    List<String> attentantsSpinnerArray = new ArrayList<>();
    List<String> expenseSpinnerArray = new ArrayList<>();

    private TextView usernameTV, daysDate, TopupTitle, commissionTV, dialogTitle, expenseViewBtn, readingTitleTV;
    private CardView salesTitleCV;
    private ImageView CloseDialog, dateIcon, mainDateIcon;
    private RadioButton rBike, rCar, rTruck, rOther;
    private EditText toPayAmtET, regNoET, serviceDescET, startRET, lastReadingET, meterDescET;
    private Button SaveBtn, pdSaveBtn, SaveRBtn;
    private CarwashDailyRecsAdapter carwashDailyRecsAdapter;
    private LinearLayout expenseTypeLL, attentantLL;
    private RadioGroup radioGroup;
    TextInputLayout regNoETIT;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    FloatingActionMenu fab;
    FloatingActionButton addSaleBtn, addExpenseBtn, addWaterReadingBtn, addTokenReadingBtn;
    String USERID;
    Gson gson;
    SweetAlertDialog pDialog;
    FirebaseFirestore firedb;
    final int MOTORBIKES_DATA = 1;
    final int CARS_DATA = 2;
    final int TRUCKS_DATA = 3;

    int showingDataFor = MOTORBIKES_DATA;
    private List<CarwashRec> records = new ArrayList<>();
    private List<CarwashAttentantTotal> attentatsTotals = new ArrayList<>();
    private List<CarwashAttentantTotal> expenseTotals = new ArrayList<>();
    String selectedType = "Motorbike";
    private CarwashRec selectedRec = new CarwashRec();
    CarWashDailySummary summary = new CarWashDailySummary();

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

        labourLL = findViewById(R.id.labourLL);
        expenseLL = findViewById(R.id.expenseLL);
        overrallTotalTV = findViewById(R.id.overrallTotalTV);
        laborTotalTV = findViewById(R.id.laborTotalTV);
        expenseTV = findViewById(R.id.expenseTV);
        expenseViewBtn = findViewById(R.id.expenseViewBtn);
        salesTitleCV = findViewById(R.id.salesTitleCV);
        remTotalTV = findViewById(R.id.remTotalTV);
        mainDateIcon = findViewById(R.id.mainDateIcon);
        mainDateIcon.setOnClickListener(view -> {
            setDate();
        });
        expenseViewBtn.setOnClickListener(view -> {
            pDialog.show();
            if (salesTitleCV.isShown()) {
                selectedType = "Expense";
                getDaySummary(DateDisplaying, selectedType);
                salesTitleCV.setVisibility(View.GONE);
                expenseViewBtn.setText("View Sales");
            } else {
                salesTitleCV.setVisibility(View.VISIBLE);
                selectedType = "Motorbike";
                getDaySummary(DateDisplaying, selectedType);
                carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
                trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
                othersBtn.setBackgroundResource(R.color.gray_btn_bg_color);
                motorbksBtn.setBackgroundResource(R.color.colorPrimary);
                expenseViewBtn.setText("View Expenses");
            }


        });
        mainDaysDate = findViewById(R.id.mainDaysDate);

        mainDaysDate.setText(GeneralMethods.ChangeDateToSimpleFormat(todate));
        activityRV = findViewById(R.id.activityRV);
        activityRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activityRV.smoothScrollToPosition(0);

        carwashDailyRecsAdapter = new CarwashDailyRecsAdapter(getApplicationContext(), records);
        carwashDailyRecsAdapter.setListener(this);
//        carwashDailyRecsAdapter.setSelectedUserName("Total");
        activityRV.setAdapter(carwashDailyRecsAdapter);
//  motorbksBtn, carsBtn, trucksBtn;
        carsBtn = findViewById(R.id.carsBtn);
        trucksBtn = findViewById(R.id.trucksBtn);
        motorbksBtn = findViewById(R.id.motorbksBtn);
        othersBtn = findViewById(R.id.othersBtn);

        motorbksBtn.setBackgroundResource(R.color.colorPrimary);
        carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        othersBtn.setBackgroundResource(R.color.gray_btn_bg_color);

        motorbksBtn.setOnClickListener(v -> {
            pDialog.show();
            selectedType = "Motorbike";
            getDaySummary(DateDisplaying, selectedType);
            carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            othersBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            motorbksBtn.setBackgroundResource(R.color.colorPrimary);
        });
        carsBtn.setOnClickListener(v -> {
            pDialog.show();
            selectedType = "Car";
            getDaySummary(DateDisplaying, selectedType);
            carsBtn.setBackgroundResource(R.color.colorPrimary);
            trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            othersBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            motorbksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        trucksBtn.setOnClickListener(v -> {
            pDialog.show();
            selectedType = "Truck";
            getDaySummary(DateDisplaying, selectedType);
            carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            trucksBtn.setBackgroundResource(R.color.colorPrimary);
            othersBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            motorbksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        othersBtn.setOnClickListener(v -> {
            pDialog.show();
            selectedType = "Other";
            getDaySummary(DateDisplaying, selectedType);
            carsBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            trucksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            othersBtn.setBackgroundResource(R.color.colorPrimary);
            motorbksBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });

        rightArrow = findViewById(R.id.rightArrow);
        rightArrow.setOnClickListener(view -> {
            DateDisplaying = getnewDate(DateDisplaying, 1);
            updateUIOnDateChange();
        });
        leftArrow = findViewById(R.id.leftArrow);
        leftArrow.setOnClickListener(view -> {
            DateDisplaying = getnewDate(DateDisplaying, -1);
            updateUIOnDateChange();
        });
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });
        fab = findViewById(R.id.fab);
        addSaleBtn = findViewById(R.id.addSaleBtn);
        addSaleBtn.setOnClickListener(v -> {
            fab.close(true);
            showCWDialog("customer");
        });
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        addExpenseBtn.setOnClickListener(v -> {
            fab.close(true);
            showCWDialog("expense");
        });
        addWaterReadingBtn = findViewById(R.id.addWaterReadingBtn);
        addWaterReadingBtn.setOnClickListener(v -> {
            fab.close(true);
            showMRDialog("WATER");

        });
        addTokenReadingBtn = findViewById(R.id.addTokenReadingBtn);
        addTokenReadingBtn .setOnClickListener(v -> {
            fab.close(true);
            showMRDialog("TOKEN");
        });

        fetchUsers();
        paymentDialog = new Dialog(this);
        paymentDialog.setContentView(R.layout.carwash_payment_dialog);
        paymentDialog.setCanceledOnTouchOutside(false);
        paymentDialog.setCancelable(false);
        paymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pdVehicletypeTV = paymentDialog.findViewById(R.id.pdVehicletypeTV);
        pdRegnoTV = paymentDialog.findViewById(R.id.pdRegnoTV);
        pdAmountTV = paymentDialog.findViewById(R.id.pdAmountTV);
        pdSaveBtn = paymentDialog.findViewById(R.id.pdSaveBtn);
        pdSaveBtn.setOnClickListener(view -> {
            SavePayment();

        });
        ClosePDialog = paymentDialog.findViewById(R.id.ClosePDialog);
        ClosePDialog.setOnClickListener(view -> {
            paymentDialog.dismiss();
        });


        commissionsDialog = new Dialog(this);
        commissionsDialog.setContentView(R.layout.commission_dialog);
        commissionsDialog.setCanceledOnTouchOutside(false);
        commissionsDialog.setCancelable(false);
        commissionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TopupTitle = commissionsDialog.findViewById(R.id.TopupTitle);
        commissionTV = commissionsDialog.findViewById(R.id.commissionTV);
        CloseCommDialog = commissionsDialog.findViewById(R.id.CloseCommDialog);
        CloseCommDialog.setOnClickListener(view -> {
            commissionsDialog.dismiss();
        });


        meterReadingDialog = new Dialog(this);
        meterReadingDialog.setContentView(R.layout.meter_reading_dialog);
        meterReadingDialog.setCanceledOnTouchOutside(false);
        meterReadingDialog.setCancelable(false);
        meterReadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        readingTitleTV = meterReadingDialog.findViewById(R.id.readingTitleTV);
        startRET = meterReadingDialog.findViewById(R.id.startRET);
        lastReadingET = meterReadingDialog.findViewById(R.id.lastReadingET);
        meterDescET = meterReadingDialog.findViewById(R.id.meterDescET);
        SaveRBtn = meterReadingDialog.findViewById(R.id.SaveRBtn);
        CloseMRDialog = meterReadingDialog.findViewById(R.id.CloseMRDialog);
        CloseMRDialog .setOnClickListener(v -> {
            meterReadingDialog.dismiss();
        });

        SaveRBtn.setOnClickListener(view -> {
            String start = startRET.getText().toString().trim();
            String end = lastReadingET.getText().toString().trim();
            String desc = meterDescET.getText().toString().trim();
            int err = 0;
            if(start.length()<1){
                err = 1;
                startRET.requestFocus();
                startRET.setError("Enter valid value!!");
            }else {
                if(readingTitleTV.getText().toString().equalsIgnoreCase("WATER")){//Water
                    summary.getWaterReading().setStart(Float.parseFloat(start));
                    summary.getWaterReading().setEnd(Float.parseFloat(end));
                    summary.getWaterReading().setDescription(desc);
                }else {
                    summary.getDailyTokenReading().setStart(Float.parseFloat(start));
                    summary.getDailyTokenReading().setEnd(Float.parseFloat(end));
                    summary.getDailyTokenReading().setDescription(desc);
                }
                new Firestore(this).addCarWashSummary(summary);
                meterReadingDialog.dismiss();
            }


        });

        newCustomerDialog = new Dialog(this);
        newCustomerDialog.setContentView(R.layout.new_car_dialog);
        newCustomerDialog.setCanceledOnTouchOutside(false);
        newCustomerDialog.setCancelable(false);
        newCustomerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTitle = newCustomerDialog.findViewById(R.id.dialogTitle);
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
        expenseTypeLL = newCustomerDialog.findViewById(R.id.expenseTypeLL);
        attentantLL = newCustomerDialog.findViewById(R.id.attentantLL);
        regNoETIT = newCustomerDialog.findViewById(R.id.regNoETIT);
        attendantSpner = newCustomerDialog.findViewById(R.id.attendantSpner);
        expenseSpner = newCustomerDialog.findViewById(R.id.expenseSpner);
        radioGroup = newCustomerDialog.findViewById(R.id.radioGroup);

        rBike.setOnClickListener(view -> {//
            vihicleRG(rBike);
            toPayAmtET.setText("50");
            regNoET.setText("KM");
            regNoET.setSelection(regNoET.getText().length());
            regNoET.requestFocus();

        });

        rCar.setOnClickListener(view -> {//
            vihicleRG(rCar);
            toPayAmtET.setText("200");
            regNoET.setText("K");
            regNoET.setSelection(regNoET.getText().length());
            regNoET.requestFocus();
        });

        rTruck.setOnClickListener(view -> {//
            vihicleRG(rTruck);
            toPayAmtET.setText("500");
            regNoET.setText("K");
            regNoET.setSelection(regNoET.getText().length());
            regNoET.requestFocus();
        });

        rOther.setOnClickListener(view -> {//
            vihicleRG(rOther);
            toPayAmtET.setText("");
            regNoET.setSelection(regNoET.getText().length());
            regNoET.requestFocus();
            regNoET.setText("");
        });
        labourLL.setOnClickListener(view -> {
            String Comms = "";
            for (CarwashAttentantTotal attentantTotal : attentatsTotals
            ) {
                Comms = Comms + attentantTotal.getName() + ": Ksh. " + attentantTotal.getTotal() + "\n";
            }
            commissionTV.setText(Comms);
            TopupTitle.setText("Commissions");
            commissionsDialog.show();
        });
        expenseLL.setOnClickListener(view -> {
            String Comms = "";
            for (CarwashAttentantTotal exp : expenseTotals
            ) {
                Comms = Comms + exp.getName() + ": Ksh. " + exp.getTotal() + "\n";
            }
            commissionTV.setText(Comms);
            TopupTitle.setText("Day's Expenses");
            commissionsDialog.show();
        });


        attentantsSpinnerArray.add("Select Attendant");
        attentantsSpinnerArray.add("Kasyoka");
        attentantsSpinnerArray.add("Muinde");
        attentantsSpinnerArray.add("Ndunda");
        attentantsSpinnerArray.add("Internal");
        attentantsSpinnerArray.add("Other");

        attendantSpnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, attentantsSpinnerArray);

        attendantSpnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendantSpner.setAdapter(attendantSpnerAdapter);


        expenseSpinnerArray.add("Select Expense");
        expenseSpinnerArray.add("Electricity");
        expenseSpinnerArray.add("Petro");
        expenseSpinnerArray.add("Water");
        expenseSpinnerArray.add("Soap");
        expenseSpinnerArray.add("Other");

        expenseSpnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, expenseSpinnerArray);

        expenseSpnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseSpner.setAdapter(expenseSpnerAdapter);

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
                if (regNoET.isShown() && regNoET.getText().toString().length() < 2) {
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

                if (attendantSpner.isShown() && attendantSpner.getSelectedItemPosition() < 1) {
                    error = 1;
                    attendantSpner.requestFocus();
                    Toast.makeText(getApplicationContext(), "Select Attendant", Toast.LENGTH_SHORT).show();
                }
                if (expenseSpner.isShown() && expenseSpner.getSelectedItemPosition() < 1) {
                    error = 1;
                    attendantSpner.requestFocus();
                    Toast.makeText(getApplicationContext(), "Select Expense type", Toast.LENGTH_SHORT).show();
                }

                if (error == 0) {
                    saveCarWashRec();
                }
            } catch (Exception e) {
                toPayAmtET.setError("Invalid");
                toPayAmtET.requestFocus();
            }

        });


        toPayAmtET.setText("50");
        regNoET.setText("KM");
        regNoET.setSelection(regNoET.getText().length());
        regNoET.requestFocus();

        rBike.setChecked(true);
        vihicleRG(rBike);
//        getRecords(todate, selectedType);
        getDaySummary(todate, selectedType);
    }

    private void showCWDialog(String type) {
        daysDate.setText(DateDisplaying);
        mainDaysDate.setText(GeneralMethods.ChangeDateToSimpleFormat(DateDisplaying));
        getDaySummary(DateDisplaying, selectedType);
        //Reset Dialog

        if (type.equals("customer")) { //new Customer
            expenseTypeLL.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            attentantLL.setVisibility(View.VISIBLE);
            regNoETIT.setVisibility(View.VISIBLE);
            attendantSpner.setSelection(0);
            if (rBike.isChecked()) {
                regNoET.setText("KM");
                toPayAmtET.setText("50");
            } else if (rCar.isChecked()) {
                regNoET.setText("K");
                toPayAmtET.setText("200");
            } else {
                regNoET.setText("");
                toPayAmtET.setText("");
            }
            regNoET.setSelection(regNoET.getText().length());
            regNoET.requestFocus();
            serviceDescET.setText("");
            dialogTitle.setText("Customer Information");

        } else { //New Expense
            radioGroup.setVisibility(View.GONE);
            expenseTypeLL.setVisibility(View.VISIBLE);
            regNoETIT.setVisibility(View.GONE);
            attentantLL.setVisibility(View.GONE);
            expenseSpner.setSelection(0);
            toPayAmtET.setText("");
            serviceDescET.setText("");
            dialogTitle.setText("Expense Information");
        }


        newCustomerDialog.show();

    }

    private void showMRDialog(String type) {
        daysDate.setText(DateDisplaying);
        mainDaysDate.setText(GeneralMethods.ChangeDateToSimpleFormat(DateDisplaying));
        getDaySummary(DateDisplaying, selectedType);
        //Reset Dialog

        if (type.equals("TOKEN")) { //Token
            readingTitleTV.setText("TOKEN");
            startRET.setText(""+ summary.getDailyTokenReading().getStart());
            lastReadingET.setText(""+ summary.getDailyTokenReading().getEnd());
            meterDescET.setText(summary.getDailyTokenReading().getDescription());

        } else { //New Expense
            readingTitleTV.setText("WATER");
            startRET.setText(""+ summary.getWaterReading().getStart());
            lastReadingET.setText(""+ summary.getWaterReading().getEnd());
            meterDescET.setText(summary.getWaterReading().getDescription());
        }


        meterReadingDialog.show();

    }

    private void SavePayment() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Comfirm Payment")
                .setContentText("Are sure you have received Ksh. " + selectedRec.getAmount() + " for " + selectedRec.getRegNo() + "?")
                .setConfirmText("Paid!")
                .setConfirmClickListener(sDialog -> sDialog
                        .setTitleText("Payment Confirmed!")
                        .setContentText("Thank the Customer")
                        .setConfirmText("OK")
                        .showCancelButton(false)
                        .setConfirmClickListener(sweetAlertDialog -> {
                            //Update payment
                            selectedRec.setPaid(true);
                            new Firestore(this).addCarWashRec(selectedRec);
                            sweetAlertDialog.dismiss();
                            paymentDialog.dismiss();
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE))
                .showCancelButton(true)
                .setCancelText("Cancel")
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    paymentDialog.dismiss();
                })
                .show();
    }

    public void vihicleRG(RadioButton rb) {
        rCar.setChecked(false);
        rTruck.setChecked(false);
        rOther.setChecked(false);
        rBike.setChecked(false);
        rb.setChecked(true);
    }

    public void saveCarWashRec() {
        CarwashRec carwashRec = new CarwashRec();

        carwashRec.setRecordedBy(userdb.getName());
        carwashRec.setDate(daysDate.getText().toString());
        carwashRec.setRecordedTimeNDate(getDateNTime());

        if (dialogTitle.getText().toString().contains("Customer")) {
            // new Customer
            carwashRec.setRegNo(regNoET.getText().toString().toUpperCase());
            carwashRec.setAttendant(attendantSpner.getSelectedItem().toString());
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
        } else {
            //new Expense
            carwashRec.setRegNo(expenseSpner.getSelectedItem().toString());
            carwashRec.setVehicleType("Expense");
        }

        carwashRec.setAmount(Integer.parseInt(toPayAmtET.getText().toString().trim()));
        carwashRec.setDesc(serviceDescET.getText().toString().trim());
        carwashRec.setPaid(false);

        new Firestore(CarWashMainActivity.this).addCarWashRec(carwashRec);

        newCustomerDialog.dismiss();
    }

//    private void displayRecords(int dataFor) {
//        showingDataFor = dataFor;
////        fetchUserSales(USERID);
//        pDialog.dismiss();
//    }


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
                    updateUIOnDateChange();
                }
            };

    public void updateUIOnDateChange() {
        daysDate.setText(DateDisplaying);
        mainDaysDate.setText(GeneralMethods.ChangeDateToSimpleFormat(DateDisplaying));
        summary = new CarWashDailySummary();
        getDaySummary(DateDisplaying, selectedType);
    }

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

    public void getRecords(String date, String type) {
        pDialog.show();
        firedb.collection(Constants.carWashRecPath)
                .whereEqualTo("date", date)
                .orderBy("recordedTimeNDate", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    List<CarwashRec> allRecords = new ArrayList<>();
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        return;
                    } else if (value.isEmpty()) {
                        pDialog.dismiss();
                        sortByType(allRecords, type, date);
                    } else {

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                CarwashRec rec = doc.toObject(CarwashRec.class);
                                allRecords.add(rec);
                            }
                        }
                        sortByType(allRecords, type, date);
                    }
                });

    }
    public void getDaySummary(String date, String type) {
        pDialog.show();
        firedb.collection(Constants.carwashDailySummary)
                .whereEqualTo("date", date)
//                .orderBy("recordedTimeNDate", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    List<CarWashDailySummary> allSales = new ArrayList<>();
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        getRecords(date, type);
                        return;
                    } else if (value.isEmpty()) {
                        pDialog.dismiss();
                        getRecords(date, type);

                    } else {

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                summary = doc.toObject(CarWashDailySummary.class);
                            }
                        }
                        getRecords(date, type);
                    }
                });

    }

    public void sortByType(List<CarwashRec> allRecords, String type, String date) {
        int mBikes = 0, cars = 0, trucks = 0, overTotal = 0, others = 0;
        int labour = 0;
        int expense = 0;
        attentatsTotals = new ArrayList<>();
        expenseTotals = new ArrayList<>();
        records = new ArrayList<>();
        for (CarwashRec rec : allRecords) {
            if (rec.getVehicleType().equals("Motorbike")) {
                mBikes++;
            } else if (rec.getVehicleType().equals("Car")) {
                cars++;
            } else if (rec.getVehicleType().equals("Truck")) {
                trucks++;
            } else if (rec.getVehicleType().equals("Other")) {
                others++;
            }

            if (rec.getVehicleType().equals(type)) {
                records.add(rec);
            }
            if (rec.getVehicleType().equalsIgnoreCase("Expense")) {
                expense = expense + rec.getAmount();

                boolean expenseFound = false;
                for (CarwashAttentantTotal exp : expenseTotals) {
                    if (exp.getName().equals(rec.getRegNo())) {
                        exp.addToTotal(rec.getAmount());
                        expenseFound = true;
                    }
                }
                if (!expenseFound) {
                    expenseTotals.add(new CarwashAttentantTotal(rec.getRegNo(), rec.getAmount()));
                }

            } else {
                overTotal = overTotal + rec.getAmount();
                int commission = GeneralMethods.getCarwashCommission(rec.getAmount());
                if (!rec.getAttendant().equals("Internal")) {
                    labour = labour + commission;
                }

                boolean attentantFound = false;
                for (CarwashAttentantTotal attentantTotal : attentatsTotals) {
                    if (attentantTotal.getName().equals(rec.getAttendant())) {
                        attentantTotal.addToTotal(commission);
                        attentantFound = true;
                    }
                }
                if (rec.getAttendant() != null && !attentantFound && !rec.getAttendant().equals("Internal")) {
                    attentatsTotals.add(new CarwashAttentantTotal(rec.getAttendant(), commission));
                }
            }
        }
        carwashDailyRecsAdapter = new CarwashDailyRecsAdapter(getApplicationContext(), records);
        carwashDailyRecsAdapter.setListener(this);
//        carwashDailyRecsAdapter.setSelectedUserName("Total");
        activityRV.setAdapter(carwashDailyRecsAdapter);
//        carwashDailyRecsAdapter.notifyDataSetChanged();

        motorbksBtn.setText("M. Bikes (" + mBikes + ")");
        carsBtn.setText("Cars (" + cars + ")");
        trucksBtn.setText("Trucks (" + trucks + ")");
        othersBtn.setText("Others (" + others + ")");
        overrallTotalTV.setText("Ksh. " + overTotal);
        laborTotalTV.setText("Ksh. " + labour);
        expenseTV.setText("Ksh. " + expense);
        int rem = overTotal - labour;
        remTotalTV.setText("Ksh. " + rem);

        summary.setDate(date);
        summary.setBalTotal(rem);
        summary.setExpense(expense);
        summary.setOverallTotal(overTotal);
        summary.setMotorbikes(mBikes);
        summary.setCars(cars);
        summary.setTrucks(trucks);
        summary.setOthers(others);
        summary.setAttentantsTotals(attentatsTotals);
        summary.setLabourTotal(labour);

        int weekNo = new GeneralMethods().getWeekNumber(DateDisplaying);
        summary.setYear_week(year + "" + weekNo);
        summary.setYear_month(new GeneralMethods().getDateParts(DateDisplaying, "yy") + new GeneralMethods().getDateParts(DateDisplaying, "MM"));
        summary.setYear(new GeneralMethods().getDateParts(DateDisplaying, "yy"));
        if (summary.getOverallTotal() > 0 || summary.getExpense() > 0) {
            new Firestore(this).addCarWashSummary(summary);
        }
        pDialog.dismiss();
    }

    public String getDateNTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy : HH:mm:ss", Locale.getDefault());
        return df.format(c);
    }

    @Override
    public void acceptPayment(CarwashRec selectedRec, boolean refreshRV) {
        this.selectedRec = selectedRec;
        pdVehicletypeTV.setText(selectedRec.getVehicleType());
        pdRegnoTV.setText(selectedRec.getRegNo());
        pdAmountTV.setText("Ksh. " + selectedRec.getAmount());
        paymentDialog.show();
    }

    public String getnewDate(String sourceDate, int days) {
        String destDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(sourceDate)); // parsed date and setting to calendar
            calendar.add(Calendar.DATE, days);  // number of days to add
            destDate = sdf.format(calendar.getTime());  // End date
        } catch (ParseException e) {
            e.printStackTrace();
            return destDate;
        }
        destDate = destDate.replace("/0", "/");
        if (destDate.startsWith("0")) {
            destDate = destDate.substring(1);
        }
        return destDate;
    }
}