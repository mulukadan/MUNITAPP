package com.munit.m_unitapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.munit.m_unitapp.ADAPTERS.DailySalesFragAdapter;
import com.munit.m_unitapp.ADAPTERS.SalesCategoryAdapter;
import com.munit.m_unitapp.ADAPTERS.WeeklySalesFragAdapter;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CashInActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private ViewPager viewPager;
    private ViewPager dailyPagerAll;
    private DailySalesFragAdapter dailySalesFragAdapter;
    private WeeklySalesFragAdapter weeklySalesFragAdapter;
    String jsonString = "";
    JsonArray objArray;
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

    List<DailySales> DailySales5 = new ArrayList<>();
    List<List<DailySales>> allUsersWeeklySales = new ArrayList<>();
    Gson gson;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);
        getSupportActionBar().hide();
        gson = new Gson();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Fetching Data....");
        pDialog.setCancelable(false);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "-" + month + "-" + year;
        fetchUsers();


        fetchData();
//        generateSales();

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });


//        viewPager = findViewById(R.id.dailyPager);
        dailyPagerAll = findViewById(R.id.dailyPagerAll);

        fab = findViewById(R.id.fab);
        addSaleBtn = findViewById(R.id.addSaleBtn);
        addSaleBtn.setOnClickListener(v -> {
            fab.close(true);
            Intent addSales = new Intent(this, AddSales.class);
            Gson gson = new Gson();
            String userJson = gson.toJson(userdb);
            addSales.putExtra("userJson",userJson);
            startActivity(addSales);
        });

    }

    public void generateSales() {
//        Collections.reverse(DailySales5);
//        if (DailySales5.size() > 0) {
//            dailySalesFragAdapter = new DailySalesFragAdapter(getSupportFragmentManager(), DailySales5);
//            viewPager.setAdapter(dailySalesFragAdapter);
//            viewPager.setPageTransformer(true, new CubeOutTransformer());
//            viewPager.setCurrentItem(DailySales5.size() - 1);
//        }
        weeklySalesFragAdapter = new WeeklySalesFragAdapter(getSupportFragmentManager(), allUsersWeeklySales);
        dailyPagerAll.setAdapter(weeklySalesFragAdapter);
        dailyPagerAll.setPageTransformer(true, new CubeOutTransformer());
        dailyPagerAll.setCurrentItem(DailySales5.size() - 1);
        pDialog.dismiss();
    }

    public void fetchData() {
        pDialog.show();
        String path = "depts/sales/dailysales";
        myRef = database.getReference(path);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                jsonString = gson.toJson(dataSnapshot.getValue());
                objArray = new JsonParser().parse(jsonString).getAsJsonArray();

                getDailySalesForDays();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
            }
        });

    }

    public void getDailySalesForDays() {
        allUsersWeeklySales = new ArrayList<>();
        Calendar cal;
        for (int k = 0; k < objArray.size(); k++) {
            JsonElement element = objArray.get(k);
            if (!element.isJsonNull()) {
                List<DailySales> DailySalesList = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    DailySales dailySales = new DailySales();
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -i);
                    String Displayingdate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);


                    String Year = "" + cal.get(Calendar.YEAR);
                    String Month = "" + (cal.get(Calendar.MONTH) + 1);
                    String day = "" + cal.get(Calendar.DAY_OF_MONTH);
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

                    if (Month.length() == 1) {
                        Month = "0" + Month;
                    }
                    if (day.length() == 1) {
                        day = "0" + day;
                    }

                    String newDateFormt = Year + "-" + Month + "-" + day;


                    JsonObject obj = element.getAsJsonObject();
                    JsonElement object = obj.get(newDateFormt);

                    dailySales = gson.fromJson(object, DailySales.class);
                    if (dailySales == null) {
                        dailySales = new DailySales();
//                dailySales.setDate(Displayingdate);
                    }
                    dailySales.setDate(dayOftheWeek + " (" + Displayingdate + ")");
                    dailySales.setUser(getUserName(k));

                    if (USERID.equals("" + k)) {
                        DailySales5.add(dailySales);
                    }
                    DailySalesList.add(dailySales);
                }
                Collections.reverse(DailySalesList);
                allUsersWeeklySales.add(DailySalesList);
            }


        }
        generateSales();
    }

    public String getUserName(int id){
        String name = "unknown";
        for(User user : users)
        if(user.getId() == id){
            name = user.getName();

//            UserFName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
            break;
        }
        return name;
    }

    public void fetchUsers(){
        SweetAlertDialog pDialog = new SweetAlertDialog(CashInActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    User u = postSnapshot.getValue(User.class);
                    if (u.getUsername().equals(user.getEmail())) {
                        userdb = u;
                        USERID = "" + userdb.getId();
                    }
                    users.add(u);
                }
                pDialog.dismiss();
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
}
