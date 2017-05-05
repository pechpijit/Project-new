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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText txtUsername;
    EditText _passwordText,_conpasswordText;
    FancyButton btn_register;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        txtUsername = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _conpasswordText = (EditText) findViewById(R.id.input_con_password);
        btn_register = (FancyButton) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });



    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        btn_register.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        String username = txtUsername.getText().toString().toLowerCase().trim();
        String password = _passwordText.getText().toString().trim();

//        new ConnectAPI().Register(
//                SignupActivity.this,
//                firstname,
//                lastname,
//                email,
//                phone,
//                username,
//                password,
//                refreshedToken
//        );

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess("","");
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess(String json,String url) {
        SharedPreferences sp = getSharedPreferences("Preferences_project", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("login", true);
        edit.commit();
        Intent intent = new Intent("finish_activity");
        sendBroadcast(intent);
        btn_register.setEnabled(true);
        setResult(RESULT_OK, null);
        startActivity(new Intent(this,MainActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        new InsertUser().ConverUserDetail(SignupActivity.this,json,url);
    }

    public void onSignupFailed() {
        try {
            progressDialog.cancel();
        } catch (Exception e) {

        }
        btn_register.setEnabled(true);
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