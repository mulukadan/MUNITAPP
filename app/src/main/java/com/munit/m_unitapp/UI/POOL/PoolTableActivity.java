package com.munit.m_unitapp.UI.POOL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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
import com.munit.m_unitapp.MODELS.PoolTableRecord;
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
import java.util.Collections;
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
    private LinearLayout chartViewLL, listViewLL;
    private BarChart chart;
    private TextView listViewBtn, chartViewBtn;
    private TextView yearlyBtn, monthlyBtn, dailyBtn;
    private ScrollView scroll;

    SweetAlertDialog pDialog;
    FirebaseFirestore firedb;

    FirebaseDatabase database;
    FirebaseUser user;
    private Calendar calendar;
    private int year, month, day;
    String todate, DateDisplaying;

    private Date todateDate;

    List<PoolRecordNew> records = new ArrayList<>();
    List<PoolRecordNew> DisplayingRecords = new ArrayList<>();
    int poolReturns = 0;
    private  boolean showName = true;


    final int DAILY_DATA = 0;
    final int MONTHLY_DATA = 1;
    final int YEARLY_DATA = 2;

    int showingDataFor = DAILY_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_table);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firedb = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firedb.setFirestoreSettings(settings);
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
        nameTV = findViewById(R.id.nameTV);
        newRcdBtn = findViewById(R.id.newRcdBtn);
        scroll = findViewById(R.id.scroll);

        listViewBtn = findViewById(R.id.listViewBtn);
        chartViewBtn = findViewById(R.id.chartViewBtn);
        chartViewLL = findViewById(R.id.chartViewLL);
        listViewLL = findViewById(R.id.listViewLL);
        chartViewLL.setVisibility(View.GONE);
        chart = findViewById(R.id.chart);

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
            String amtS = rAmt.getText().toString().trim();
            if (amtS.length() < 1) {
                rAmt.setError("Enter valid amount");
            } else {
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

        adapter = new PoolRecordsAdapter_New(getApplicationContext(), DisplayingRecords);
        recordsRV.setAdapter(adapter);


        yearlyBtn = findViewById(R.id.yearlyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        dailyBtn = findViewById(R.id.dailyBtn);

        dailyBtn.setBackgroundResource(R.color.colorPrimary);
        yearlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        dailyBtn.setOnClickListener(v -> {
            pDialog.show();
            showingDataFor = DAILY_DATA;
            fetchPoolRecords(poolTable.getId());
            yearlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            dailyBtn.setBackgroundResource(R.color.colorPrimary);
        });
        yearlyBtn.setOnClickListener(v -> {
            pDialog.show();
            showingDataFor = YEARLY_DATA;
            fetchPoolRecords(poolTable.getId());
            yearlyBtn.setBackgroundResource(R.color.colorPrimary);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        monthlyBtn.setOnClickListener(v -> {
            pDialog.show();
            showingDataFor = MONTHLY_DATA;
            fetchPoolRecords(poolTable.getId());
            yearlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.colorPrimary);
            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });

        chartViewBtn.setOnClickListener(v -> {
            chartViewLL.setVisibility(View.VISIBLE);
            listViewLL.setVisibility(View.GONE);
            chartViewBtn.setBackgroundResource(R.color.colorPrimary);
            listViewBtn.setBackgroundResource(R.color.gray_btn_bg_color);
//            scroll.fullScroll(View.FOCUS_DOWN);
            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));

//            populateChart(records);
        });
        listViewBtn.setOnClickListener(v -> {
            chartViewLL.setVisibility(View.GONE);
            listViewLL.setVisibility(View.VISIBLE);
            chartViewBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            listViewBtn.setBackgroundResource(R.color.colorPrimary);
        });


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
                    } else if (value.isEmpty()) {
//                        computeUserSales(new ArrayList<>());
                        pDialog.dismiss();
                    } else {
                        records.clear();
                        DisplayingRecords.clear();

                        poolReturns = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                PoolRecordNew record = doc.toObject(PoolRecordNew.class);
                                poolReturns += record.getAmount();
                                records.add(record);
                                DisplayingRecords.add(record);
                            }
                        }
