package com.nfctagupload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {

    private String status="approved";

    private volatile boolean isClosing=false;
    private String statusText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        if (getIntent()!=null && getIntent().hasExtra("status")){
            boolean bt=getIntent().getBooleanExtra("status",false);
            if (bt){
                status="approved";
            }else{
                status="denied";
            }
            if (getIntent().hasExtra("body")) {
                statusText = getIntent().getStringExtra("body");
            }
        }

        View vapprove=findViewById(R.id.statusapprovecontainer);
        View vdenied=findViewById(R.id.statusdenycontainer);
        TextView txtstatustext=(TextView)findViewById(R.id.txtstatustext);
        if (status.compareTo("approved")==0){
            vapprove.setVisibility(View.VISIBLE);
            vdenied.setVisibility(View.GONE);
            txtstatustext.setVisibility(View.GONE);
        }else{
            vapprove.setVisibility(View.GONE);
            vdenied.setVisibility(View.VISIBLE);
        }
        txtstatustext.setText(statusText);
        Thread thr=new Thread(){
            public void run(){
                try{
                    Thread.sleep(AppCache.config.closeInterval*1000);
                }catch(Exception ex){}

                if (!isClosing){
                    isClosing=true;
                }
                runOnUiThread(new Runnable(){
                    public void run(){
                        finish();
                    }
                });
            }
        };
        thr.start();
    }

    @Override
    public void onDestroy(){
        isClosing=true;
        super.onDestroy();
    }

    public void closeClick(View v){
        isClosing=true;
        finish();
    }
}
