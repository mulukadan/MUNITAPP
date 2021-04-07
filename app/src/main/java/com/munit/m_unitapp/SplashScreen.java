package com.munit.m_unitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    Animation fromBottom;
    Animation fromTop;
    Animation fade;
    ImageView logo;
    TextView slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
//        getSupportActionBar().hide();

        logo = findViewById(R.id.logo);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        logo.setAnimation(fromTop);

        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        logo.setAnimation(fade);

        slogan = findViewById(R.id.slogan);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        slogan.setAnimation(fromBottom);

        LogoLauncher launcher = new LogoLauncher();
        launcher.start();
    }
    private class LogoLauncher extends Thread {

        public void run() {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String email = sharedPref.getString("email", " ");
            String password = sharedPref.getString("password", " ");

            if (email.length() > 5) {//Login, Existing User
                Intent Pin = new Intent(getApplicationContext(), PinActivity.class);
                finish();
                startActivity(Pin);
            } else {
                Intent intent = new Intent(SplashScreen.this, SignIn.class);
                finish();
                startActivity(intent);
            }
            SplashScreen.this.finish();
        }
    }
}