package com.munit.m_unitapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.ADAPTERS.HSMSObjsAdapter;
import com.munit.m_unitapp.MODELS.HSMSObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HSMSActivationActivity extends AppCompatActivity {
    private ImageView back_arrow;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    private Dialog HSMSobjDialog;
    private ImageView CloseBillDialog;
    private Button SaveBtn;
    private EditText name;
    private EditText InstallDate;
    private EditText cost;
    SweetAlertDialog sdialog;


    RecyclerView HsmsObjsRV;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton addUserBtn;

    List<HSMSObject> HSMSObjs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsmsactivation);
        getSupportActionBar().hide();

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        sdialog = new SweetAlertDialog(HSMSActivationActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);

        HsmsObjsRV = findViewById(R.id.HsmsObjsRV);
        HsmsObjsRV.setLayoutManager(new LinearLayoutManager(this));
        HsmsObjsRV.smoothScrollToPosition(0);

        HSMSobjDialog = new Dialog(this);
        HSMSobjDialog.setCanceledOnTouchOutside(false);
        HSMSobjDialog.setContentView(R.layout.hsms_dialog);
        HSMSobjDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseBillDialog = HSMSobjDialog.findViewById(R.id.CloseBillDialog);
        name = HSMSobjDialog.findViewById(R.id.name);
        cost = HSMSobjDialog.findViewById(R.id.cost);
        InstallDate = HSMSobjDialog.findViewById(R.id.InstallDate);
        SaveBtn = HSMSobjDialog.findViewById(R.id.SaveBtn);

        CloseBillDialog.setOnClickListener((view) -> {
            HSMSobjDialog.dismiss();
        });

        SaveBtn.setOnClickListener((view) -> {
            HSMSObject hsmsObject = new HSMSObject();
            int error = 0;
            String uname = name.getText().toString().trim();
            int uCost = 0;
            String UcostString = cost.getText().toString().trim();
            if (UcostString.trim().length() < 1) {
                cost.setError("Enter AMount");
                error = 1;
            } else {
                uCost = Integer.parseInt(UcostString);
            }

            String uInstallDate = InstallDate.getText().toString().trim();
            if (uname.trim().length() < 2) {
                name.setError("Enter Name");
                error = 1;
            }

            if (error == 0) {
                uname = uname.toUpperCase();
                hsmsObject.setSchoolName(uname);
                hsmsObject.setInstallDate(uInstallDate);
                String initials = "";  //KAKSE for KAKUKU SECONDARY SCHOOL
                initials = uname.substring(0, 3) + uname.substring((uname.indexOf(" ") + 1), (uname.indexOf(" ") + 3));
                hsmsObject.setInitials(initials);
                hsmsObject.setCost(uCost);

                sdialog.setTitleText("Save User?")
                        .setContentText("Are you sure you want to save!")
                        .setConfirmText("Save")
                        .showCancelButton(true)
                        .setCancelClickListener(sweetAlertDialog -> {
                            sdialog.dismiss();
                        })
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
//                                users.add(user);
                                addHSMSToFirebase(hsmsObject);
                            }
                        })
                        .show();
            }


        });

        floatingActionMenu = findViewById(R.id.fab);
        floatingActionMenu.setClosedOnTouchOutside(true);
        addUserBtn = findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener((view) -> {
            floatingActionMenu.close(true);
            HSMSobjDialog.show();
        });
        fetchData();
    }

    public void fetchData() {
        SweetAlertDialog pDialog = new SweetAlertDialog(HSMSActivationActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("depts/hsms/clients");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HSMSObjs.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    HSMSObject hsmsObject = postSnapshot.getValue(HSMSObject.class);
                    HSMSObjs.add(hsmsObject);

                }
                HSMSObjsAdapter adapter = new HSMSObjsAdapter(HSMSActivationActivity.this, HSMSObjs);
                HsmsObjsRV.setAdapter(adapter);
                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(HSMSActivationActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }

    private void addHSMSToFirebase(HSMSObject hsmsObject) {
        sdialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sdialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sdialog.setTitleText("Creating Account ...")
                .setContentText("Saving new User Details!");

        sdialog.showCancelButton(false);

        HSMSObjs.add(hsmsObject);
        myRef = database.getReference("depts/hsms/clients");
        myRef.setValue(HSMSObjs);
        sdialog.dismiss();
        HSMSobjDialog.dismiss();

    }
}
