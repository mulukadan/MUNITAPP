package com.munit.m_unitapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.munit.m_unitapp.API.Client;
import com.munit.m_unitapp.API.Service;
import com.munit.m_unitapp.DB.firebase;
import com.munit.m_unitapp.MODELS.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;

public class ViewUserActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    firebase db = new firebase();
    private FirebaseAuth mAuth;


    private ImageView back_arrow;
    private ImageView delete;
    private ImageView reset;
    private CircularImageView ProfilePic;
    Bundle extras;
    private User user = new User();
    List<User> users = new ArrayList<>();

    private TextView name;
    private TextView PhoneNo;
    private TextView userName;
    private TextView password;
    private ImageButton showPassword;
    private LinearLayout sendPwdLayout;

    SweetAlertDialog sdialog;

    private Calendar calendar;
    private int year, month, day;
    String todate;

    private String AdminEmail, AdminPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        getSupportActionBar().hide();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Gson gson = new Gson();
        extras = getIntent().getExtras();
        if (extras != null) {
            user = gson.fromJson(getIntent().getStringExtra("userJson"), User.class);
        } else {

        }

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        todate = day + "-" + month + "-" + year;

        GetUserInfo();
        fetchData();
        ProfilePic = findViewById(R.id.ProfilePic);
        Picasso.get().load(user.getImgUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(ProfilePic);

        sdialog = new SweetAlertDialog(ViewUserActivity.this, SweetAlertDialog.WARNING_TYPE);
        sdialog.setCancelable(false);


        back_arrow = findViewById(R.id.back_arrow);
        delete = findViewById(R.id.delete);
        reset = findViewById(R.id.reset);
        back_arrow.setOnClickListener((view) -> {
            finish();
        });


        sendPwdLayout = findViewById(R.id.sendPwdLayout);
        password = findViewById(R.id.password);
        showPassword = findViewById(R.id.showPassword);
        name = findViewById(R.id.name);
        name.setText("Name: " + user.getName());

        PhoneNo = findViewById(R.id.PhoneNo);
        PhoneNo.setText("Phone No: " + user.getPhoneNo());

        userName = findViewById(R.id.userName);
        userName.setText("Username: " + user.getUsername());

        delete.setOnClickListener(v -> {
            sdialog.setTitleText("Delete User?")
                    .setContentText("Are you sure you want to Delete: " + user.getName() + "?")
                    .setConfirmText("Delete")
                    .showCancelButton(true)
                    .setCancelClickListener(sweetAlertDialog -> {
                        sdialog.dismiss();
                    })
                    .setCancelText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
//                            school.getUsers().remove(user);
                            for (User u : users) {
                                if (u.getUsername().equals(user.getUsername())) {
                                    users.remove(u);
                                    break;
                                }
                            }
                            removeUserFromFirebase(user);
                        }
                    })
                    .show();
        });

        reset.setOnClickListener(v -> {
            sdialog.setTitleText("Send User Details?")
                    .setContentText("Are you sure you want to Share: " + user.getName() + " Details?")
                    .setConfirmText("Share")
                    .showCancelButton(true)
                    .setCancelClickListener(sweetAlertDialog -> {
                        sdialog.dismiss();
                    })
                    .setCancelText("Cancel")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        int Index7 = user.getPhoneNo().indexOf("7");
                        String Phone = "254" + user.getPhoneNo().substring(Index7);
                        String userFname = user.getName();
                        if (userFname.contains(" ")) {
                            userFname = userFname.substring(0, userFname.indexOf(" "));
                        }
                        String Msg = "Dear " + userFname + ", Your Login Details to M-Unit Messenger are: Username: " + user.getUsername() + " Password: " + user.getPassword();
                        SendSMS(Phone, Msg);

                    })
                    .show();
        });
        if (user.getUsername().substring(0, user.getUsername().indexOf("@")).equals("admin")) {
            delete.setVisibility(View.GONE);
        }

        showPassword.setOnClickListener(v -> {
            String pwd = password.getText().toString();
            if (pwd.contains("****")) {
                password.setText("Password: " + user.getPassword());
            } else {
                password.setText("Password: *********");
            }
        });


    }

    public void fetchData() {
        SweetAlertDialog pDialog = new SweetAlertDialog(ViewUserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        myRef = database.getReference("users");

        // Read from the database
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

                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
                pDialog.dismiss();

                // 3. Error message
                new SweetAlertDialog(ViewUserActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        });

    }

    private void removeUserFromFirebase(User user) {
        sdialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sdialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sdialog.setTitleText("Deleting Account ...")
                .setContentText("Please Wait...!");
        sdialog.showCancelButton(false);
        //Logout As Admin, Sign in as User, Delete UserAccount, then Log in As Admin Again
        FirebaseAuth.getInstance().signOut();
        SignIn(user.getUsername(), user.getPassword(), 1);

    }

    public void SignIn(String Email, String Password, int type) {
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                                                Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                                                updateUI(user);
                            if (type == 1) {
                                //LogedIn As User
                                DeleteUser();
                            } else {//Admin
                                sdialog
                                        .setTitleText("Done!")
                                        .setContentText("User Account Deleted Successfully")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(sweetAlertDialog -> {
                                            finish();
                                            sdialog.dismissWithAnimation();
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }


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

    public void DeleteUser() {

        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

        fbuser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User account deleted.");
                            db.saveUsers(users);
                            SignIn(AdminEmail, AdminPassword, 0);
                        }
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
                                .setContentText("User Account Details send Successfully")
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

    public void GetUserInfo() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        AdminEmail = sharedPref.getString("email", "");
        AdminPassword = sharedPref.getString("password", "");
    }
}
