package com.munit.m_unitapp.UI.POOL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.ADAPTERS.PoolRecordsAdapter_New;
import com.munit.m_unitapp.ADAPTERS.UsersNamesAdapter;
import com.munit.m_unitapp.DB.Firestore;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.Employee;
import com.munit.m_unitapp.MODELS.PoolRecordNew;
import com.munit.m_unitapp.MODELS.PoolTable;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;
import com.munit.m_unitapp.UI.CYBER.AddSales;
import com.munit.m_unitapp.UI.CYBER.CashInActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolTableActivity extends AppCompatActivity {
    private ImageView back_arrow, editPLBtn, CloseDialog, CloseRDialog, poolIcon, calenderIcon, rcalenderIcon;
    private EditText nameEt, costEt, rAmt;
    private Spinner colorSpiner, locationSpiner;
    private TextView purchaseDate, dateOfPurchaseTV, ageTV, costTV, returnsTV, locationTV, nameTV, rDate, rInitialAmtTV;
    private Button SaveBtn, rSaveBtn;
    private CircularImageView ProfilePic;
    private RelativeLayout newRcdBtn;
    private RecyclerView recordsRV;
    private User dbuser;
    firebase db = new firebase();
    private Dialog newPoolDialog, poolRecordDialog;
    PoolTable poolTable = new PoolTable();
    private PoolRecordsAdapter_New adapter;

    SweetAlertDialog pDialog;
    FirebaseFirestore firedb;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    private Calendar calendar;
    private int year, month, day;
    String todate, DateDisplaying;

    private Date todateDate;

    List <PoolRecordNew> records = new ArrayList<>();
    int poolReturns = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_table);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firedb = FirebaseFirestore.getInstance();

        getSupportActionBar().hide();

        Gson gson = new Gson();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            poolTable = gson.fromJson(getIntent().getStringExtra("poolTableJson"), PoolTable.class);
        } else {
            finish();
        }

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

        todateDate = calendar.getTime();

        editPLBtn = findViewById(R.id.editPLBtn);
        dateOfPurchaseTV = findViewById(R.id.dateOfPurchaseTV);
        costTV = findViewById(R.id.costTV);
        ageTV = findViewById(R.id.ageTV);
        returnsTV = findViewById(R.id.returnsTV);
        ProfilePic = findViewById(R.id.ProfilePic);
        locationTV = findViewById(R.id.locationTV);
        nameTV = findViewById(R.id.nameTV);
        newRcdBtn = findViewById(R.id.newRcdBtn);

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(v -> finish());

        poolRecordDialog = new Dialog(this);
        poolRecordDialog.setCanceledOnTouchOutside(false);
        poolRecordDialog.setContentView(R.layout.new_pool_record_dialog);
        poolRecordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseRDialog = poolRecordDialog.findViewById(R.id.CloseRDialog);
        poolIcon = poolRecordDialog.findViewById(R.id.poolIcon);
        rDate = poolRecordDialog.findViewById(R.id.rDate);
        rcalenderIcon = poolRecordDialog.findViewById(R.id.rcalenderIcon);
        rInitialAmtTV = poolRecordDialog.findViewById(R.id.rInitialAmtTV);
        rAmt = poolRecordDialog.findViewById(R.id.rAmt);
        rSaveBtn = poolRecordDialog.findViewById(R.id.rSaveBtn);

        CloseRDialog.setOnClickListener(v -> {
            poolRecordDialog.dismiss();
        });


        rcalenderIcon.setOnClickListener((view) -> {
            setDate();
        });
        rSaveBtn.setOnClickListener(v -> {
            String amtS =rAmt.getText().toString().trim();
            if(amtS.length()<1){
                rAmt.setError("Enter valid amount");
            }else {
                int amt = Integer.parseInt(amtS);
                saveRecord(amt);
            }

        });


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

        newRcdBtn.setOnClickListener(v -> {
            rDate.setText(DateDisplaying);
            rAmt.setText("");
            poolRecordDialog.show();
        });


        recordsRV = findViewById(R.id.recordsRV);
        recordsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recordsRV.smoothScrollToPosition(0);

        adapter = new PoolRecordsAdapter_New(getApplicationContext(), records);
        recordsRV.setAdapter(adapter);


        updateUi();
        fetchPoolRecords(poolTable.getId());
    }

    public void fetchPoolRecords(int key) {
        pDialog.show();
        firedb.collection(Constants.poolRecordsPath)
                .whereEqualTo("poolId", String.valueOf(key))
                .orderBy("id", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        pDialog.dismiss();
//                        computeUserSales(new ArrayList<>());
                        return;
                    }else if (value.isEmpty()) {
//                        computeUserSales(new ArrayList<>());
                        pDialog.dismiss();
                    }else {
                        records.clear();
                        poolReturns = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                PoolRecordNew record = doc.toObject(PoolRecordNew.class);
                                poolReturns += record.getAmount();
                                records.add(record);
                            }
                        }
