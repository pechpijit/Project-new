package com.comsci.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.comsci.project.fragment.MainFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {
    Fragment selectFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFram("", 1);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        toolbar.setBackgroundColor(getResources().getColor(R.color.menu1));
                        break;
                    case R.id.tab_search:
                        toolbar.setBackgroundColor(getResources().getColor(R.color.menu3));
                        break;
                    case R.id.tab_top:
                        toolbar.setBackgroundColor(getResources().getColor(R.color.menu4));
                        break;
                    case R.id.tab_user:
                        toolbar.setBackgroundColor(getResources().getColor(R.color.menu5));
                        SharedPreferences sp = getSharedPreferences("Preferences_project", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putBoolean("login", false);
                        edit.commit();
                        startActivity(new Intent(MainActivity.this,SplashActivity.class));
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
//                Toast.makeText(getApplicationContext(), TabMessage.get(tabId, true), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setFram(String s,int page) {
        Log.d("HomeActivity", "setFram");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

//        SharedPreferences sp = getSharedPreferences("Preferences_project", Context.MODE_PRIVATE);
//        String url = sp.getString("url", "");

        switch (page) {
            case 1:
                selectFragment = new MainFragment();
                break;
        }

        ft.replace(R.id.content, selectFragment);
        ft.commit();
    }
}
