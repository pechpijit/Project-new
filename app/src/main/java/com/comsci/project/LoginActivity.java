package com.comsci.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText txtUsername;
    EditText _passwordText;
    FancyButton btn_login;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        txtUsername = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        btn_login = (FancyButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });


    }

    public void Login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        btn_login.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = txtUsername.getText().toString().toLowerCase().trim();
        String password = _passwordText.getText().toString().trim();

//        new ConnectAPI().Login(
//                LoginActivity.this,
//                username,
//                password,
//                refreshedToken
//        );

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess("","");
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onLoginSuccess(String json,String url) {
        SharedPreferences sp = getSharedPreferences("Preferences_project", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("login", true);
        edit.commit();
        Intent intent = new Intent("finish_activity");
        sendBroadcast(intent);
        btn_login.setEnabled(true);
        setResult(RESULT_OK, null);
        startActivity(new Intent(this,MainActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        new InsertUser().ConverUserDetail(LoginActivity.this,json,url);
    }

    public void onSignupFailed() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
        btn_login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = txtUsername.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 6) {
            txtUsername.setError("at least 6 characters");
            valid = false;
        } else {
            txtUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
