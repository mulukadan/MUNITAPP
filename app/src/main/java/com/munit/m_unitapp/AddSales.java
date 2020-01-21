package com.munit.m_unitapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.munit.m_unitapp.ADAPTERS.SalesCategoryAdapter;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.MODELS.SalesCategory;
import com.munit.m_unitapp.MODELS.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    String USERID;

    private TextView computerService;
    private TextView computerSales;
    private TextView photos;
    private TextView video;
    private TextView movies;
    private TextView games;
    private TextView Username;
    private TextView total;
    private ImageButton editCompSer, editCompSel, editphotos, editvideo, editMovies, editGames;

    String AmtFor = "";
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);
        getSupportActionBar().hide();

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
        photos = findViewById(R.id.photos);
        video = findViewById(R.id.video);
        movies = findViewById(R.id.movies);
        games = findViewById(R.id.games);
        total = findViewById(R.id.total);
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

        editphotos = findViewById(R.id.editphotos);
        editphotos.setOnClickListener(v -> {
            icon.setBackgroundResource(R.drawable.photos_icon);
            showDialog("photos", "Ksh. " + dailySales.getPhotos());
        });

        editvideo = findViewById(R.id.editvideo);
        editvideo.setOnClickListener(v -> {
            icon.setBackgroundResource(R.drawable.video_icon);
            showDialog("video", "Ksh. " + dailySales.getVideo());
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        String Year = date.substring(date.lastIndexOf("/") + 1);
        String Month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
        String day = date.substring(0, date.indexOf("/"));

        daysDate.setText(date);

        if (Month.length() == 1) {
            Month = "0" + Month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }

        final int[] Amount = {10};
        String newDateFormt = Year + "-" + Month + "-" + day;
        String path = "depts/sales/dailysales/" + userId + "/" + newDateFormt;
        myRef = database.getReference(path);
//        myRef = database.getReference("msg");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dailySales = dataSnapshot.getValue(DailySales.class);

                if (dailySales == null) {
                    dailySales = new DailySales();
                    dailySales.setDate(DateDisplaying);
                }

                computerService.setText("Ksh. " + dailySales.getComputer_service());
                computerSales.setText("Ksh. " + dailySales.getComputer_sales());
                photos.setText("Ksh. " + dailySales.getPhotos());
                video.setText("Ksh. " + dailySales.getVideo());
                movies.setText("Ksh. " + dailySales.getMovies());
                games.setText("Ksh. " + dailySales.getGames());
                total.setText("Ksh. " + dailySales.getTotal());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                int k = 2;
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
            case "photos":
                dailySales.setPhotos(amt);
                break;
            case "video":
                dailySales.setVideo(amt);
                break;
            case "movies":
                dailySales.setMovies(amt);
                break;
            case "games":
                dailySales.setGames(amt);
                break;
        }

        new firebase().addDailySale(USERID, dailySales);
    }
}
