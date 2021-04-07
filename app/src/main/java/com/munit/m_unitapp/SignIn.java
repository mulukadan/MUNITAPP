package com.munit.m_unitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button loginBtn;
    private EditText userNoEt;
    private EditText passwordEt;
    SweetAlertDialog pDialog;
    private TextView createAcc;
    String email ="",password ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        pDialog = new SweetAlertDialog(SignIn.this, SweetAlertDialog.PROGRESS_TYPE);
        userNoEt = findViewById(R.id.username);
        passwordEt = findViewById(R.id.password);
        userNoEt.clearFocus();
        passwordEt.clearFocus();
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userNoEt.getText().toString().trim().toLowerCase();
                password = passwordEt.getText().toString();

                int err = 0;
                if(email.length()<2){
                    userNoEt.setError("Invalid Username");
                    err= 1;
                }

                if(password.length()<3){
                    passwordEt.setError("Invalid Password");
                    err= 1;
                }
                if(err == 0) {
                    if(!email.contains("@")){
                        email = email +"@munit.com";
                    }
                    userNoEt.setText(email);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Logging in  ...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    Login();
                }
            }
        });

        createAcc = findViewById(R.id.createAcc);
        createAcc.setOnClickListener((view)->{
//            Intent intent = new Intent(SignIn.this, CreateAccActivity.class);
//            finish();
//            startActivity(intent);
        });

    }

    public void Login( ){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            SaveInfo();
                            Intent dash = new Intent(getApplicationContext(),PinActivity.class);
                            finish();
                            startActivity(dash);
                            pDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            pDialog.dismiss();
                            new SweetAlertDialog(SignIn.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Login Failed")
                                    .setContentText("Wrong Username/Password!")
                                    .show();
//
                        }

                        // ...
                    }
                });
    }
    public void SaveInfo() {
        SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }
}