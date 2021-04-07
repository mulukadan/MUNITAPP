package com.munit.m_unitapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.PoolTableRecord;
import com.munit.m_unitapp.MODELS.User;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolHomeActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private Dialog NewSalesDialog;
    private ImageView CloseDialog, calenderIcon;
    private TextView Date, T1Last, T2Last, T3Last, LastAllTotal, NewAllTotal;
    private EditText T1Amt, T2Amt, T3Amt;
    private Button SaveBtn;

    private RelativeLayout newSales;
    private RelativeLayout History;

    FirebaseDatabase database;
    private DatabaseReference myRef;
    SweetAlertDialog pDialog;

    //    double Table1Total = 0;
//    double Table2Total = 0;
    private Calendar calendar;
    private int year, month, day;
    private String todate;
    private User dbuser;
    String DateDisplaying;
    PoolTableRecord record = new PoolTableRecord();
    PoolTableRecord Lastrecord = new PoolTableRecord();

    boolean DisplaySalesDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_home);
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        NewSalesDialog = new Dialog(this);
        NewSalesDialog.setCanceledOnTouchOutside(false);
        NewSalesDialog.setContentView(R.layout.new_sales_dialog);
        NewSalesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseDialog = NewSalesDialog.findViewById(R.id.CloseDialog);
        Date = NewSalesDialog.findViewById(R.id.Date);
        calenderIcon = NewSalesDialog.findViewById(R.id.calenderIcon);
        T1Last = NewSalesDialog.findViewById(R.id.T1Last);
        T2Last = NewSalesDialog.findViewById(R.id.T2Last);
        T3Last = NewSalesDialog.findViewById(R.id.T3Last);
        LastAllTotal = NewSalesDialog.findViewById(R.id.LastAllTotal);
        NewAllTotal = NewSalesDialog.findViewById(R.id.NewAllTotal);
        T1Amt = NewSalesDialog.findViewById(R.id.T1Amt);
        T2Amt = NewSalesDialog.findViewById(R.id.T2Amt);
        T3Amt = NewSalesDialog.findViewById(R.id.T3Amt);
        SaveBtn = NewSalesDialog.findViewById(R.id.SaveBtn);

        CloseDialog.setOnClickListener((view) -> {
            NewSalesDialog.dismiss();
        });

        calenderIcon.setOnClickListener((view) -> {
            setDate();
        });

        T1Amt.addTextChangedListener(new TextWatcher() {
            private String previousDigits, num;
            private boolean textChanged = false;

            @Override
            public void onTextChanged(CharSequence currentDigits, int start,
                                      int before, int count) {
                if (!(previousDigits.equalsIgnoreCase(currentDigits.toString()))) {
                    textChanged = true;
                    num = currentDigits.toString();
                    if (num.length() > 0) {
                        record.setTableOneTotal(Double.parseDouble(num));
                    } else {
                        record.setTableOneTotal(0);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                previousDigits = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textChanged) {
                    textChanged = false;
                    NewAllTotal.setText("Total: Ksh. " + String.valueOf(record.getTotal()));
                }
            }
        });
        T2Amt.addTextChangedListener(new TextWatcher() {
            private String previousDigits, num;
            private boolean textChanged = false;

            @Override
            public void onTextChanged(CharSequence currentDigits, int start,
                                      int before, int count) {
                if (!(previousDigits.equalsIgnoreCase(currentDigits.toString()))) {
                    textChanged = true;
                    num = currentDigits.toString();
                    if (num.length() > 0) {
                        record.setTableTwoTotal(Double.parseDouble(num));
                    } else {
                        record.setTableTwoTotal(0);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                previousDigits = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textChanged) {
                    textChanged = false;
                    NewAllTotal.setText("Total: Ksh. " + (record.getTotal()));
                }
            }
        });
        T3Amt.addTextChangedListener(new TextWatcher() {
            private String previousDigits, num;
            private boolean textChanged = false;

            @Override
            public void onTextChanged(CharSequence currentDigits, int start,
                                      int before, int count) {
                if (!(previousDigits.equalsIgnoreCase(currentDigits.toString()))) {
                    textChanged = true;
                    num = currentDigits.toString();
                    if (num.length() > 0) {
                        record.setTableThreeTotal(Double.parseDouble(num));
                    } else {
                        record.setTableThreeTotal(0);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                previousDigits = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textChanged) {
                    textChanged = false;
                    NewAllTotal.setText("Total: Ksh. " + (record.getTotal()));
                }
            }
        });

        SaveBtn.setOnClickListener(v -> {
            record.setDate(DateDisplaying);
            record.setTotal(record.getTotal());
            record.setBiz1Total(record.getBiz1Total());
            record.setUser("Mulu");
            new firebase().addPoolSale(record);
            NewSalesDialog.dismiss();
        });

        newSales = findViewById(R.id.newSales);
        newSales.setOnClickListener(v -> {
            DisplaySalesDialog = true;
            DateDisplaying = todate;
            getPoolSale(DateDisplaying);

        });
        History = findViewById(R.id.History);
        History.setOnClickListener(v -> {
            Intent intent = new Intent(this, PoolHistActivity.class);
            startActivity(intent);
        });
    }
    @SuppressWarnings("deprecation")
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
                    DateDisplaying = arg3 +"/" + (arg2+1) +"/" + arg1;
//                    Date.setText(DateDisplaying);
//                    DisplaySalesDialog = true;
                    getPoolSale(DateDisplaying);
                }
            };
    private void prepareNewSalesDialog() {
//        record = new firebase().getPoolSale(DateDisplaying);
        Date.setText(DateDisplaying);
        T1Amt.setText("" + record.getTableOneTotal());
        T2Amt.setText("" + record.getTableTwoTotal());
        T3Amt.setText("" + record.getTableThreeTotal());

        T1Last.setText("Last: Ksh. " + Lastrecord.getTableOneTotal());
        T2Last.setText("Last: Ksh. " + Lastrecord.getTableTwoTotal());
        T3Last.setText("Last: Ksh. " + Lastrecord.getTableThreeTotal());
        LastAllTotal.setText("Last Total: Ksh. " + Lastrecord.getTotal());

        NewAllTotal.setText("Total: Ksh. " + record.getTotal());
        if (DisplaySalesDialog) {
            NewSalesDialog.show();
            DisplaySalesDialog = false;
        }
        pDialog.dismiss();
    }

    public void getPoolSale(String date) {
        pDialog.show();
        String Year = date.substring(date.lastIndexOf("/") + 1);
        String Month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
        String day = date.substring(0, date.indexOf("/"));

        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        final int[] Amount = {10};
        String newDateFormt = Year + "-" + Month + "-" + day;
        String path = "depts/pool/collections/" + newDateFormt;
        myRef = database.getReference(path);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                record = dataSnapshot.getValue(PoolTableRecord.class);

                if (record == null) {
                    record = new PoolTableRecord();
                    record.setDate(date);
                }
                getlastRecord();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
            }
        });
    }

    public void getlastRecord(){
        String path = "depts/pool/collections/";
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference(path);
        Query lastQuery = dbReference.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Lastrecord = dataSnapshot(PoolTableRecord.class);
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Lastrecord.setTableOneTotal(ds.child("tableOneTotal").getValue(Double.class));
                    Lastrecord.setTableTwoTotal(ds.child("tableTwoTotal").getValue(Double.class));
                    Lastrecord.setTableThreeTotal(ds.child("tableThreeTotal").getValue(Double.class));
                    Lastrecord.setTotal(ds.child("total").getValue(Double.class));
                }

                prepareNewSalesDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage()); //Don't ignore potential errors!
            }
        });
    }
}
