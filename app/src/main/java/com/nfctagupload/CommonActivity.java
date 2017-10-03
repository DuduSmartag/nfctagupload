package com.nfctagupload;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CommonActivity extends AppCompatActivity {

    private static final String TAG=CommonActivity.class.getName();

    protected ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeProgressBar();
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void initializeProgressBar(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);
    }

    public void showProgressBar(){
        runOnUiThread(new Runnable(){
            public void run(){
                progressBar.show();
            }
        });
    }

    public void closeProgressBar(){
        runOnUiThread(new Runnable(){
            public void run(){
                progressBar.hide();
            }
        });
    }
}
