package com.munit.m_unitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PinActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    FirebaseUser user;
    private FirebaseAuth mAuth;
    RadioButton pOne;
    RadioButton pTwo;
    RadioButton pThree;
    RadioButton pFour;

    TextView one;
    TextView two;
    TextView three;
    TextView four;
    TextView five;
    TextView six;
    TextView seven;
    TextView eight;
    TextView nine;
    TextView zero;

    TextView forgotPassword;
    TextView enterPinTxt;
    TextView usernameTv;

    RelativeLayout Delete;
    String inputPin = "",inputPinToConfirm = "", UserPin = "", firstname, uEmail, uPassword;
    private CircularImageView ProfilePic;
    private String ProfilePicURL;
    private Bitmap bitmap;
    // Database Helper
//    DbHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

//        getSupportActionBar().hide();
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        GetUserInfo();
//        db = new DbHelper(getApplicationContext());

        ProfilePic= findViewById(R.id.ProfilePic);
        String Username = "";
        if(user !=null){
            SetProfilePic();
            Username = user.getDisplayName();
            if(Username == null || Username.length()<1){
                Username=user.getEmail().substring(0, user.getEmail().indexOf("@"));
            }else {
                if(Username.contains(" ")){
                    Username = Username.substring(0, Username.indexOf(" "));
                }
            }
        }
        usernameTv = findViewById(R.id.username);

        usernameTv.setText("Welcome back, "+Username+"!");

        pOne = findViewById(R.id.pone);
        enterPinTxt = findViewById(R.id.enterPinTxt);
        pTwo = findViewById(R.id.ptwo);
        pThree = findViewById(R.id.pthree);
        pFour = findViewById(R.id.pfour);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        zero = findViewById(R.id.zero);
        forgotPassword = findViewById(R.id.forgotPin);
        Delete = findViewById(R.id.Delete);

        CheckLoginStage();

        zero.setOnClickListener((View v) -> {
            SetPin("0");
        });
        one.setOnClickListener((View v) -> {
            SetPin("1");
        });
        two.setOnClickListener((View v) -> {
            SetPin("2");
        });
        three.setOnClickListener((View v) -> {
            SetPin("3");
        });
        four.setOnClickListener((View v) -> {
            SetPin("4");
        });
        five.setOnClickListener((View v) -> {
            SetPin("5");
        });
        six.setOnClickListener((View v) -> {
            SetPin("6");
        });
        seven.setOnClickListener((View v) -> {
            SetPin("7");
        });
        eight.setOnClickListener((View v) -> {
            SetPin("8");
        });
        nine.setOnClickListener((View v) -> {
            SetPin("9");
        });
        Delete.setOnClickListener((View v) -> {
            SetPin("Del");
        });
        forgotPassword.setOnClickListener((View v) -> {
            new SweetAlertDialog(one.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Reset Your Pin?")
                    .setContentText("Are you Sure?")
                    .setConfirmText("Reset")
                    .showCancelButton(true)
                    .setCancelText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //Dismiss your alert here then you will be able to call it again
                            sweetAlertDialog.dismiss();
                            ResetUserDetails();
                            Intent Sign = new Intent(getApplicationContext(), SignIn.class);
                            finish();
                            startActivity(Sign);
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();
        });

    }

    private void CheckLoginStage() {
        if(UserPin.length()<4){
            enterPinTxt.setText("Set PIN");
            forgotPassword.setVisibility(View.GONE);
        }
    }

    public void SetPin(String pinDigit) {
        if (pinDigit.equals("Del")) {
            //Delete Last Character
            if (inputPin.length() > 0) {
                inputPin = inputPin.substring(0, inputPin.length() - 1);
                CheckPin();
            }
        } else {
            inputPin = inputPin + pinDigit;
            CheckPin();
        }
    }

    public void CheckPin() {
        int PinLength = inputPin.length();
        if (PinLength == 0) {
            pOne.setChecked(false);
            pTwo.setChecked(false);
            pThree.setChecked(false);
            pFour.setChecked(false);

        } else if (PinLength == 1) {
            pOne.setChecked(true);
            pTwo.setChecked(false);
            pThree.setChecked(false);
            pFour.setChecked(false);
        } else if (PinLength == 2) {
            pOne.setChecked(true);
            pTwo.setChecked(true);
            pThree.setChecked(false);
            pFour.setChecked(false);
        } else if (PinLength == 3) {
            pOne.setChecked(true);
            pTwo.setChecked(true);
            pThree.setChecked(true);
            pFour.setChecked(false);
        } else if (PinLength == 4) {
            pOne.setChecked(true);
            pTwo.setChecked(true);
            pThree.setChecked(true);
            pFour.setChecked(true);
            LogIN();
        }

    }

    private void LogIN() {
        if(UserPin.length()<4){//Create Pin
            if(inputPinToConfirm.length()<4){
                inputPinToConfirm = inputPin;
                inputPin = "";
                enterPinTxt.setText("Confirm PIN");
                CheckPin();
            }else {
                if(inputPin.equals(inputPinToConfirm)){
                    SaveUserPin();
                    UserPin = inputPin;
                    new SweetAlertDialog(one.getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("PIN Set Successfully")
                            .setContentText("Now Log In with the new Pin")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    //Dismiss your alert here then you will be able to call it again
                                    sweetAlertDialog.dismiss();
                                    inputPin = "";
                                    enterPinTxt.setText("Please Enter your secure PIN");
                                    forgotPassword.setVisibility(View.VISIBLE);
                                    CheckPin();
                                }
                            })
                            .show();

                }else {
                    new SweetAlertDialog(pOne.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Pin Set Failed")
                            .setContentText("Pin Mismatch, Please Try Again")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    //Dismiss your alert here then you will be able to call it again
                                    sweetAlertDialog.dismiss();
                                    inputPin = "";
                                    inputPinToConfirm = "";
                                    CheckPin();
                                }
                            })
                            .show();
                }
            }

        }else {//Has Pin, Login
            if (UserPin.equals(inputPin)) {
                if (user != null) {
                    // User is signed in
                    ToDashBOrNotifications();
                } else {
                    // No user is signed in
                    SignInUser();
                }


            } else {
                new SweetAlertDialog(pOne.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Login Failed")
                        .setContentText("Wrong Pin")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                //Dismiss your alert here then you will be able to call it again
                                sweetAlertDialog.dismiss();
                                inputPin = "";
                                CheckPin();
                            }
                        })
                        .show();

            }
        }

    }

    private void SignInUser() {
        mAuth.signInWithEmailAndPassword(uEmail, uPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            ToDashBOrNotifications();

                        } else {
                            // If sign in fails, display a message to the user.
                            new SweetAlertDialog(PinActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong! Please Login")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        ResetUserDetails();
                                        Intent Sign = new Intent(getApplicationContext(), SignIn.class);
                                        finish();
                                        startActivity(Sign);
                                    })
                                    .show();
                        }

                        // ...
                    }
                });

    }

    public void ToDashBOrNotifications(){
        Intent Dash = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(Dash);
    }

    public void ResetUserDetails() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("PIN", "");
        editor.putString("email", "");
        editor.putString("password", " ");
        editor.apply();
    }
    public void SaveUserPin() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("PIN", inputPin);
        editor.apply();
    }
    private void SetProfilePic() {
        Picasso.get().load(user.getPhotoUrl()).memoryPolicy(MemoryPolicy.NO_STORE).centerCrop().fit().into(ProfilePic);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public void GetUserInfo() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        firstname = sharedPref.getString("firstname", "User");
//        lastname = sharedPref.getString("lastname", "");
        UserPin = sharedPref.getString("PIN", "");
        ProfilePicURL = sharedPref.getString("ProfilePicURL", "");
        uEmail = sharedPref.getString("email", "");
        uPassword = sharedPref.getString("password", "");
    }

}