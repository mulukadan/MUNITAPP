package com.munit.m_unitapp;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

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

    private TextView dailyBtn, monthlyBtn, yealyBtn;
    SweetAlertDialog pDialog;
    //    PoolTableRecord record = new PoolTableRecord();
    public List<PoolTableRecord> records = new ArrayList<>();
    List<PoolTableRecord> monthlyRecords = new ArrayList<>();
    List<PoolTableRecord> yealyRecords = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_hist);
        getSupportActionBar().hide();

        pDialog = new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);

        sdialog = new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });
        poolRecordsRV = findViewById(R.id.poolRecordsRV);
        poolRecordsRV.setLayoutManager(new LinearLayoutManager(this));
        poolRecordsRV.smoothScrollToPosition(0);

        dailyBtn = findViewById(R.id.dailyBtn);
        monthlyBtn = findViewById(R.id.monthlyBtn);
        yealyBtn = findViewById(R.id.yealyBtn);

        dailyBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(records);
            dailyBtn.setBackgroundResource(R.color.colorPrimary);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);

        });
        monthlyBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(monthlyRecords);

            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.colorPrimary);
            yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
        });
        yealyBtn.setOnClickListener(v -> {
            pDialog.show();
            displayRecords(yealyRecords);

            dailyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
            yealyBtn.setBackgroundResource(R.color.colorPrimary);
        });

        fetchData();
    }

    public void fetchData() {
        pDialog.show();
        myRef = database.getReference("depts/pool/collections");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                records.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    PoolTableRecord record = postSnapshot.getValue(PoolTableRecord.class);
                    records.add(record);

                }
                PoolRecordsAdapter transactionsTypesAdapter = new PoolRecordsAdapter(PoolHistActivity.this, records);
                poolRecordsRV.setAdapter(transactionsTypesAdapter);
                groupRecords();

                dailyBtn.setBackgroundResource(R.color.colorPrimary);
                monthlyBtn.setBackgroundResource(R.color.gray_btn_bg_color);
                yealyBtn.setBackgroundResource(R.color.gray_btn_bg_color);

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

    public void displayRecords( List<PoolTableRecord> recordsToDisplay) {
        poolRecordsRV.setAdapter(null);
        PoolRecordsAdapter transactionsTypesAdapter = new PoolRecordsAdapter(PoolHistActivity.this, recordsToDisplay);
        poolRecordsRV.setAdapter(transactionsTypesAdapter);
        pDialog.dismiss();
    }

    public void groupRecords(){
        for (PoolTableRecord rec : records) {
            addOrCreateRec(rec);
        }
    }

    public void addOrCreateRec(PoolTableRecord rec) {
        //Group Monthly
        String monthYr = rec.getDate().substring(rec.getDate().indexOf("/") +1);
        boolean found = false;
        for (PoolTableRecord mrec : monthlyRecords) {
            if (mrec.getDate().equals(monthYr)) {
                mrec.setBiz1Total(mrec.getBiz1Total() + rec.getBiz1Total());
                mrec.setTableOneTotal(mrec.getTableOneTotal() + rec.getTableOneTotal());
                mrec.setTableTwoTotal(mrec.getTableTwoTotal() + rec.getTableTwoTotal());
                mrec.setTableThreeTotal(mrec.getTableThreeTotal() + rec.getTableThreeTotal());
                mrec.setTotal(mrec.getTotal() + rec.getTotal());
                found= true;
                break;
            }

        }
        if(!found) {
            PoolTableRecord newMrec = new PoolTableRecord();
            newMrec.setBiz1Total(rec.getBiz1Total());
            newMrec.setTableOneTotal(rec.getTableOneTotal());
            newMrec.setTableTwoTotal(rec.getTableTwoTotal());
            newMrec.setTableThreeTotal(rec.getTableThreeTotal());
            newMrec.setTotal(rec.getTotal());
            newMrec.setDate(monthYr);
            monthlyRecords.add(newMrec);
        }

        // Group Yealy
        String Yr = monthYr.substring(monthYr.indexOf("/") +1);
        found = false;
        for (PoolTableRecord mrec : yealyRecords) {
            if (mrec.getDate().equals(Yr)) {
                mrec.setBiz1Total(mrec.getBiz1Total() + rec.getBiz1Total());
                mrec.setTableOneTotal(mrec.getTableOneTotal() + rec.getTableOneTotal());
                mrec.setTableTwoTotal(mrec.getTableTwoTotal() + rec.getTableTwoTotal());
                mrec.setTableThreeTotal(mrec.getTableThreeTotal() + rec.getTableThreeTotal());
                mrec.setTotal(mrec.getTotal() + rec.getTotal());
                found= true;
                break;
            }

        }
        if(!found) {
            PoolTableRecord newMrec = new PoolTableRecord();
            newMrec.setBiz1Total(rec.getBiz1Total());
            newMrec.setTableOneTotal(rec.getTableOneTotal());
            newMrec.setTableTwoTotal(rec.getTableTwoTotal());
            newMrec.setTableThreeTotal(rec.getTableThreeTotal());
            newMrec.setTotal(rec.getTotal());
            newMrec.setDate(Yr);
            yealyRecords.add(newMrec);
        }

    }
}
