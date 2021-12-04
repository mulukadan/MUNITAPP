package com.munit.m_unitapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munit.m_unitapp.ADAPTERS.UsersAdapter;
import com.munit.m_unitapp.API.Client;
import com.munit.m_unitapp.API.Service;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.User;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;

public class UsersActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private FirebaseAuth mAuth;

    private User user = new User();

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton addUserBtn;
    private ImageView back_arrow;

    RecyclerView usersRV;

    private Dialog UserDialog;
    private ImageView CloseBillDialog;
    private Button SaveBtn;
    private TextView name;
    private EditText PhoneNo;
    private EditText UserName;
    private EditText password;
    private String AdminEmail, AdminPassword;
    private Spinner userLevelSpiner;

    SweetAlertDialog sdialog;
    String UserType;
    Bundle extras;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sdialog = new SweetAlertDialog(UsersActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);
        GetUserInfo();
        extras = getIntent().getExtras();
        if (extras != null) {
            UserType = getIntent().getStringExtra("UserType");
        } else {

        }

        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });

        floatingActionMenu = findViewById(R.id.fab);
        floatingActionMenu.setClosedOnTouchOutside(true);

        addUserBtn = findViewById(R.id.addUserBtn);

        usersRV = findViewById(R.id.usersRV);
        usersRV.setLayoutManager(new LinearLayoutManager(this));
        usersRV.smoothScrollToPosition(0);

        UserDialog = new Dialog(this);
        UserDialog.setCanceledOnTouchOutside(false);
        UserDialog.setContentView(R.layout.user_dialog);
        UserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CloseBillDialog = UserDialog.findViewById(R.id.CloseBillDialog);
        name = UserDialog.findViewById(R.id.name);
        PhoneNo = UserDialog.findViewById(R.id.PhoneNo);
        UserName = UserDialog.findViewById(R.id.UserName);
        userLevelSpiner = UserDialog.findViewById(R.id.userLevelSpiner);
        password = UserDialog.findViewById(R.id.password);
        SaveBtn = UserDialog.findViewById(R.id.SaveBtn);

        CloseBillDialog.setOnClickListener((view) -> {
            UserDialog.dismiss();
        });

        SaveBtn.setOnClickListener((view) -> {
            String uname = name.getText().toString();
            String uPhoneNo = PhoneNo.getText().toString().trim();
            String uUserName = UserName.getText().toString().trim().toLowerCase();
            String upassword = password.getText().toString();
            int error = 0;
            if (uname.trim().length() < 2) {
                name.setError("Enter Name");
                error = 1;
            }

            int Index7 = uPhoneNo.indexOf("7");
            if (Index7 > 0) {
                uPhoneNo = "0" + uPhoneNo.substring(Index7);
                if (uPhoneNo.trim().length() != 10) {
                    PhoneNo.setError("Enter Valid Number");
                    error = 1;
                }
            } else {
                PhoneNo.setError("Enter Valid Number");
                error = 1;
            }

            if (uUserName.length() < 3) {
                UserName.setError("Enter Username");
                error = 1;
            }
            if (UserExists(uUserName) == 1) {
                UserName.setError("Username taken");
                error = 1;
            }
            if (upassword.trim().length() < 6) {
                password.setError("At least 6 characters");
                error = 1;
            }

            if (error == 0) {
                user.setName(uname);
                user.setPhoneNo(uPhoneNo);
                String email = uUserName + "@munit.com";
                user.setUsername(email);
                user.setPassword(upassword);
                user.setId(Integer.parseInt(users.size()+uPhoneNo.substring(5,7)));
                user.setActive(true);
                user.setLevel( userLevelSpiner.getSelectedItemPosition()+1);

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
                                users.add(user);
                                addUserToFirebase(user);
                            }
                        })
                        .show();

            }


        });

        addUserBtn.setOnClickListener((view) -> {
            floatingActionMenu.close(true);
            UserDialog.show();
        });

        if (!UserType.equals("Admin")) {
            floatingActionMenu.setVisibility(View.GONE);
        }

        fetchData();
    }

    private void addUserToFirebase(User user) {
        sdialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sdialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sdialog.setTitleText("Creating Account ...")
                .setContentText("Saving new User Details!");

        sdialog.showCancelButton(false);
        mAuth.createUserWithEmailAndPassword(user.getUsername(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Sign out the new User and Re-SignIn the Admin

                            db.saveUsers(users);
                            int Index7 = user.getPhoneNo().indexOf("7");
                            String Phone = "254" + user.getPhoneNo().substring(Index7);
                            String userFname = user.getName();
                            if(userFname.contains(" ")){
                                userFname = userFname.substring(0, userFname.indexOf(" "));
                            }

                            String Msg = "Dear " + userFname + ", Your Login Details to M-Unit Messenger are: Username: " + user.getUsername() + " Password: " + user.getPassword();
                            SendSMS(Phone, Msg);
                            FirebaseAuth.getInstance().signOut();
                            ReSignInAdmin();

                        } else {
                            // If sign in fails, display a message to the user.
                            sdialog.setTitleText("Oops..Account Creation Failed!")
                                    .setContentText(" " + task.getException())
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }

                        // ...
                    }
                });
    }

    public void fetchData(){
        SweetAlertDialog pDialog = new SweetAlertDialog(UsersActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Read from the database
        myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String Key = postSnapshot.getKey();
                    User user = postSnapshot.getValue(User.class);
//                      if(room_ic.getId().equals("l Room 12"))
                    users.add(user);
//                    Rooms.add(room_ic);

                }
                UsersAdapter adapter = new UsersAdapter(UsersActivity.this, users, UserType);
                usersRV.setAdapter(adapter);
                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(UsersActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }

    public void ReSignInAdmin() {
        mAuth.signInWithEmailAndPassword(AdminEmail, AdminPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                                                Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                                                updateUI(user);
                            sdialog
                                    .setTitleText("Account Created Successfully!")
                                    .setContentText("Use details in SMS received to login")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        UserDialog.dismiss();
                                        sdialog.dismissWithAnimation();
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        } else {
                            // If sign in fails, display a message to the user.
//                                                Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(UsersActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                                                updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void SendSMS(String PhoneNo, String Msg) {
        sdialog.showCancelButton(false)
                .setContentText("Sending User Details .... ")
                .getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sdialog.setCancelable(false);
        sdialog.setTitleText("Please wait.....");
        sdialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);


        try {
            Service apiService = Client.SendSMS(PhoneNo, Msg).create(Service.class);
            Call<String> call = apiService.getRequestResponse();
            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> Response) {
                    if (Response.isSuccessful()) {//
                        sdialog
                                .setTitleText("Done!")
                                .setContentText("New User Account Details send to the User Successfully")
                                .setConfirmText("OK")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    finish();
                                    sdialog.dismissWithAnimation();
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    } else {

                        Toast.makeText(getApplicationContext(), "Error Fecthing Data!\nPlease try Again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error Fecthing Data!\n Check your  Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {

//            Log.d("Error: " , e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int UserExists(String username) {
        int Found = 0;

        for (User user : users) {

            if (user.getName().equals(username)) {
                Found = 1;
                break;
            }
        }
        return Found;
    }

    public void GetUserInfo() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        AdminEmail = sharedPref.getString("email", "");
        AdminPassword = sharedPref.getString("password", "");
    }
}
