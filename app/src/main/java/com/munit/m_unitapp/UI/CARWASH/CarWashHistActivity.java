package com.munit.m_unitapp.UI.CARWASH;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.munit.m_unitapp.ADAPTERS.AllCarwashSalesAdapter;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.ADAPTERS.UsersNamesAdapter;
import com.munit.m_unitapp.MODELS.CarWashDailySummary;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;
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

public class CarWashHistActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private Calendar calendar;
    private int year, month, day;
    private String todate;
    private BarChart chart;
    private TextView listViewBtn, chartViewBtn;
    private Spinner reportsSpner;

    List<String> reportsSpinnerArray = new ArrayList<>();
    String reportFor;
    private ArrayAdapter<String> reportSpnerAdapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private TextView daysDate, field1, field2, weeklyBtn, monthlyBtn, dailyBtn;
    private User userdb = new User();
    private LinearLayout chartViewLL, listViewLL;
    private Gson gson;
    private SweetAlertDialog pDialog;
    private FirebaseFirestore firedb;

    RecyclerView weeklySummary;
    AllCarwashSalesAdapter adapter;
    List<CarWashDailySummary> records = new ArrayList<>();
    CarWashDailySummary summary;
    int todaysWeekNo;
    final int DAILY_DATA = 1;
    final int WEEKLY_DATA = 2;
    final int MONTHLY_DATA = 3;

    int showingDataFor = DAILY_DATA;
    int pos = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash_hist);

        getSupportActionBar().hide();

        reportsSpner = findViewById(R.id.reportsSpner);
        weeklyBtn = findViewById(R.id.weeklyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        dailyBtn = findViewById(R.id.dailyBtn);
        field1 = findViewById(R.id.field1);
        field2 = findViewById(R.id.field2);

        reportsSpinnerArray.add("Labour and Expense");
        reportsSpinnerArray.add("Token and Water");
        reportFor = reportsSpinnerArray.get(0);
        reportSpnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, reportsSpinnerArray);

        reportSpnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportsSpner.setAdapter(reportSpnerAdapter);

        reportsSpner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                reportFor = reportsSpner.getSelectedItem().toString();
                if (reportFor.equalsIgnoreCase("Labour and Expense")) {
                    field1.setText("Lbr");
                    field2.setText("Exp.");
                } else {
                    field1.setText("H2O");
                    field2.setText("Token");
                }
                adapter.setReportFor(reportFor);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
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

        todaysWeekNo = new GeneralMethods().getWeekNumber(todate);
        weeklySummary = findViewById(R.id.weeklySummary);
        weeklySummary.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        weeklySummary.smoothScrollToPosition(0);

        adapter = new AllCarwashSalesAdapter(weeklySummary.getContext(), records, reportFor);
//        adapter.setListener(this);
        adapter.setSelectedUserName("Total");
        weeklySummary.setAdapter(adapter);

        listViewBtn = findViewById(R.id.listViewBtn);
        chartViewBtn = findViewById(R.id.chartViewBtn);
        chartViewLL = findViewById(R.id.chartViewLL);
        listViewLL = findViewById(R.id.listViewLL);
        chartViewLL.setVisibility(View.GONE);
        chart = findViewById(R.id.chart);
        daysDate = findViewById(R.id.daysDate);

        weeklyBtn = findViewById(R.id.weeklyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        dailyBtn = findViewById(R.id.dailyBtn);

        dailyBtn.setBackgroundResource(R.color.colorPrimary);
        weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        dailyBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(DAILY_DATA);
            weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            dailyBtn.setBackgroundResource(R.color.colorPrimary);
        });
        weeklyBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(WEEKLY_DATA);
            weeklyBtn.setBackgroundResource(R.color.colorPrimary);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        monthlyBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(MONTHLY_DATA);
            weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.colorPrimary);
            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        chartViewBtn.setOnClickListener(v -> {
            chartViewLL.setVisibility(View.VISIBLE);
            listViewLL.setVisibility(View.GONE);
            chartViewBtn.setBackgroundResource(R.color.colorPrimary);
            listViewBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            populateChart(records);
        });
        listViewBtn.setOnClickListener(v -> {
            chartViewLL.setVisibility(View.GONE);
            listViewLL.setVisibility(View.VISIBLE);
            chartViewBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            listViewBtn.setBackgroundResource(R.color.colorPrimary);
        });


        displayRecords(DAILY_DATA);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayRecords(int dataFor) {
        showingDataFor = dataFor;
        fetchSales("");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void fetchSales(String key) {

        pDialog.show();
        firedb.collection(Constants.carwashDailySummary)
//                .whereEqualTo("userId", key)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
//                        Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        computeUserSales(new ArrayList<>());
                        return;
                    } else if (value.isEmpty()) {
                        computeUserSales(new ArrayList<>());
                        pDialog.dismiss();
                    } else {
                        List<CarWashDailySummary> allSales = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                CarWashDailySummary dailySales = doc.toObject(CarWashDailySummary.class);

                                Date date1 = null;
                                try {
                                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(dailySales.getDate());
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                int dateInt = (int) (date1.getTime() / 1000);
                                dailySales.setSortValue(dateInt);
                                String yrWkNo = dailySales.getYear() + GeneralMethods.getWeekNumber(dailySales.getDate());
                                dailySales.setYear_week(yrWkNo);

                                allSales.add(dailySales);
                            }
                        }
                        allSales.sort(Comparator.comparing(CarWashDailySummary::getSortValue).reversed());
                        computeUserSales(allSales);
                    }


