package com.munit.m_unitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.ADAPTERS.WeeklySalesFragAdapter;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SummaryActivity extends AppCompatActivity implements AllDailySalesAdapter.ClickListener {
    private ImageView back_arrow;
    private ViewPager dailyPagerAll;
    TextView weekRangeTV, weekTitleTv, userNameTV, userTillPayTV, userCashPayTV, weeklyBtn, monthlyBtn, yealyBtn;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    int todaysWeekNo;
    int currentDisplayedWeek, currentDisplayedMonth, currentDisplayedYear;
    List<String> weekDates = new ArrayList<>();
    List<DailySales> weeklySalesSammary = new ArrayList<>();
    DailySales totalSummary = new DailySales();
    int year, month, day;
    String todate, DateDisplaying;
    SweetAlertDialog sweetAlertDialog;
    FirebaseFirestore firedb;
    ImageView weeklyPrevNav, weeklyNextNav;
    AllDailySalesAdapter allDailySalesAdapter;
    RecyclerView weeklySummary;

    private WeeklySalesFragAdapter weeklySalesFragAdapter;
    List<List<DailySales>> allUsersWeeklySales = new ArrayList<>();
    List<DailySales> allSales;

    final int WEEKLY_DATA = 1;
    final int MONTHLY_DATA = 2;
    final int YEARLY_DATA = 3;

    int showingDataFor = WEEKLY_DATA;
    String queryfield = "year_week";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        getSupportActionBar().hide();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Fetching Data....");
        sweetAlertDialog.setCancelable(false);
        firedb = FirebaseFirestore.getInstance();


        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        dailyPagerAll = findViewById(R.id.dailyPagerAll);

        weeklyBtn = findViewById(R.id.weeklyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        yealyBtn = findViewById(R.id.yealyBtn);

        weeklyBtn.setBackgroundResource(R.color.colorPrimary);
        monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);

        weeklyBtn.setOnClickListener(v -> {
            sweetAlertDialog.show();
            displayRecords(WEEKLY_DATA);
            weeklyBtn.setBackgroundResource(R.color.colorPrimary);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        monthlyBtn.setOnClickListener(v -> {
            sweetAlertDialog.show();
            displayRecords(MONTHLY_DATA);
            weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.colorPrimary);
            yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        yealyBtn.setOnClickListener(v -> {
            sweetAlertDialog.show();
            displayRecords(YEARLY_DATA);

            weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            yealyBtn.setBackgroundResource(R.color.colorPrimary);
        });

        userNameTV = findViewById(R.id.userNameTV);
        userTillPayTV = findViewById(R.id.userTillPayTV);
        userCashPayTV = findViewById(R.id.userCashPayTV);

        weekRangeTV = findViewById(R.id.weekRangeTV);
        weekTitleTv = findViewById(R.id.weekTitleTv);
        weeklyPrevNav = findViewById(R.id.weeklyPrevNav);
        weeklyPrevNav.setOnClickListener(v -> {
            fetchPreviousData();
        });

        weeklyNextNav = findViewById(R.id.weeklyNextNav);
        weeklyNextNav.setOnClickListener(v -> {
            fetchNextData();
        });
        weeklySummary = findViewById(R.id.weeklySummary);
        weeklySummary.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        weeklySummary.smoothScrollToPosition(0);

        allDailySalesAdapter = new AllDailySalesAdapter(weekRangeTV.getContext(), weeklySalesSammary);
        allDailySalesAdapter.setListener(this);
        allDailySalesAdapter.setSelectedUserName("Total");
        weeklySummary.setAdapter(allDailySalesAdapter);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;
        todaysWeekNo = currentDisplayedWeek = new GeneralMethods().getWeekNumber(todate);
        currentDisplayedMonth = month;
        currentDisplayedYear = year;
        fetchAllWeeklyData();
        loadData(year+""+todaysWeekNo);
    }
    public void fetchAllWeeklyData() {
        sweetAlertDialog.show();
        firedb.collection(Constants.dailySalesPath)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    allSales = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("date") != null) {
                            DailySales dailySales = doc.toObject(DailySales.class);
                            allSales.add(dailySales);
                        }
                    }
                    getDailySalesForDays();
                });
    }
    public void generateSales() {
        weeklySalesFragAdapter = new WeeklySalesFragAdapter(getSupportFragmentManager(), allUsersWeeklySales);
        dailyPagerAll.setAdapter(weeklySalesFragAdapter);
        dailyPagerAll.setPageTransformer(true, new CubeOutTransformer());
        dailyPagerAll.setCurrentItem(6);
        sweetAlertDialog.dismiss();
    }
    public void getDailySalesForDays() {
        allUsersWeeklySales = new ArrayList<>();
        Calendar cal;
        for (int i = 0; i < 7; i++) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            String Displayingdate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

            String dayOftheWeek = "";
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            switch (dow) {
                case 1:
                    dayOftheWeek = "SUNDAY";
                    break;
                case 2:
                    dayOftheWeek = "MONDAY";
                    break;
                case 3:
                    dayOftheWeek = "TUESDAY";
                    break;
                case 4:
                    dayOftheWeek = "WEDNESDAY";
                    break;
                case 5:
                    dayOftheWeek = "THURSDAY";
                    break;
                case 6:
                    dayOftheWeek = "FRIDAY";
                    break;
                case 7:
                    dayOftheWeek = "SATURDAY";
                    break;
                default:
                    System.out.println("GO To Hell....");
            }

            allUsersWeeklySales.add(getDailySalesFromAll(Displayingdate, dayOftheWeek));
        }
        Collections.reverse(allUsersWeeklySales);
        generateSales();
    }

    public List<DailySales> getDailySalesFromAll(String date, String day) {
        List<DailySales> daySales = new ArrayList<>();
        for (DailySales sales : allSales) {
            if (sales.getDate().equalsIgnoreCase(date)) {
                sales.setDate(day + "(" + sales.getDate() + ")");
                daySales.add(sales);
            }
        }
        Collections.reverse(daySales);
        return daySales;
    }
    private void fetchPreviousData() {
        switch (showingDataFor) {
            case WEEKLY_DATA:
                currentDisplayedWeek = currentDisplayedWeek - 1;
                fetchDatesInWeek(currentDisplayedWeek);
                loadData(year+""+currentDisplayedWeek);
                break;
            case MONTHLY_DATA:
                loadData(getPrevMonthYearKey());
                break;
            case YEARLY_DATA:
                currentDisplayedYear = currentDisplayedYear -1;
                loadData(""+currentDisplayedYear);
                break;
        }
    }

    private void fetchNextData() {
        switch (showingDataFor) {
            case WEEKLY_DATA:
                currentDisplayedWeek = currentDisplayedWeek + 1;
                fetchDatesInWeek(currentDisplayedWeek);
                loadData(year+""+currentDisplayedWeek);
                break;
            case MONTHLY_DATA:
                loadData(getNextMonthYearKey());
                break;
            case YEARLY_DATA:
                currentDisplayedYear = currentDisplayedYear +1;
                loadData(""+currentDisplayedYear);
                break;
        }

    }

    public void loadData(String key) {
        if (showingDataFor == WEEKLY_DATA) {
            fetchDatesInWeek(currentDisplayedWeek);
        }
        fetchWeeklyData(key);
    }

    private void displayRecords(int dataFor) {
        showingDataFor = dataFor;
        switch (showingDataFor) {
            case WEEKLY_DATA:
                queryfield = "year_week";
                weekRangeTV.setVisibility(View.VISIBLE);

                weekTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                loadData(year+""+todaysWeekNo);
                break;
            case MONTHLY_DATA:
                weekRangeTV.setVisibility(View.GONE);
                weekTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
                queryfield = "year_month";
                loadData(year+""+month);
                break;
            case YEARLY_DATA:
                weekRangeTV.setVisibility(View.GONE);
                weekTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                queryfield = "year";
                loadData(year+"");
                break;
        }

    }

    public void fetchWeeklyData(String key) {
        totalSummary = new DailySales();
        allDailySalesAdapter.setSelectedUserName("Total");
        sweetAlertDialog.show();
        firedb.collection(Constants.dailySalesPath)
                .whereEqualTo(queryfield, key)
//                .whereArrayContains(queryfield, key)

                .addSnapshotListener((value, e) -> {
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<DailySales> allweeklySales = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("date") != null) {
                            DailySales dailySales = doc.toObject(DailySales.class);
                            allweeklySales.add(dailySales);
                        }
                    }
                    computeWeeklySales(allweeklySales);
                });
    }

    private void computeWeeklySales(List<DailySales> allweeklySales) {
        weeklySalesSammary.clear();
        for (DailySales sale : allweeklySales) {
            boolean found = false;
            for (DailySales saleSummary : weeklySalesSammary) {
                if (saleSummary.getUserId().equalsIgnoreCase(sale.getUserId())) { //add Totals
                    saleSummary.setComputer_service(saleSummary.getComputer_service() + sale.getComputer_service());
                    saleSummary.setComputer_sales(saleSummary.getComputer_sales() + sale.getComputer_sales());
                    saleSummary.setMovies(saleSummary.getMovies() + sale.getMovies());
                    saleSummary.setGames(saleSummary.getGames() + sale.getGames());
                    saleSummary.setMpesaTill(saleSummary.getMpesaTill() + sale.getMpesaTill());
                    saleSummary.setCashPayment(saleSummary.getCashPayment() + sale.getCashPayment());
                    saleSummary.setTotal(saleSummary.getTotal() + sale.getTotal());
                    saleSummary.setCount(saleSummary.getCount() + sale.getCount());
                    found = true;
                    break;
                }
            }

            if (!found) {//Insert New
                weeklySalesSammary.add(sale);
            }

            if (totalSummary.getTotal() == 0) {
                totalSummary.setComputer_service(sale.getComputer_service());
                totalSummary.setComputer_sales(sale.getComputer_sales());
                totalSummary.setMovies(sale.getMovies());
                totalSummary.setGames(sale.getGames());
                totalSummary.setMpesaTill(sale.getMpesaTill());
                totalSummary.setCashPayment(sale.getCashPayment());
                totalSummary.setTotal(sale.getTotal());

            } else {
                totalSummary.setComputer_service(totalSummary.getComputer_service() + sale.getComputer_service());
                totalSummary.setComputer_sales(totalSummary.getComputer_sales() + sale.getComputer_sales());
                totalSummary.setMovies(totalSummary.getMovies() + sale.getMovies());
                totalSummary.setGames(totalSummary.getGames() + sale.getGames());
                totalSummary.setMpesaTill(totalSummary.getMpesaTill() + sale.getMpesaTill());
                totalSummary.setCashPayment(totalSummary.getCashPayment() + sale.getCashPayment());
                totalSummary.setTotal(totalSummary.getTotal() + sale.getTotal());
            }
        }
        totalSummary.setUserName("Total");
        weeklySalesSammary.add(totalSummary);
        allDailySalesAdapter.notifyDataSetChanged();

        //Set Dates Display
        String weekTitle = "";
        switch (showingDataFor){
            case WEEKLY_DATA:
                if (currentDisplayedWeek == todaysWeekNo) {
                    weekTitle = "This Week";
                } else if (todaysWeekNo - currentDisplayedWeek == 1) {
                    weekTitle = "Last Week";
                } else {
                    weekTitle = "Last Week but " + ((todaysWeekNo - currentDisplayedWeek) - 1);
                }
                if (currentDisplayedWeek == todaysWeekNo && currentDisplayedYear == year) {
                    weeklyNextNav.setVisibility(View.GONE);
                } else {
                    weeklyNextNav.setVisibility(View.VISIBLE);
                }
                weekRangeTV.setText(weekDates.get(0) + " - " + weekDates.get(weekDates.size() - 1));
                break;
            case MONTHLY_DATA:
                weekTitle =new GeneralMethods().getMonthName(currentDisplayedMonth) + ", "+ currentDisplayedYear;

                if (currentDisplayedMonth == month && currentDisplayedYear == year) {
                    weeklyNextNav.setVisibility(View.GONE);
                } else {
                    weeklyNextNav.setVisibility(View.VISIBLE);
                }
                break;
            case YEARLY_DATA:
                weekTitle =""+ currentDisplayedYear;
                if (currentDisplayedYear == year) {
                    weeklyNextNav.setVisibility(View.GONE);
                } else {
                    weeklyNextNav.setVisibility(View.VISIBLE);
                }
                break;
        }
        weekTitleTv.setText(weekTitle);
        sweetAlertDialog.dismiss();
    }

    public void fetchDatesInWeek(int weekNo) {
        weekDates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        for (int i = 2; i < 8; i++) {
            cal.set(Calendar.DAY_OF_WEEK, i);
            if (!cal.after(today)) {
                weekDates.add(sdf.format(cal.getTime()));
            } else {
                break;
            }
        }
        // Add Sunday
        cal.add(Calendar.DATE, 1);
        if (!cal.after(today)) {
            weekDates.add(sdf.format(cal.getTime()));
        }
    }

    @Override
    public void showCashBreakDown(DailySales dailySales, boolean refreshRV) {
//        userNameTV, userTillPayTV, userCashPayTV
        userNameTV.setText(dailySales.getUserName());
        userTillPayTV.setText("Till Payment: Ksh. " + dailySales.getMpesaTill());
        userCashPayTV.setText("Cash Payment: Ksh. " + dailySales.getCashPayment());
        allDailySalesAdapter.setSelectedUserName(dailySales.getUserName());
        if (refreshRV)
            allDailySalesAdapter.notifyDataSetChanged();
    }

    public String getPrevMonthYearKey() {
        if (currentDisplayedMonth == 1) {
            currentDisplayedYear = currentDisplayedYear - 1;
            currentDisplayedMonth = 12;

        } else{
            currentDisplayedMonth = currentDisplayedMonth - 1;
        }

        return (currentDisplayedYear) + ""+ currentDisplayedMonth;
    }

    public String getNextMonthYearKey() {
        if (currentDisplayedMonth == 12) {
            currentDisplayedYear = currentDisplayedYear + 1;
            currentDisplayedMonth = 1;
        } else{
            currentDisplayedMonth= currentDisplayedMonth + 1;
        }
        return (currentDisplayedYear) + ""+ currentDisplayedMonth;
    }



}