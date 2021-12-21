package com.munit.m_unitapp.UI.POOL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.ADAPTERS.PoolsAdapter;
import com.munit.m_unitapp.ADAPTERS.UsersAdapter;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.PoolTable;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.UI.SYSUSERS.UsersActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolsListActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private FirebaseAuth mAuth;

    private PoolTable poolTable = new PoolTable();
    PoolsAdapter poolsAdapter;

    private Calendar calendar;
    private int year, month, day;
    String todate, DateDisplaying;

    private ImageView addPoolMenuBtn;
    private ImageView back_arrow, CloseDialog, calenderIcon;
    private EditText nameEt, costEt;
    private Spinner colorSpiner, locationSpiner;
    private TextView purchaseDate;
    private Button SaveBtn;

    SweetAlertDialog sdialog;
    RecyclerView poolsRV;
    List<PoolTable> pooltables = new ArrayList<>();

    private Dialog newPoolDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pools_list);

        getSupportActionBar().hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sdialog = new SweetAlertDialog(PoolsListActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);
//        GetUserInfo();

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        poolsRV = findViewById(R.id.poolsRV);
        poolsRV.setLayoutManager(new LinearLayoutManager(this));
        poolsRV.smoothScrollToPosition(0);

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

        calenderIcon.setOnClickListener((view) -> {
            setDate();
        });
        CloseDialog.setOnClickListener(v -> {
            newPoolDialog.dismiss();
        });
        SaveBtn.setOnClickListener(v -> {
            savePool();
        });

        addPoolMenuBtn = findViewById(R.id.addPoolMenuBtn);
        addPoolMenuBtn.setOnClickListener(v -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(PoolsListActivity.this, addPoolMenuBtn);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.add_pool, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.one){
                        nameEt.setText("");
                        costEt.setText("");
                        newPoolDialog.show();
                    }else {
                        Toast.makeText(PoolsListActivity.this,"Coming soon: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                    }
                    return true;
                }
            });

            popup.show();//showing popup menu
        });
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;
        purchaseDate.setText(DateDisplaying);
        fetchData();
    }

    private void savePool() {
        int error =0;
        String name = nameEt.getText().toString().trim().toUpperCase();
        if(name.length()<2){
            nameEt.setError("Enter valid name");
            error = 1;
        }

        String cost = costEt.getText().toString().trim();
        if(name.length()<2){
            costEt.setError("Enter valid Cost");
            error = 1;
        }

        String color = colorSpiner.getSelectedItem().toString();
        String location = locationSpiner.getSelectedItem().toString();
        String dateOfP = purchaseDate.getText().toString();

        if(error == 0){
            PoolTable poolTable = new PoolTable();
            poolTable.setId((int) (new Date().getTime()/1000));
            poolTable.setName(name);
            poolTable.setStatus("Active");
            poolTable.setLocation(location);
            poolTable.setCost(Integer.parseInt(cost));
            poolTable.setColor(color);
            poolTable.setDateOfPurchase(dateOfP);

//            pooltables.add(poolTable);
            db.savePoolTable(poolTable);
            newPoolDialog.dismiss();

        }


    }


    public void fetchData(){
        SweetAlertDialog pDialog = new SweetAlertDialog(PoolsListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("depts/pool/pooltables");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pooltables.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    PoolTable pool = postSnapshot.getValue(PoolTable.class);

                    pooltables.add(pool);
                }
                poolsAdapter = new PoolsAdapter(PoolsListActivity.this, pooltables);
                poolsRV.setAdapter(poolsAdapter);

                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(PoolsListActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

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
                }
            };
}