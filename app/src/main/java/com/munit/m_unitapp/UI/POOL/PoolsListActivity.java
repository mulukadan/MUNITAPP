package com.munit.m_unitapp.UI.POOL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolsListActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private FirebaseAuth mAuth;

    private PoolTable poolTable = new PoolTable();
    PoolsAdapter poolsAdapter;

    private FloatingActionMenu fab;
    private FloatingActionButton addPoolBtn;
    private ImageView back_arrow;
    SweetAlertDialog sdialog;
    RecyclerView poolsRV;
    List<PoolTable> pooltables = new ArrayList<>();
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

        fab = findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);

        addPoolBtn = findViewById(R.id.addPoolBtn);
        addPoolBtn.setOnClickListener(v -> {
            fab.close(true);

            PoolTable poolTable = new PoolTable();
            poolTable.setName("test3");
            poolTable.setId(3);
            poolTable.setAge(5);
            poolTable.setColor("Green");
            poolTable.setCost(68000);
            poolTable.setLocation("Kivingoni");
            poolTable.setStatus("Active");

            pooltables.add(poolTable);
            db.savePoolTables(pooltables);


        });

        poolsRV = findViewById(R.id.poolsRV);
        poolsRV.setLayoutManager(new LinearLayoutManager(this));
        poolsRV.smoothScrollToPosition(0);

        fetchData();


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
}