//                        allweeklySales.sort(Comparator.comparing(DailySales::getSortValue).reversed());
//                        computeUserSales(allweeklySales);
                    }
                    computePoolRecords();

                    returnsTV.setText("Ksh. " + String.format("%,.2f", (double) poolReturns));
                    poolTable.setReturns(poolReturns);
                    poolTable.setAge(ageTV.getText().toString());
                    db.savePoolTable(poolTable);
                    pDialog.dismiss();
                });
    }

    private void computePoolRecords() {
        DisplayingRecords.clear();
        List<PoolRecordNew> records2 = new ArrayList<>();
        records2.addAll(records);
        switch (showingDataFor) {
            case DAILY_DATA:
//                for (PoolRecordNew poolRecordNew : records) {
////                    poolRecordNew.setUserName(sale.getDate());
//                    DisplayingRecords.add(poolRecordNew);
//                }

                DisplayingRecords.addAll(records2);
                break;

            case MONTHLY_DATA:
                for (PoolRecordNew poolRecordNew : records2) {
                    boolean found = false;
                    for (PoolRecordNew record : DisplayingRecords) {
                        if (record.getYear_month().equalsIgnoreCase(poolRecordNew.getYear_month())) {//Exists
                            record.setAmount(record.getAmount() + poolRecordNew.getAmount());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //Add new
                        poolRecordNew.setDate(new GeneralMethods().getMonthName(Integer.parseInt(poolRecordNew.getYear_month().substring(4))) + " " + poolRecordNew.getYear());
                        DisplayingRecords.add(poolRecordNew);
                    }

                }
                break;

            case YEARLY_DATA:
                for (PoolRecordNew poolRecordNew : records2) {
                    boolean found = false;
                    for (PoolRecordNew record : DisplayingRecords) {
                        if (record.getYear().equalsIgnoreCase(poolRecordNew.getYear())) {//Exists
                            record.setAmount(record.getAmount() + poolRecordNew.getAmount());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //Add new
                        poolRecordNew.setDate(poolRecordNew.getYear());
                        DisplayingRecords.add(poolRecordNew);
                    }

                }
                break;
        }

        adapter.notifyDataSetChanged();
        populateChart(DisplayingRecords);
        pDialog.dismiss();
    }

    public void populateChart(List<PoolRecordNew> recds) {
        List<PoolRecordNew> salesRev = new ArrayList<>();
        salesRev.addAll(recds);
        Collections.reverse(salesRev);
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (PoolRecordNew dailySales : salesRev) {
            yVals1.add(new BarEntry(salesRev.indexOf(dailySales), dailySales.getAmount()));
            xAxisLabel.add(shortenForChart(dailySales.getDate()));
        }

        BarDataSet set1;
        String desc="";
        switch (showingDataFor) {
            case DAILY_DATA:
                desc="Daily sales";
                break;
            case YEARLY_DATA:
                desc="Yearly Sales";
                break;
            case MONTHLY_DATA:
                desc="Monthly Sales";
                break;
        }

        set1 = new BarDataSet(yVals1, desc);
        set1.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        chart.setTouchEnabled(true);
        chart.setHorizontalScrollBarEnabled(true);
        chart.setData(data);
        chart.animateXY(2000, 2000);
        chart.invalidate();

    }

    public String shortenForChart(String title) {
        if (title.contains("Jan")) {
            return "Jan";
        }
        if (title.contains("Feb")) {
            return "Feb";
        }
        if (title.contains("Mar")) {
            return "Mar";
        }
        if (title.contains("Apr")) {
            return "Apr";
        }
        if (title.contains("May")) {
            return "May";
        }
        if (title.contains("Jun")) {
            return "Jun";
        }
        if (title.contains("Jul")) {
            return "Jul";
        }
        if (title.contains("Aug")) {
            return "Aug";
        }
        if (title.contains("Sep")) {
            return "Sep";
        }
        if (title.contains("Oct")) {
            return "Oct";
        }
        if (title.contains("Nov")) {
            return "Nov";
        }
        if (title.contains("Dec")) {
            return "Dec";
        }

        title = title.replace("Week", "wk");
        title = title.replace("Last", "L.");
        title = title.replace("but", "bt");

        return title;
    }

    private void saveRecord(int amt) {
        PoolRecordNew recordNew = new PoolRecordNew();
        recordNew.setAmount(amt);
        recordNew.setPoolId(String.valueOf(poolTable.getId()));
        recordNew.setPoolName(poolTable.getName());
        recordNew.setLocation(poolTable.getName().substring(0, poolTable.getName().indexOf(".")));

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
        recordNew.setYear_week(new GeneralMethods().getDateParts(DateDisplaying, "yy") + "" + new GeneralMethods().getWeekNumber(DateDisplaying));
        recordNew.setYear_month(new GeneralMethods().getDateParts(DateDisplaying, "yy") + new GeneralMethods().getDateParts(DateDisplaying, "MM"));
        recordNew.setYear(new GeneralMethods().getDateParts(DateDisplaying, "yy"));

        new Firestore(this).addPoolRecord(recordNew);

        poolRecordDialog.dismiss();
    }

    private void updateUi() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date pd = null;
        try {
            pd = format.parse(poolTable.getDateOfPurchase());
        } catch (Exception e) {

        }

        int days = new GeneralMethods().getDifferenceDays(pd, todateDate);
        int years = days / 365;
        int remDays = days % 365;
        int months = remDays / 30;
        days = remDays % 30;

        String age = "";
        if (years > 0) {
            age = age + years + " Yrs ";
        }
        if (months > 0) {
            age = age + months + " Months ";
        }
        age = age + days + " days";


        dateOfPurchaseTV.setText(poolTable.getDateOfPurchase());
        ageTV.setText(age);
        costTV.setText("Ksh. " + String.format("%,.2f", (double) poolTable.getCost()));
        returnsTV.setText("Ksh. " + String.format("%,.2f", (double) poolTable.getReturns()));
        dateOfPurchaseTV.setText(poolTable.getDateOfPurchase());

        nameTV.setText(poolTable.getName() + ", " + poolTable.getLocation());

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