package com.munit.m_unitapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.ADAPTERS.PoolRecordsAdapter;
import com.munit.m_unitapp.MODELS.PoolTableRecord;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolHistActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    RecyclerView poolRecordsRV;
    private ImageView back_arrow;

    SweetAlertDialog sdialog;
    private FirebaseAuth mAuth;

//    PoolTableRecord record = new PoolTableRecord();
    public List<PoolTableRecord> records = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_hist);
        getSupportActionBar().hide();

        sdialog = new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });
        poolRecordsRV = findViewById(R.id.poolRecordsRV);
        poolRecordsRV.setLayoutManager(new LinearLayoutManager(this));
        poolRecordsRV.smoothScrollToPosition(0);
        fetchData();
    }

    public void fetchData(){
        SweetAlertDialog pDialog = new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();
        myRef = database.getReference("depts/pool/collections");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                records.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    PoolTableRecord record = postSnapshot.getValue(PoolTableRecord.class);
                    records.add(record);

                }
                PoolRecordsAdapter transactionsTypesAdapter = new PoolRecordsAdapter(PoolHistActivity.this, records);
                poolRecordsRV.setAdapter(transactionsTypesAdapter);
                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }
}
