package com.munit.m_unitapp.UI.POOL;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.ADAPTERS.PoolRecordsAdapter;
import com.munit.m_unitapp.DB.Firestore;
import com.munit.m_unitapp.MODELS.PoolRecordNew;
import com.munit.m_unitapp.MODELS.PoolTableRecord;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.TOOLS.GeneralMethods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private ImageView copyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_hist);
        getSupportActionBar().hide();

        pDialog = new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);

        sdialog = new SweetAlertDialog(PoolHistActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        copyBtn = findViewById(R.id.copyBtn);
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
        copyBtn.setOnClickListener(view -> {
            copyData();
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

    public void displayRecords(List<PoolTableRecord> recordsToDisplay) {
        poolRecordsRV.setAdapter(null);
        PoolRecordsAdapter transactionsTypesAdapter = new PoolRecordsAdapter(PoolHistActivity.this, recordsToDisplay);
        poolRecordsRV.setAdapter(transactionsTypesAdapter);
        pDialog.dismiss();
    }

    public void groupRecords() {
        for (PoolTableRecord rec : records) {
            addOrCreateRec(rec);
        }
    }

    public void addOrCreateRec(PoolTableRecord rec) {
        //Group Monthly
        String monthYr = rec.getDate().substring(rec.getDate().indexOf("/") + 1);
        boolean found = false;
        for (PoolTableRecord mrec : monthlyRecords) {
            if (mrec.getDate().equals(monthYr)) {
                mrec.setBiz1Total(mrec.getBiz1Total() + rec.getBiz1Total());
                mrec.setTableOneTotal(mrec.getTableOneTotal() + rec.getTableOneTotal());
                mrec.setTableTwoTotal(mrec.getTableTwoTotal() + rec.getTableTwoTotal());
                mrec.setTableThreeTotal(mrec.getTableThreeTotal() + rec.getTableThreeTotal());
                mrec.setTotal(mrec.getTotal() + rec.getTotal());
                found = true;
                break;
            }

        }
        if (!found) {
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
        String Yr = monthYr.substring(monthYr.indexOf("/") + 1);
        found = false;
        for (PoolTableRecord mrec : yealyRecords) {
            if (mrec.getDate().equals(Yr)) {
                mrec.setBiz1Total(mrec.getBiz1Total() + rec.getBiz1Total());
                mrec.setTableOneTotal(mrec.getTableOneTotal() + rec.getTableOneTotal());
                mrec.setTableTwoTotal(mrec.getTableTwoTotal() + rec.getTableTwoTotal());
                mrec.setTableThreeTotal(mrec.getTableThreeTotal() + rec.getTableThreeTotal());
                mrec.setTotal(mrec.getTotal() + rec.getTotal());
                found = true;
                break;
            }

        }
        if (!found) {
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

    public void copyData() {
        Toast.makeText(this, "Total Records: " + records.size(), Toast.LENGTH_SHORT).show();
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Copying data");
        pDialog.show();
        for (PoolTableRecord r : records) {
            double tableOneTotal = r.getTableOneTotal();
            double tableTwoTotal = r.getTableTwoTotal();
            double tableThreeTotal = r.getTableThreeTotal();
            String rDate = r.getDate();
            Date date1 = null;
            try {
                date1 = new SimpleDateFormat("dd/MM/yyyy").parse(rDate);
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
            int dateInt = (int) (date1.getTime() / 1000);
            String ruser = r.getUser();
            String remployee = r.getEmployee();

            PoolRecordNew t1 = new PoolRecordNew();
            t1.setPoolName("EK-1.1");
            t1.setAmount((int) tableOneTotal);
            t1.setPoolId("1639401074");
            t1.setId(dateInt);
            t1.setDate(rDate);
            t1.setEmployee(remployee);
            t1.setUser(ruser);
            t1.setYear_week(new GeneralMethods().getDateParts(rDate, "yy") + "" + new GeneralMethods().getWeekNumber(rDate));
            t1.setYear_month(new GeneralMethods().getDateParts(rDate, "yy") + new GeneralMethods().getDateParts(rDate, "MM"));
            t1.setYear(new GeneralMethods().getDateParts(rDate, "yy"));

            new Firestore(this).addPoolRecord(t1);


            PoolRecordNew t2 = new PoolRecordNew();
            t2.setPoolName("EK-1.2");
            t2.setAmount((int) tableTwoTotal);
            t2.setPoolId("1639401106");
            t2.setId(dateInt);
            t2.setDate(rDate);
            t2.setEmployee(remployee);
            t2.setUser(ruser);
            t2.setYear_week(new GeneralMethods().getDateParts(rDate, "yy") + "" + new GeneralMethods().getWeekNumber(rDate));
            t2.setYear_month(new GeneralMethods().getDateParts(rDate, "yy") + new GeneralMethods().getDateParts(rDate, "MM"));
            t2.setYear(new GeneralMethods().getDateParts(rDate, "yy"));

            new Firestore(this).addPoolRecord(t2);

            PoolRecordNew t3 = new PoolRecordNew();
            t3.setPoolName("EK-1.3");
            t3.setAmount((int) tableThreeTotal);
            t3.setPoolId("1639401406");
            t3.setId(dateInt);
            t3.setDate(rDate);
            t3.setEmployee(remployee);
            t3.setUser(ruser);
            t3.setYear_week(new GeneralMethods().getDateParts(rDate, "yy") + "" + new GeneralMethods().getWeekNumber(rDate));
            t3.setYear_month(new GeneralMethods().getDateParts(rDate, "yy") + new GeneralMethods().getDateParts(rDate, "MM"));
            t3.setYear(new GeneralMethods().getDateParts(rDate, "yy"));

            new Firestore(this).addPoolRecord(t3);

            Toast.makeText(this, "Record done: " + records.indexOf(r), Toast.LENGTH_SHORT).show();
            pDialog.setTitleText("Record " + records.indexOf(r) + "/ " + records.size() + " done!");

        }
        pDialog.dismiss();
    }

}
