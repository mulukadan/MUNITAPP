package com.munit.m_unitapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.gson.Gson;
import com.munit.m_unitapp.DB.Firestore;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.User;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddSales extends AppCompatActivity {
    private ImageView back_arrow;
    private Calendar calendar;
    private int year, month, day;
    private String todate;
    User dbuser;
    private Dialog newSalesDialog;
    private ImageView CloseDialog;
    private TextView TopupTitle;
    private TextView initialAmt;
    private EditText amt;
    private Button SaveBtn;
    private ImageView icon, dateIcon;

    private TextView daysDate;
    String DateDisplaying;

    private DailySales dailySales;
    String dailysalesPath = "dailysales";
    String USERID;

    private TextView computerService;
    private TextView computerSales;
    private TextView photos;
    private TextView video;
    private TextView movies;
    private TextView games;
    private TextView Username;
    private TextView totalTV, TotaltillPay, totalCashPay ;
    private ImageButton editCompSer, editCompSel, editphotos, editvideo, editMovies, editGames;
    private LinearLayout editTillPayLL;
    private EditText tillPatED;
    private Button OkTillPayment, testBtn;
    private ImageButton edittillpayBtn;

    String AmtFor = "";
    Gson gson = new Gson();

    FirebaseFirestore firedb;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);
        getSupportActionBar().hide();

        firedb = FirebaseFirestore.getInstance();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        String userJson = extras.getString("userJson");

        dbuser = gson.fromJson(userJson, User.class);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "/" + month + "/" + year;
        DateDisplaying = todate;

        USERID = "" + dbuser.getId();
        newSalesDialog = new Dialog(this);
        newSalesDialog.setContentView(R.layout.new_sale_dialog);
        newSalesDialog.setCanceledOnTouchOutside(false);
        newSalesDialog.setCancelable(false);
        newSalesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        amt = newSalesDialog.findViewById(R.id.amt);
        TopupTitle = newSalesDialog.findViewById(R.id.TopupTitle);
        icon = newSalesDialog.findViewById(R.id.icon);
        initialAmt = newSalesDialog.findViewById(R.id.initialAmt);

        dateIcon = findViewById(R.id.dateIcon);
        dateIcon.setOnClickListener(v -> {
            setDate();
        });
        CloseDialog = newSalesDialog.findViewById(R.id.CloseDialog);
        CloseDialog.setOnClickListener(v -> {
            newSalesDialog.dismiss();
        });
        SaveBtn = newSalesDialog.findViewById(R.id.SaveBtn);

        SaveBtn.setOnClickListener(v -> {
            int amount = Integer.parseInt(amt.getText().toString());
            updateDailySale(amount);
            newSalesDialog.dismiss();
        });

        daysDate = findViewById(R.id.daysDate);


        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        computerService = findViewById(R.id.computerService);
        computerSales = findViewById(R.id.computerSales);
        movies = findViewById(R.id.movies);
        games = findViewById(R.id.games);
        totalTV = findViewById(R.id.total);
        Username = findViewById(R.id.Username);
        Username.setText(dbuser.getName());
        editCompSer = findViewById(R.id.editCompSer);
        editCompSer.setOnClickListener(v -> {
            icon.setBackgroundResource(R.drawable.compserv_icon);
            showDialog("computer_service", "Ksh. " + dailySales.getComputer_service());
        });

        editCompSel = findViewById(R.id.editCompSel);
        editCompSel.setOnClickListener(v -> {
            icon.setBackgroundResource(R.drawable.compsells_icon);
            showDialog("computer_sales", "Ksh. " + dailySales.getComputer_sales());
        });

        editMovies = findViewById(R.id.editMovies);
        editMovies.setOnClickListener(v -> {
            icon.setBackgroundResource(R.drawable.movie_icon);
            showDialog("movies", "Ksh. " + dailySales.getMovies());
        });

        editGames = findViewById(R.id.editGames);
        editGames.setOnClickListener(v -> {
            icon.setBackgroundResource(R.drawable.games_icon);
            showDialog("games", "Ksh. " + dailySales.getGames());
        });

        totalCashPay  = findViewById(R.id.totalCashPay);
        TotaltillPay  = findViewById(R.id.TotaltillPay);
        editTillPayLL  = findViewById(R.id.editTillPayLL);
        editTillPayLL.setVisibility(View.GONE);
        tillPatED  = findViewById(R.id.tillPatED);
        edittillpayBtn  = findViewById(R.id.edittillpayBtn);
        edittillpayBtn.setOnClickListener(v -> {
            tillPatED.setText("");
            tillPatED.setError(null);
            editTillPayLL.setVisibility(View.VISIBLE);
            edittillpayBtn.setVisibility(View.GONE);
        });
        OkTillPayment  = findViewById(R.id.OkTillPayment);
        OkTillPayment.setOnClickListener(v -> {
            String amt = tillPatED.getText().toString().trim();
            if (amt.length()>1){
                //Save
                AmtFor = "till";
                updateDailySale(Integer.parseInt(amt));
                tillPatED.setText("");
                editTillPayLL.setVisibility(View.GONE);
                edittillpayBtn.setVisibility(View.VISIBLE);
            }else {
                tillPatED.setError("Enter Amount");
            }
        });
        tillPatED.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") ) {
                    int total = dailySales.getTotal();

                    String amt = tillPatED.getText().toString().trim();
                    try{
                        int tillAmt = Integer.parseInt(amt);

                        if (tillAmt<total){
                            tillPatED.setError(null);
                            TotaltillPay.setText("Ksh. " + amt);
                            totalCashPay.setText("Ksh. " + (total-tillAmt));
                            tillPatED.setError(null);
                            OkTillPayment.setEnabled(true);

                        }else {
                            tillPatED.setError("Amount Exceeds the total");
                            OkTillPayment.setEnabled(false);
                        }

                    }catch (Exception e){
                        if(amt.length()>0){
                            tillPatED.setError("Invalid Amount");
                        }
                        TotaltillPay.setText("Ksh. " + dailySales.getMpesaTill());
                        totalCashPay.setText("Ksh. " + dailySales.getCashPayment());
                        OkTillPayment.setEnabled(false);
                    }

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            public void afterTextChanged(Editable s) {

            }
        });
        getDailySale(DateDisplaying, USERID);

    }

    @SuppressWarnings("deprecation")
    public void setDate() {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    myDateListener, year, month - 1, day);

            datePickerDialog.setCanceledOnTouchOutside(false);
            return datePickerDialog;
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

                    getDailySale(DateDisplaying, USERID);

                }
            };

    public void showDialog(String Dprname, String InitAmt) {
        TopupTitle.setText(Dprname);
        AmtFor = Dprname;
        initialAmt.setText(InitAmt);
        amt.setText("");
        newSalesDialog.show();
    }

    public void getDailySale(String date, String userId) {
//        Firestore firestore = new Firestore(AddSales.this);
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.show();

        daysDate.setText(date);
        String docId = date.replace("/", "-") + ":" + userId;
        DocumentReference docRef = firedb.collection(dailysalesPath).document(docId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
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
                    dailySales.setDate(DateDisplaying);
                }
                dailySales.setUserId(USERID);
                dailySales.setUserName(dbuser.getName());
                computerService.setText("Ksh. " + dailySales.getComputer_service());
                computerSales.setText("Ksh. " + dailySales.getComputer_sales());
                movies.setText("Ksh. " + dailySales.getMovies());
                games.setText("Ksh. " + dailySales.getGames());
                totalTV.setText("Ksh. " + dailySales.getTotal());
                TotaltillPay.setText("Ksh. " + dailySales.getMpesaTill());
                totalCashPay.setText("Ksh. " + dailySales.getCashPayment());

                pDialog.dismiss();
            }
        });
    }

    public void updateDailySale(int amt) {
        switch (AmtFor) {
            case "computer_service":
                dailySales.setComputer_service(amt);
                break;
            case "computer_sales":
                dailySales.setComputer_sales(amt);
                break;
//            case "photos":
//                dailySales.setPhotos(amt);
//                break;
//            case "video":
//                dailySales.setVideo(amt);
//                break;
            case "movies":
                dailySales.setMovies(amt);
                break;
            case "games":
                dailySales.setGames(amt);
                break;
            case "till":
                dailySales.setMpesaTill(amt);
                break;
        }

//        new firebase().addDailySale(USERID, dailySales);
        new Firestore(AddSales.this).addDailySale(dailySales);
    }
}
