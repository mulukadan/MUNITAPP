package com.munit.m_unitapp.UI.SYSUSERS;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.munit.m_unitapp.R;
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

    private Dialog UserDialog;
    private ImageView CloseBillDialog;
    private Button SaveBtn;
    private TextView name;
    private EditText PhoneNo;
    private EditText UserName;
    private EditText password;
    private String AdminEmail, AdminPassword;
    private Spinner userLevelSpiner;

    private TextView uName;
    private TextView uPhoneNo;
    private TextView uUserName;
    private TextView uPassword;
    private ImageButton showPassword;
    private RelativeLayout sendPwdLayout;
    private TextView activateDeactivateUser, editBtn, userLevelTv, userStatusTV;

    SweetAlertDialog sdialog;

    private Calendar calendar;
    private int year, month, day;
    String todate;

    boolean active = true;


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
            String upName = name.getText().toString();
            String upPhoneNo = PhoneNo.getText().toString().trim();
            String upPassword = password.getText().toString();
            int error = 0;
            if (upName.trim().length() < 2) {
                name.setError("Enter Name");
                error = 1;
            }

            int Index7 = upPhoneNo.indexOf("7");
            if (Index7 > 0) {
                upPhoneNo = "0" + upPhoneNo.substring(Index7);
                if (upPhoneNo.trim().length() != 10) {
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
            if (upPassword.trim().length() < 6) {
                password.setError("At least 6 characters");
                error = 1;
            }

            if (error == 0) {
                String finalUpPhoneNo = upPhoneNo;
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
                                user.setName(upName);
                                user.setPhoneNo(finalUpPhoneNo);
                                user.setPassword(upPassword);
                                user.setActive(true);
                                user.setLevel(userLevelSpiner.getSelectedItemPosition() + 1);
                                updateFirebaseDb();
                            }
                        })
                        .show();
            }
        });

        sendPwdLayout = findViewById(R.id.sendPwdLayout);
        uPassword = findViewById(R.id.password);
        showPassword = findViewById(R.id.showPassword);
        uName = findViewById(R.id.name);
        uPhoneNo = findViewById(R.id.PhoneNo);
        uUserName = findViewById(R.id.userName);
        userLevelTv = findViewById(R.id.userLevelTv);
        userStatusTV = findViewById(R.id.userStatusTV);

        editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v -> {
            setUserDetails();
        });
        activateDeactivateUser = findViewById(R.id.activateDeactivateUser);

        if (user.getActive() != null) {
            active = user.getActive();
        } else {
            user.setActive(true);
        }

        if (user.getLevel() == 0) {
            user.setLevel(4);
        }

        activateDeactivateUser.setOnClickListener(v -> {
            String action = "Activate";
            if (active) {
                action = "Deactivate";
            }
            sdialog = new SweetAlertDialog(ViewUserActivity.this, SweetAlertDialog.WARNING_TYPE);
            sdialog.setTitleText(action + " User?")
                    .setContentText("Are you sure you want to " + action + ": " + user.getName() + "?")
                    .setConfirmText(action)
                    .showCancelButton(true)
                    .setCancelClickListener(sdialog -> {
                        sdialog.dismiss();
                    })
                    .setCancelText("Cancel")
                    .setConfirmClickListener(sDialog -> {
                        user.setActive(!user.getActive());
                        updateFirebaseDb();
                    })
                    .show();
        });
        editBtn = findViewById(R.id.editBtn);

        delete.setOnClickListener(v -> {
            sdialog = new SweetAlertDialog(ViewUserActivity.this, SweetAlertDialog.WARNING_TYPE);
            sdialog.setTitleText("Delete User?")
                    .setContentText("Are you sure you want to Delete: " + user.getName() + "?")
                    .setConfirmText("Delete")
                    .showCancelButton(true)
                    .setCancelClickListener(sdialog -> {
                        sdialog.dismiss();
                    })
                    .setCancelText("Cancel")
                    .setConfirmClickListener(sDialog -> {
                        for (User u : users) {
                            if (u.getUsername().equals(user.getUsername())) {
                                users.remove(u);
                                break;
                            }
                        }
                        removeUserFromFirebase(user);
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
                        String uName = user.getUsername().substring(0, user.getUsername().lastIndexOf("@"));

                        String Msg = "Dear " + userFname + ", Your Login Details to M-Unit App are: Username: " + uName + " and Password: " + user.getPassword();
                        SendSMS(Phone, Msg);

                    })
                    .show();
        });
        if (user.getUsername().substring(0, user.getUsername().indexOf("@")).equals("admin")) {
            delete.setVisibility(View.GONE);
        }

        showPassword.setOnClickListener(v -> {
            String pwd = uPassword.getText().toString();
            if (pwd.contains("****")) {
                uPassword.setText(user.getPassword());
            } else {
                uPassword.setText("********");
            }
        });

        updateUI();
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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
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
                                            sdialog.dismiss();
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

    private void updateFirebaseDb() {
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                users.remove(u);
                users.add(user);
                db.saveUsers(users);
                break;
            }
        }
        if (sdialog.isShowing()) {
            sdialog
                    .setTitleText("Done!")
                    .setContentText("User Updated Successfully")
                    .setConfirmText("OK")
                    .showCancelButton(false)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        updateUI();
                        sdialog.dismiss();
                    })
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
        if(UserDialog.isShowing()){
            UserDialog.dismiss();
        }
    }

    public void updateUI() {
        uName.setText(user.getName());
        uPhoneNo.setText(user.getPhoneNo());
        uUserName.setText(user.getUsername());
        if (user.getActive()) {
            activateDeactivateUser.setText("Deactivate");
            activateDeactivateUser.setBackgroundColor(Color.parseColor("#F3341A"));
            userStatusTV.setText("User Status: Active");
            userStatusTV.setTextColor(Color.parseColor("#1A790D"));

        } else {
            activateDeactivateUser.setText("Activate");
            activateDeactivateUser.setBackgroundColor(Color.parseColor("#1A790D"));
            userStatusTV.setText("User Status: Inactive");
            userStatusTV.setTextColor(Color.parseColor("#F3341A"));
        }
        userLevelTv.setText("User Level: " + user.getLevel());
    }

    public void setUserDetails() {
        name.setText(user.getName());
        PhoneNo.setText(user.getPhoneNo());
        UserName.setText(user.getUsername());
        UserName.setEnabled(false);
        password.setText(user.getPassword());
        userLevelSpiner.setSelection(user.getLevel() - 1);
        UserDialog.show();

    }

    public void GetUserInfo() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        AdminEmail = sharedPref.getString("email", "");
        AdminPassword = sharedPref.getString("password", "");
    }
}
