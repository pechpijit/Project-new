package com.comsci.project;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import mehdi.sakout.fancybuttons.FancyButton;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout layout;
    FancyButton btn_login, btn_register;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        broadcast_reciever();

        layout = (LinearLayout) findViewById(R.id.btnView);
        btn_login = (FancyButton) findViewById(R.id.btn_login);
        btn_register = (FancyButton) findViewById(R.id.btn_register);
        progressBar = (ProgressBar) findViewById(R.id.proView);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        SharedPreferences sp = getSharedPreferences("Preferences_project", Context.MODE_PRIVATE);
        boolean user = sp.getBoolean("login", false);

        if (user) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            layout.setVisibility(View.VISIBLE);
                        }
                    }, 3000);
        }

    }

    public void onSignupFailed() {
        try {
            progressDialog.cancel();
        } catch (Exception e) {

        }
    }

    private void broadcast_reciever() {
        BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, SignupActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

}
