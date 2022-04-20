package com.munit.m_unitapp;

import android.content.Intent;
import android.graphics.Color;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.UI.CARWASH.CarWashMainActivity;
import com.munit.m_unitapp.UI.CYBER.CashInActivity;
import com.munit.m_unitapp.UI.HSMS.HSMSActivationActivity;
import com.munit.m_unitapp.UI.PAYROLL.PayrollActivity;
import com.munit.m_unitapp.UI.POOL.PoolHomeActivity;
import com.munit.m_unitapp.UI.SYSUSERS.SettingsActivity;
import com.munit.m_unitapp.UI.SYSUSERS.UsersActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View DrawerHeader;
    private TextView NotificationCount;
    private CircularImageView ProfilePic, ProfilePicDrw;
    private TextView UserName;
    private TextView adminSwitch;
    private String UserType;
    FirebaseUser user;
    FirebaseFirestore firedb;
    String Username;
    public List<User> users = new ArrayList<>();
    public User userdb = new User();
    private String UserFName;
    private TextView GreetingTv;

    private TextView DrawerUsername;
    private MenuItem payrollItem;
    Calendar c = Calendar.getInstance();

    private RelativeLayout sales,pool, logo, newsPapers, carwash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firedb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firedb.setFirestoreSettings(settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserFName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        if (!UserFName.equals("admin")) {
            UserType = "user";
        } else {
            UserType = "Admin";
        }
        fetchData();
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation);
        DrawerHeader = navigationView.inflateHeaderView(R.layout.header);
        DrawerUsername = DrawerHeader.findViewById(R.id.DrawerUsername); //R.id.DrawerShopname
        ProfilePicDrw = DrawerHeader.findViewById(R.id.ProfilePicDrw); //R.id.DrawerShopname
        ProfilePic = findViewById(R.id.ProfilePic);
        Picasso.get().load(user.getPhotoUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(ProfilePic);
//        Picasso.get().load(user.getPhotoUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(ProfilePicDrw);

        UserName = findViewById(R.id.username);
        Username = user.getDisplayName();
        if (Username == null || Username.length() < 1) {
            Username = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        }
        UserName.setText(Username.toUpperCase());
        GreetingTv = findViewById(R.id.greetings);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String Greetings = "";

        if (timeOfDay >= 0 && timeOfDay < 12) {
            Greetings = "Good Morning!";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            Greetings = "Good Afternoon!";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            Greetings = "Good Evening!";
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            Greetings = "Good Night!";
        }

        GreetingTv.setText(Greetings);
        setupDrawerContent(navigationView);

        adminSwitch = findViewById(R.id.adminSwitch);
        adminSwitch.setOnClickListener((view) -> {
            if(user.getEmail().contains("admin")){
                Intent intent = new Intent(MainActivity.this, HSMSActivationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        sales = findViewById(R.id.sales);
        sales.setOnClickListener((view) -> {
            Intent intent = new Intent(MainActivity.this, CashInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        carwash = findViewById(R.id.carwash);
        carwash.setOnClickListener((view) -> {
            Intent intent = new Intent(MainActivity.this, CarWashMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });


        newsPapers = findViewById(R.id.newsPapers);
        newsPapers.setOnClickListener((view) -> {
//            Intent intent = new Intent(MainActivity.this, CashInActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);

        });

        pool = findViewById(R.id.pool);
        pool.setOnClickListener((view) -> {
            if(userdb.getLevel()<3){
                Intent intent = new Intent(MainActivity.this, PoolHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else {
                Toast.makeText(MainActivity.this,"Comin Soon!",Toast.LENGTH_SHORT).show();
            }

        });
        logo = findViewById(R.id.logo);
        logo.setOnClickListener((view) -> {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("UserType", UserType);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Settingsmenu:
                Intent Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(Settings);
                break;
            case R.id.payRoll:
                if(userdb.getLevel()<3){
                    Intent payroll = new Intent(getApplicationContext(), PayrollActivity.class);
                    startActivity(payroll);
                }else {
                    Toast.makeText(this, "Comming Soon!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.home:
                finish();
                startActivity(getIntent());
                break;
//
//            case R.id.topupHist:
//                Intent intent = new Intent(getApplicationContext(), PaymentHistActivity.class);
//                intent.putExtra("SchoolId", SchoolId);
//                intent.putExtra("UserFName", UserFName);
//                startActivity(intent);
//                break;
//            case R.id.outboxHist:
//                Intent outboxHistintent = new Intent(getApplicationContext(), SMSHistoryActivity.class);
//                outboxHistintent.putExtra("SchoolId", SchoolId);
//                outboxHistintent.putExtra("UserFName", UserFName);
//                startActivity(outboxHistintent);
//                break;
//            case R.id.ShareApp:
//                launchPhonePicker();
//                break;
            case R.id.Logoutmenu:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exit M-Unit Messenger?")
                        .setContentText("Are you sure you want to Exit!")
                        .setCancelText("No")
                        .setConfirmText("Exit")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(sweetAlertDialog -> {
                            finish();
                        })
                        .show();
                break;
            default:
        }
        drawerLayout.closeDrawers();
    }

    public void fetchData(){
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Read from the database

        myRef = database.getReference("users");
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    User user = postSnapshot.getValue(User.class);
//                      if(room_ic.getId().equals("l Room 12"))
                    users.add(user);
//                    Rooms.add(room_ic);
                }
                for (User u : users) {
                    if (u.getUsername().equals(user.getEmail())) {
                        userdb = u;
                        break;
                    }
                }
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });
    }

}