//                    usersNamesAdapter.notifyDataSetChanged();
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void computeUserSales(List<CarWashDailySummary> allweeklySales) {
        records.clear();
        switch (showingDataFor) {
            case DAILY_DATA:
                for (CarWashDailySummary sale : allweeklySales) {
//                    sale.setUserName(sale.getDate());
                    sale.setTitle(sale.getDate());
                    records.add(sale);
                }
                break;
            case WEEKLY_DATA:
                for (CarWashDailySummary sale : allweeklySales) {
                    boolean found = false;
                    for (CarWashDailySummary dailySales : records) {
                        if (dailySales.getYear_week().equalsIgnoreCase(sale.getYear_week())) {//Exists
                            dailySales.setOverallTotal(dailySales.getOverallTotal() + sale.getOverallTotal());
                            dailySales.setLabourTotal(dailySales.getLabourTotal() + sale.getLabourTotal());
                            dailySales.setExpense(dailySales.getExpense() + sale.getExpense());
                            dailySales.setCars(dailySales.getCars() + sale.getCars());
                            dailySales.setMotorbikes(dailySales.getMotorbikes() + sale.getMotorbikes());
                            dailySales.setTrucks(dailySales.getTrucks() + sale.getTrucks());
                            dailySales.setBalTotal(dailySales.getBalTotal() + sale.getBalTotal());
                            dailySales.setOthers(dailySales.getOthers() + sale.getOthers());
                            dailySales.setDate(sale.getDate() + ", " + dailySales.getDate());
                            dailySales.getWaterReading().setUnits(dailySales.getWaterReading().getUnits() + sale.getWaterReading().getUnits());
                            dailySales.getDailyTokenReading().setUnits(dailySales.getDailyTokenReading().getUnits() + sale.getDailyTokenReading().getUnits());
//                            dailySales.setSortValue(sale.getSortValue());
                            dailySales.setCount(dailySales.getCount() + 1);
                            if (dailySales.getSortValue() > sale.getSortValue()) {
                                dailySales.setSortValue(sale.getSortValue());
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        int theWeeksNo = new GeneralMethods().getWeekNumber(sale.getDate());
                        String weekTitle = "";
                        if (todaysWeekNo == theWeeksNo) {
                            weekTitle = "This Week";
                        } else if (todaysWeekNo - theWeeksNo == 1) {
                            weekTitle = "Last Week";
                        } else {
                            weekTitle = "L.Wk but " + ((todaysWeekNo - theWeeksNo) - 1);
                        }
                        sale.setTitle(weekTitle);
                        records.add(sale);
                    }

                }
                break;
            case MONTHLY_DATA:
                for (CarWashDailySummary sale : allweeklySales) {
                    boolean found = false;
                    for (CarWashDailySummary dailySales : records) {
                        if (dailySales.getYear_month().equalsIgnoreCase(sale.getYear_month())) {//Exists
                            dailySales.setOverallTotal(dailySales.getOverallTotal() + sale.getOverallTotal());
                            dailySales.setLabourTotal(dailySales.getLabourTotal() + sale.getLabourTotal());
                            dailySales.setExpense(dailySales.getExpense() + sale.getExpense());
                            dailySales.setCars(dailySales.getCars() + sale.getCars());
                            dailySales.setMotorbikes(dailySales.getMotorbikes() + sale.getMotorbikes());
                            dailySales.setTrucks(dailySales.getTrucks() + sale.getTrucks());
                            dailySales.setBalTotal(dailySales.getBalTotal() + sale.getBalTotal());
                            dailySales.setOthers(dailySales.getOthers() + sale.getOthers());

                            dailySales.getWaterReading().setUnits(dailySales.getWaterReading().getUnits() + sale.getWaterReading().getUnits());
                            dailySales.getDailyTokenReading().setUnits(dailySales.getDailyTokenReading().getUnits() + sale.getDailyTokenReading().getUnits());
//                            dailySales.setDate(sale.getDate());
                            dailySales.setCount(dailySales.getCount() + 1);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //Add new
                        sale.setTitle(new GeneralMethods().getMonthName(Integer.parseInt(sale.getYear_month().substring(4))) + " " + sale.getYear());
                        records.add(sale);
                    }

                }
                break;
        }
        records.sort(Comparator.comparing(CarWashDailySummary::getSortValue).reversed());
        adapter.notifyDataSetChanged();
        populateChart(records);
        pDialog.dismiss();
    }

    public void populateChart(List<CarWashDailySummary> sales) {
        List<CarWashDailySummary> salesRev = new ArrayList<>();
        salesRev.addAll(sales);
        Collections.reverse(salesRev);
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (CarWashDailySummary dailySales : salesRev) {
            yVals1.add(new BarEntry(salesRev.indexOf(dailySales), dailySales.getBalTotal()));
            xAxisLabel.add(shortenForChart(dailySales.getTitle()));
        }

        BarDataSet set1;
        String desc = "";
        switch (showingDataFor) {
            case DAILY_DATA:
                desc = "Daily sales";
                break;
            case WEEKLY_DATA:
                desc = "Weekly Sales";
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