package com.munit.m_unitapp.UI.CARWASH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.ADAPTERS.CarwashExpenseAdapter;
import com.munit.m_unitapp.ADAPTERS.WeeklySalesFragAdapter;
import com.munit.m_unitapp.MODELS.CarWashDailySummary;
import com.munit.m_unitapp.MODELS.CarwashExpenseModel;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SummaryActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private Spinner reportsSpner;
    List<String> reportsSpinnerArray = new ArrayList<>();
    String reportFor;
    private ArrayAdapter<String> reportSpnerAdapter;
    TextView weekRangeTV, weekTitleTv, userNameTV, userTillPayTV, userCashPayTV, weeklyBtn, monthlyBtn, yealyBtn;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    int todaysWeekNo;
    int currentDisplayedWeek, currentDisplayedMonth, currentDisplayedYear;
    List<String> weekDates = new ArrayList<>();
    List<CarwashExpenseModel> displayingSummary = new ArrayList<>();
    CarwashExpenseModel totalSummary = new CarwashExpenseModel();
    int year, month, day;
    String todate, DateDisplaying;
    SweetAlertDialog sweetAlertDialog;
    FirebaseFirestore firedb;
    ImageView weeklyPrevNav, weeklyNextNav;
    CarwashExpenseAdapter carwashExpenseAdapter;
    RecyclerView weeklySummary;
    RelativeLayout refreshBtn;
    List<CarWashDailySummary> allExpenses = new ArrayList<>();
    List<DailySales> allSales;

    final int WEEKLY_DATA = 1;
    final int MONTHLY_DATA = 2;
    final int YEARLY_DATA = 3;

    int showingDataFor = WEEKLY_DATA;
    String queryfield = "year_week";
    String type = "Expenses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary2);

        getSupportActionBar().hide();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Fetching Data....");
        sweetAlertDialog.setCancelable(false);

        firedb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firedb.setFirestoreSettings(settings);

        reportsSpner = findViewById(R.id.reportsSpner);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        weeklyBtn = findViewById(R.id.weeklyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        yealyBtn = findViewById(R.id.yealyBtn);

        weeklyBtn.setBackgroundResource(R.color.colorPrimary);
        monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);

        weeklyBtn.setOnClickListener(v -> {
            sweetAlertDialog.show();
            currentDisplayedYear = year;
            currentDisplayedWeek = new GeneralMethods().getWeekNumber(todate);
            currentDisplayedMonth = month;
            displayRecords(WEEKLY_DATA);
            weeklyBtn.setBackgroundResource(R.color.colorPrimary);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        monthlyBtn.setOnClickListener(v -> {
            sweetAlertDialog.show();
            currentDisplayedYear = year;
            currentDisplayedWeek = new GeneralMethods().getWeekNumber(todate);
            currentDisplayedMonth = month;
            displayRecords(MONTHLY_DATA);
            weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.colorPrimary);
            yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        yealyBtn.setOnClickListener(v -> {
            sweetAlertDialog.show();
            currentDisplayedYear = year;
            currentDisplayedWeek = new GeneralMethods().getWeekNumber(todate);
            currentDisplayedMonth = month;
            displayRecords(YEARLY_DATA);

            weeklyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            yealyBtn.setBackgroundResource(R.color.colorPrimary);
        });

        reportsSpinnerArray.add("Expenses");
        reportsSpinnerArray.add("Commissions");
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
                if (reportFor.equalsIgnoreCase("Expenses")) {
                    type = "Expenses";
                } else {
                    type = "Commissions";
                }

                computeWeeklySales(allExpenses);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

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

        carwashExpenseAdapter = new CarwashExpenseAdapter(weekRangeTV.getContext(), displayingSummary);
//        allDailySalesAdapter.setListener(this);
        carwashExpenseAdapter.setSelectedUserName("Total");
        weeklySummary.setAdapter(carwashExpenseAdapter);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;
        currentDisplayedWeek = new GeneralMethods().getWeekNumber(todate);
        todaysWeekNo = 0;
        currentDisplayedMonth = month;
        currentDisplayedYear = year;
//        fetchAllWeeklyData();
        loadData(currentDisplayedYear + "" + currentDisplayedWeek);
    }
    private void displayRecords(int dataFor) {
        showingDataFor = dataFor;
        switch (showingDataFor) {
            case WEEKLY_DATA:
                queryfield = "year_week";
                weekRangeTV.setVisibility(View.VISIBLE);
                weekTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                loadData(currentDisplayedYear + "" + currentDisplayedWeek);
                break;
            case MONTHLY_DATA:
                weekRangeTV.setVisibility(View.GONE);
                weekTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
                queryfield = "year_month";
                loadData(currentDisplayedYear + "" + month);
                break;
            case YEARLY_DATA:
                weekRangeTV.setVisibility(View.GONE);
                weekTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
                queryfield = "year";
                loadData(currentDisplayedYear + "");
                break;
        }

    }
    public void loadData(String key) {
        if (showingDataFor == WEEKLY_DATA) {
            fetchDatesInWeek(currentDisplayedWeek);
        }
        if(weekDates.size()>1){
            String startYr = new GeneralMethods().getDateParts(weekDates.get(0), "yy");
            String endYr = new GeneralMethods().getDateParts(weekDates.get(weekDates.size()-1), "yy");
            if(startYr.equals(endYr)){
                fetchWeeklyData(key);
            }else {
                fetchWeeklyDataWeekBtnTwoYears(startYr+"52", endYr+"1");
            }
        }else {
            fetchWeeklyData(key);
        }

    }

    public void fetchDatesInWeek(int weekNo) {
        weekDates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        cal.setMinimalDaysInFirstWeek(1);
        cal.set(Calendar.YEAR, currentDisplayedYear);
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

    public void fetchWeeklyData(String key) {
        totalSummary = new CarwashExpenseModel();
//        displayingSummary.setSelectedUserName("Total");
        sweetAlertDialog.show();
        firedb.collection(Constants.carwashDailySummary)
                .whereEqualTo(queryfield, key)
//                .whereArrayContains(queryfield, key)

                .addSnapshotListener((value, e) -> {
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    allExpenses = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("date") != null) {
                            CarWashDailySummary summary = doc.toObject(CarWashDailySummary.class);
                            allExpenses.add(summary);
                        }
                    }
                    computeWeeklySales(allExpenses);
                });
    }


    public void fetchWeeklyDataWeekBtnTwoYears(String startKey, String endKey) {
        totalSummary = new CarwashExpenseModel();
//        allDailySalesAdapter.setSelectedUserName("Total");
        sweetAlertDialog.show();
        firedb.collection(Constants.carwashDailySummary)
                .whereIn("year_week", Arrays.asList(startKey, endKey))
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    allExpenses = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("date") != null) {
                            CarWashDailySummary dailySales = doc.toObject(CarWashDailySummary.class);
                            allExpenses.add(dailySales);
                        }
                    }
                    computeWeeklySales(allExpenses);
                });
    }

    private void computeWeeklySales(List<CarWashDailySummary> allExpenseSales) {
        displayingSummary.clear();

        for (CarWashDailySummary summary : allExpenseSales) {
            if(summary.getExpenseTotals() != null){
                boolean found = false;

                List<CarwashExpenseModel> list = new ArrayList<>();
                if(type.equalsIgnoreCase("Commissions")){
                    list.addAll(summary.getAttentantsTotals());
                }else{
                    list.addAll(summary.getExpenseTotals());
                }

                for (CarwashExpenseModel expenseModel: list){

                    for (CarwashExpenseModel expenseModel1 : displayingSummary) {
                        if (expenseModel.getName().equalsIgnoreCase(expenseModel1.getName())) { //add Totals
                            expenseModel1.setTotal(expenseModel.getTotal() + expenseModel1.getTotal());
                            expenseModel1.setCount(expenseModel1.getCount() + 1);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {//Insert New
                        displayingSummary.add(expenseModel);
                    }

                }
            }



//            if (totalSummary.getTotal() == 0) {
//                totalSummary.setComputer_service(sale.getComputer_service());
//                totalSummary.setComputer_sales(sale.getComputer_sales());
//                totalSummary.setMovies(sale.getMovies());
//                totalSummary.setGames(sale.getGames());
//                totalSummary.setMpesaTill(sale.getMpesaTill());
//                totalSummary.setCashPayment(sale.getCashPayment());
//                totalSummary.setTotal(sale.getTotal());
//
//            } else {
//                totalSummary.setComputer_service(totalSummary.getComputer_service() + sale.getComputer_service());
//                totalSummary.setComputer_sales(totalSummary.getComputer_sales() + sale.getComputer_sales());
//                totalSummary.setMovies(totalSummary.getMovies() + sale.getMovies());
//                totalSummary.setGames(totalSummary.getGames() + sale.getGames());
//                totalSummary.setMpesaTill(totalSummary.getMpesaTill() + sale.getMpesaTill());
//                totalSummary.setCashPayment(totalSummary.getCashPayment() + sale.getCashPayment());
//                totalSummary.setTotal(totalSummary.getTotal() + sale.getTotal());
//            }
        }
//        totalSummary.setUserName("Total");
//        displayingSummary.add(totalSummary);
        carwashExpenseAdapter.notifyDataSetChanged();

        //Set Dates Display
        String weekTitle = "";
        switch (showingDataFor) {
            case WEEKLY_DATA:
                if (todaysWeekNo == 0) {
                    weekTitle = "This Week";
                } else if (todaysWeekNo == -1) {
                    weekTitle = "Last Week";
                } else {
                    int disp = todaysWeekNo * -1;
                    weekTitle = "Last Week but " + (disp - 1);
                }
                if (todaysWeekNo == 0 && currentDisplayedYear == year) {
                    weeklyNextNav.setVisibility(View.GONE);
                } else {
                    weeklyNextNav.setVisibility(View.VISIBLE);
                }
                weekRangeTV.setText(weekDates.get(0) + " - " + weekDates.get(weekDates.size() - 1));
                break;
            case MONTHLY_DATA:
                weekTitle = new GeneralMethods().getMonthName(currentDisplayedMonth) + ", " + currentDisplayedYear;

                if (currentDisplayedMonth == month && currentDisplayedYear == year) {
                    weeklyNextNav.setVisibility(View.GONE);
                } else {
                    weeklyNextNav.setVisibility(View.VISIBLE);
                }
                break;
            case YEARLY_DATA:
                weekTitle = "" + currentDisplayedYear;
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
    private void fetchPreviousData() {
        todaysWeekNo--;
        switch (showingDataFor) {
            case WEEKLY_DATA:
                currentDisplayedWeek = currentDisplayedWeek - 1;
                if (currentDisplayedWeek == 0) {
                    currentDisplayedYear--;
                    currentDisplayedWeek = 52;
                }

                fetchDatesInWeek(currentDisplayedWeek);
                loadData(currentDisplayedYear + "" + currentDisplayedWeek);
                break;
            case MONTHLY_DATA:
                loadData(getPrevMonthYearKey());
                break;
            case YEARLY_DATA:
                currentDisplayedYear = currentDisplayedYear - 1;
                loadData("" + currentDisplayedYear);
                break;
        }
    }

    private void fetchNextData() {
        todaysWeekNo++;
        switch (showingDataFor) {
            case WEEKLY_DATA:
                currentDisplayedWeek = currentDisplayedWeek + 1;
                if (currentDisplayedWeek == 53) {
                    currentDisplayedWeek = 1;
                    currentDisplayedYear++;
                }

//                fetchDatesInWeek(currentDisplayedWeek);
                loadData(currentDisplayedYear + "" + currentDisplayedWeek);
                break;
            case MONTHLY_DATA:
                loadData(getNextMonthYearKey());
                break;
            case YEARLY_DATA:
                currentDisplayedYear = currentDisplayedYear + 1;
                loadData("" + currentDisplayedYear);
                break;
        }

    }
    public String getPrevMonthYearKey() {
        if (currentDisplayedMonth == 1) {
            currentDisplayedYear = currentDisplayedYear - 1;
            currentDisplayedMonth = 12;

        } else {
            currentDisplayedMonth = currentDisplayedMonth - 1;
        }

        return (currentDisplayedYear) + "" + currentDisplayedMonth;
    }

    public String getNextMonthYearKey() {
        if (currentDisplayedMonth == 12) {
            currentDisplayedYear = currentDisplayedYear + 1;
            currentDisplayedMonth = 1;
        } else {
            currentDisplayedMonth = currentDisplayedMonth + 1;
        }
        return (currentDisplayedYear) + "" + currentDisplayedMonth;
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
//                    getDailySalesForDays();
                });
    }
}