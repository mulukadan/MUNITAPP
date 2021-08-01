package com.munit.m_unitapp;

import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.ADAPTERS.DailySalesFragAdapter;
import com.munit.m_unitapp.ADAPTERS.WeeklySalesFragAdapter;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.TOOLS.Constants;
import com.munit.m_unitapp.TOOLS.GeneralMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CashInActivity extends AppCompatActivity implements AllDailySalesAdapter.ClickListener {
    private ImageView back_arrow;
    private Calendar calendar;
    private int year, month, day;
    String todate;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    FloatingActionMenu fab;
    FloatingActionButton addSaleBtn;
    String USERID;
    List<User> users = new ArrayList<>();
    User userdb = new User();

    List<List<DailySales>> allUsersWeeklySales = new ArrayList<>();
    Gson gson;
    SweetAlertDialog pDialog;
    FirebaseFirestore firedb;
    List<DailySales> allSales;
    Button summaryBtn;

    RecyclerView weeklySummary;
    AllDailySalesAdapter allDailySalesAdapter;
    List<DailySales> userSales = new ArrayList<>();
    TextView daysDate, compServTV, compSalesTV, GamesTV, moviesTV, mpesaTillTV, cashTV, totalPayTV, weeklyBtn, monthlyBtn, dailyBtn;
    ;

    DailySales dailySales;
    int todaysWeekNo;
    final int DAILY_DATA = 1;
    final int WEEKLY_DATA = 2;
    final int MONTHLY_DATA = 3;

    int showingDataFor = DAILY_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);
        getSupportActionBar().hide();

        gson = new Gson();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firedb = FirebaseFirestore.getInstance();

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

        allDailySalesAdapter = new AllDailySalesAdapter(weeklySummary.getContext(), userSales);
        allDailySalesAdapter.setListener(this);
        allDailySalesAdapter.setSelectedUserName("Total");
        weeklySummary.setAdapter(allDailySalesAdapter);

        daysDate = findViewById(R.id.daysDate);
        compServTV = findViewById(R.id.compServTV);
        compSalesTV = findViewById(R.id.compSalesTV);
        GamesTV = findViewById(R.id.GamesTV);
        moviesTV = findViewById(R.id.moviesTV);
        mpesaTillTV = findViewById(R.id.mpesaTillTV);
        mpesaTillTV = findViewById(R.id.mpesaTillTV);
        cashTV = findViewById(R.id.cashTV);
        totalPayTV = findViewById(R.id.totalPayTV);
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
        summaryBtn = findViewById(R.id.summaryBtn);
        summaryBtn.setOnClickListener((view) -> {
            Intent intent = new Intent(CashInActivity.this, SummaryActivity.class);
            startActivity(intent);
        });
        fab = findViewById(R.id.fab);
        addSaleBtn = findViewById(R.id.addSaleBtn);
        addSaleBtn.setOnClickListener(v -> {
            fab.close(true);
            Intent addSales = new Intent(this, AddSales.class);
            Gson gson = new Gson();
            String userJson = gson.toJson(userdb);
            addSales.putExtra("userJson", userJson);
            startActivity(addSales);
        });
        fetchUsers();
    }

    private void displayRecords(int dataFor) {
        showingDataFor = dataFor;
        switch (showingDataFor) {
            case DAILY_DATA:
                fetchUserSales(USERID);
                break;
            case WEEKLY_DATA:
                fetchUserSales(USERID);
                break;
            case MONTHLY_DATA:
                fetchUserSales(USERID);
                break;
        }
    }
    public void getDailySale() {
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.show();
        daysDate.setText("Today Sales (" + todate + ")");
        String docId = todate.replace("/", "-") + ":" + USERID;
        DocumentReference docRef = firedb.collection(Constants.dailySalesPath).document(docId);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                pDialog.setContentText(e.getMessage());
                pDialog.setConfirmClickListener(sweetAlertDialog -> {
                    pDialog.dismiss();
                });
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                dailySales = snapshot.toObject(DailySales.class);
            } else {
                dailySales = new DailySales();
            }
            compServTV.setText("Ksh. " + dailySales.getComputer_service());
            compSalesTV.setText("Ksh. " + dailySales.getComputer_sales());
            moviesTV.setText("Ksh. " + dailySales.getMovies());
            GamesTV.setText("Ksh. " + dailySales.getGames());
            totalPayTV.setText("Ksh. " + dailySales.getTotal());
            mpesaTillTV.setText("Ksh. " + dailySales.getMpesaTill());
            cashTV.setText("Ksh. " + dailySales.getCashPayment());
            pDialog.dismiss();
        });
    }

    public void fetchUserSales(String key) {
        allDailySalesAdapter.setSelectedUserName("Total");
        pDialog.show();
        firedb.collection(Constants.dailySalesPath)
                .whereEqualTo("userId", key)
                .orderBy("sortValue", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if(value.isEmpty()){
                        pDialog.dismiss();
                    }

                    List<DailySales> allweeklySales = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("date") != null) {
                            DailySales dailySales = doc.toObject(DailySales.class);
                            dailySales.setCount(1);
                            allweeklySales.add(dailySales);
                        }
                    }
                    computeUserSales(allweeklySales);
                });

