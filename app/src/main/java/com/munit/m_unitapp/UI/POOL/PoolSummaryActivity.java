package com.munit.m_unitapp.UI.POOL;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.munit.m_unitapp.ADAPTERS.PoolRecordsAdapter_New;
import com.munit.m_unitapp.DB.Firestore;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.PoolRecordNew;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolSummaryActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private TextView ek1Btn, kv1Btn, allBtn;

    private RecyclerView recordsRV;
    private User dbuser;
    firebase db = new firebase();
    private PoolRecordsAdapter_New adapter;
    private LinearLayout chartViewLL, listViewLL;
    private BarChart chart;
    private TextView listViewBtn, chartViewBtn;
    private TextView yearlyBtn, monthlyBtn, dailyBtn;
    private ScrollView scroll;

    SweetAlertDialog pDialog;
    FirebaseFirestore firedb;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    private Calendar calendar;
    private int year, month, day;
    String todate, DateDisplaying;

    private Date todateDate;

    List<PoolRecordNew> records = new ArrayList<>();
    List<PoolRecordNew> DisplayingRecords = new ArrayList<>();
    int poolReturns = 0;


    final int EK_DATA = 0;
    final int KV_DATA = 1;
    final int All_DATA = 2;
    int locDataFor = EK_DATA;

    final int DAILY_DATA = 0;
    final int MONTHLY_DATA = 1;
    final int YEARLY_DATA = 2;
    int showingDataFor = DAILY_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_summary);
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firedb = FirebaseFirestore.getInstance();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Fetching Data....");
        pDialog.setCancelable(false);

        back_arrow = findViewById(R.id.back_arrow);
        ek1Btn = findViewById(R.id.ek1Btn);
        kv1Btn = findViewById(R.id.kv1Btn);
        allBtn = findViewById(R.id.allBtn);

        ek1Btn.setBackgroundResource(R.color.colorPrimary);
        kv1Btn.setBackgroundResource(R.color.gray_btn_bg_color);
        allBtn.setBackgroundResource(R.color.gray_btn_bg_color);

        ek1Btn.setOnClickListener(v -> {
            pDialog.show();
            locDataFor = EK_DATA;
            fetchPoolRecords();
            ek1Btn.setBackgroundResource(R.color.colorPrimary);
            kv1Btn.setBackgroundResource(R.color.gray_btn_bg_color);
            allBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        kv1Btn.setOnClickListener(v -> {
            pDialog.show();
            locDataFor = KV_DATA;
            fetchPoolRecords();
            ek1Btn.setBackgroundResource(R.color.gray_btn_bg_color);
            kv1Btn.setBackgroundResource(R.color.colorPrimary);
            allBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        allBtn.setOnClickListener(v -> {
            pDialog.show();
            locDataFor = All_DATA;
            fetchPoolRecords();
            ek1Btn.setBackgroundResource(R.color.gray_btn_bg_color);
            kv1Btn.setBackgroundResource(R.color.gray_btn_bg_color);
            allBtn.setBackgroundResource(R.color.colorPrimary);
        });


        chartViewLL = findViewById(R.id.chartViewLL);
        listViewLL = findViewById(R.id.listViewLL);
        chart = findViewById(R.id.chart);
        listViewBtn = findViewById(R.id.listViewBtn);
        chartViewBtn = findViewById(R.id.chartViewBtn);
        chartViewLL.setVisibility(View.GONE);
        scroll = findViewById(R.id.scroll);


        back_arrow.setOnClickListener(view -> finish());

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
            fetchPoolRecords();
            yearlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            dailyBtn.setBackgroundResource(R.color.colorPrimary);
        });
        yearlyBtn.setOnClickListener(v -> {
            pDialog.show();
            showingDataFor = YEARLY_DATA;
            fetchPoolRecords();
            yearlyBtn.setBackgroundResource(R.color.colorPrimary);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        monthlyBtn.setOnClickListener(v -> {
            pDialog.show();
            showingDataFor = MONTHLY_DATA;
            fetchPoolRecords();
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
            try {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            } catch (Exception e) {

            }

        });
        listViewBtn.setOnClickListener(v -> {
            chartViewLL.setVisibility(View.GONE);
            listViewLL.setVisibility(View.VISIBLE);
            chartViewBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            listViewBtn.setBackgroundResource(R.color.colorPrimary);
        });

//        updateUi();
        fetchPoolRecords();
    }

    public void fetchPoolRecords() {
        switch (locDataFor) {
            case EK_DATA:
                fetchFilteredPoolRecords("EK-1");
                break;
            case KV_DATA:
//                UpdateKiv("KV-1");
                fetchFilteredPoolRecords("KV-1");
                break;
            case All_DATA:
                fetchAllPoolRecords();
                break;
        }

    }

    public void fetchAllPoolRecords() {
        pDialog.show();
        firedb.collection(Constants.poolRecordsPath)
//                .whereEqualTo("poolId", String.valueOf(key))
                .orderBy("id", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        pDialog.dismiss();
                        return;
                    } else if (value.isEmpty()) {
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
                    }
                    computePoolRecords();
                    pDialog.dismiss();
                });
    }

    public void fetchFilteredPoolRecords(String key) {
        pDialog.show();
        firedb.collection(Constants.poolRecordsPath)
                .whereEqualTo("location", String.valueOf(key))
                .orderBy("id", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    records.clear();
                    DisplayingRecords.clear();
                    poolReturns = 0;

                    if (e != null) {
                        pDialog.dismiss();
                        return;
                    } else if (value.isEmpty()) {

                        pDialog.dismiss();
                    } else {

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                PoolRecordNew record = doc.toObject(PoolRecordNew.class);
                                poolReturns += record.getAmount();
                                records.add(record);
                                DisplayingRecords.add(record);
                            }
                        }
                    }
                    computePoolRecords();
                    pDialog.dismiss();
                });
    }

 public void UpdateKiv(String key) {
        pDialog.show();
        firedb.collection(Constants.poolRecordsPath)
                .whereEqualTo("poolId", "1639402083")
                .orderBy("id", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    records.clear();
                    DisplayingRecords.clear();
                    poolReturns = 0;

                    if (e != null) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (value.isEmpty()) {

                        pDialog.dismiss();
                    } else {

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                PoolRecordNew record = doc.toObject(PoolRecordNew.class);
                                record.setLocation("KV-1");
                                new Firestore(this).addPoolRecord(record);
                                poolReturns += record.getAmount();
                                records.add(record);
                                DisplayingRecords.add(record);
                            }
                        }
                    }
                    computePoolRecords();
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
        String desc = "";
        switch (showingDataFor) {
            case DAILY_DATA:
                desc = "Daily sales";
                break;
            case YEARLY_DATA:
                desc = "Yearly Sales";
                break;
            case MONTHLY_DATA:
                desc = "Monthly Sales";
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
}