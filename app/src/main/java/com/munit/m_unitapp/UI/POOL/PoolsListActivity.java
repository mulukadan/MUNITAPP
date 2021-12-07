package com.munit.m_unitapp.UI.POOL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.PoolTable;
import com.munit.m_unitapp.MODELS.User;
import com.munit.m_unitapp.R;
import com.munit.m_unitapp.UI.SYSUSERS.UsersActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoolsListActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private FirebaseAuth mAuth;

    private PoolTable poolTable = new PoolTable();

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton addPoolBtn;
    private ImageView back_arrow;
    SweetAlertDialog sdialog;
    RecyclerView poolsRV;

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

        floatingActionMenu = findViewById(R.id.fab);
        floatingActionMenu.setClosedOnTouchOutside(true);


    }
}