//        firedb.collection(Constants.dailySalesPath)
//                .whereEqualTo("userId", key)
//                .orderBy("sortValue", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    List<String> list = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        list.add(document.getId());
//                    }
//                    String k = list.toString();
//                } else {
////                    Log.d(TAG, "Error getting documents: ", task.getException());
//                    String h =  task.getException().getMessage();
//                    int i = 0;
//
//                }
//            }
//        });


    }

    private void computeUserSales(List<DailySales> allweeklySales) {
        userSales.clear();
        switch (showingDataFor) {
            case DAILY_DATA:
                for (DailySales sale : allweeklySales) {
                    sale.setUserName(sale.getDate());
                    userSales.add(sale);
                }
                break;
            case WEEKLY_DATA:
                for (DailySales sale : allweeklySales) {
                    boolean found = false;
                    for(DailySales dailySales : userSales){
                        if(dailySales.getYear_week().equalsIgnoreCase(sale.getYear_week())){//Exists
                            dailySales.setComputer_service(dailySales.getComputer_service() + sale.getComputer_service());
                            dailySales.setComputer_sales(dailySales.getComputer_sales() + sale.getComputer_sales());
                            dailySales.setMovies(dailySales.getMovies() + sale.getMovies());
                            dailySales.setGames(dailySales.getGames() + sale.getGames());
                            dailySales.setMpesaTill(dailySales.getMpesaTill() + sale.getMpesaTill());
                            dailySales.setCashPayment(dailySales.getCashPayment() + sale.getCashPayment());
                            dailySales.setTotal(dailySales.getTotal() + sale.getTotal());
                            dailySales.setCount(dailySales.getCount() + sale.getCount());
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        int theWeeksNo = new GeneralMethods().getWeekNumber(sale.getDate());
                        String weekTitle = "";
                        if(todaysWeekNo == theWeeksNo){
                            weekTitle = "This Week";
                        }else if (todaysWeekNo - theWeeksNo == 1) {
                            weekTitle = "Last Week";
                        } else {
                            weekTitle = "Last Week but " + ((todaysWeekNo - theWeeksNo) - 1);
                        }
                        sale.setUserName(weekTitle);
                        userSales.add(sale);
                    }
                }
                break;
            case MONTHLY_DATA:
                for (DailySales sale : allweeklySales) {
                    boolean found = false;
                    for(DailySales dailySales : userSales){
                        if(dailySales.getYear_month().equalsIgnoreCase(sale.getYear_month())){//Exists
                            dailySales.setComputer_service(dailySales.getComputer_service() + sale.getComputer_service());
                            dailySales.setComputer_sales(dailySales.getComputer_sales() + sale.getComputer_sales());
                            dailySales.setMovies(dailySales.getMovies() + sale.getMovies());
                            dailySales.setGames(dailySales.getGames() + sale.getGames());
                            dailySales.setMpesaTill(dailySales.getMpesaTill() + sale.getMpesaTill());
                            dailySales.setCashPayment(dailySales.getCashPayment() + sale.getCashPayment());
                            dailySales.setTotal(dailySales.getTotal() + sale.getTotal());
                            dailySales.setCount(dailySales.getCount() + sale.getCount());
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        //Add new
                        sale.setUserName(new GeneralMethods().getMonthName(Integer.parseInt(sale.getYear_month().substring(4))) +" "+ sale.getYear());
                        userSales.add(sale);
                    }

                }
                break;
        }

        allDailySalesAdapter.notifyDataSetChanged();
        pDialog.dismiss();
    }

    public void fetchUsers() {
        SweetAlertDialog pDialog = new SweetAlertDialog(CashInActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    User u = postSnapshot.getValue(User.class);
                    if (u.getUsername().equals(user.getEmail())) {
                        userdb = u;
                        USERID = "" + userdb.getId();
                    }
                    users.add(u);
                }
                if(userdb.getLevel()>2){
                    summaryBtn.setVisibility(View.GONE);
                }
                pDialog.dismiss();
                getDailySale();
                fetchUserSales(USERID);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(CashInActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }

    @Override
    public void showCashBreakDown(DailySales dailySales, boolean refreshRV) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUsers();
    }
}