//                        allweeklySales.sort(Comparator.comparing(DailySales::getSortValue).reversed());
//                        computeUserSales(allweeklySales);
                    }
                    adapter.notifyDataSetChanged();
                    returnsTV.setText("Ksh. " + String.format("%,.2f", (double) poolReturns));
                    poolTable.setReturns(poolReturns);
                    poolTable.setAge(ageTV.getText().toString());
                    db.savePoolTable(poolTable);
                    pDialog.dismiss();
                });

    }
    private void saveRecord(int amt) {
        PoolRecordNew recordNew = new PoolRecordNew();
        recordNew.setAmount(amt);
        recordNew.setPoolId(String.valueOf(poolTable.getId()));
        recordNew.setPoolName(poolTable.getName());

        String dateV = rDate.getText().toString().trim();

        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(dateV);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        int dateInt = (int) (date1.getTime() / 1000);
        recordNew.setId(dateInt);
        recordNew.setDate(dateV);
        recordNew.setYear_week(new GeneralMethods().getDateParts(DateDisplaying,"yy") +""+ new GeneralMethods().getWeekNumber(DateDisplaying));
        recordNew.setYear_month(new GeneralMethods().getDateParts(DateDisplaying,"yy")+new GeneralMethods().getDateParts(DateDisplaying, "MM"));
        recordNew.setYear(new GeneralMethods().getDateParts(DateDisplaying,"yy"));

        new Firestore(this).addPoolRecord(recordNew);

        poolRecordDialog.dismiss();
    }

    private void updateUi() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date pd = null;
        try {
            pd =  format.parse(poolTable.getDateOfPurchase());
        }catch (Exception e){

        }

        int days = new GeneralMethods().getDifferenceDays(pd,todateDate);
        int years = days/365;
        int remDays = days%365;
        int months = remDays/30;
        days = remDays%30;

        String age = "";
        if(years>0){
            age = age + years + " Yrs ";
        }
        if(months>0){
            age = age + months + " Months ";
        }
        age = age + days + " days";



        dateOfPurchaseTV.setText(poolTable.getDateOfPurchase());
        ageTV.setText(age);
        costTV.setText("Ksh. " + String.format("%,.2f", (double) poolTable.getCost()));
        returnsTV.setText("Ksh. " + String.format("%,.2f", (double) poolTable.getReturns()));
        dateOfPurchaseTV.setText(poolTable.getDateOfPurchase());
        locationTV.setText(poolTable.getLocation());
        nameTV.setText(poolTable.getName());

        if (poolTable.getColor().equalsIgnoreCase("blue")) {
            ProfilePic.setBackgroundResource(R.drawable.blue_pool_table);
            poolIcon.setBackgroundResource(R.drawable.blue_pool_table);
        } else if (poolTable.getColor().equalsIgnoreCase("red")) {
            ProfilePic.setBackgroundResource(R.drawable.red_pool_table);
            poolIcon.setBackgroundResource(R.drawable.red_pool_table);
        } else {
            ProfilePic.setBackgroundResource(R.drawable.green_pool_table);
            poolIcon.setBackgroundResource(R.drawable.green_pool_table);
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
                    DateDisplaying = arg3 + "/" + (arg2 + 1) + "/" + arg1;
                    purchaseDate.setText(DateDisplaying);
                    rDate.setText(DateDisplaying);
                }
            };
}